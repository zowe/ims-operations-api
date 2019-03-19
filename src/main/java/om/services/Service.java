/**
 *  Copyright IBM Corporation 2018, 2019
 */

package om.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.ims.connect.Cmderr;
import com.ibm.ims.connect.ImsConnectApiException;
import com.ibm.ims.connect.Mbr;
import com.ibm.ims.connect.Msgdata;
import com.ibm.ims.connect.Type2CmdResponse;

import om.exception.OmConnectionException;
import om.exception.OmException;
import om.fid.FIDManager;
import om.fid.FIDReader;
import om.message.OM_MESSAGE;
import om.message.OmCommandErrorMbr;
import om.message.OmMessageContext;
import om.result.OmResult.COMMAND_TYPE;
import om.result.OmResultSet;
import om.version.Version;
import utils.ArrayUtils;
import utils.ThreadHelper;

/**
 * Abstract class providing a foundation for creating new IMS Resource service interactions.
 * @author ddimatos
 *
 */
public class Service extends ThreadHelper{
	private static final Logger logger = LoggerFactory.getLogger(Service.class);
	private final static String NEW_LINE = System.getProperty("line.separator");
	private static final String OM_READY_CMD = "CMD(QUERY IMSPLEX TYPE(SCI,OM) SHOW(STATUS))";
	private static final String SPACES_COMMA = "    ,";

	protected Om om;

	FIDManager myFIDManager;

	protected String[] imsMbrs;
	private OmResultSet omResultSet = null;
	Version version = null;

	protected Service(Om om,Version version) {
		this.om = om;
		this.omResultSet = new OmResultSet();
		this.version = version;
	}

	/**
	 * Tests if the connection on the host side if SCI and OM are READY and ACTIVE. Both these 
	 * must be true for OM to execute either a Type1 or Type2 command. 
	 * @return boolean True if the host is READY and ACTIVE, otherwise false
	 * @throws OmConnectionException 
	 */
	public void noOpCommand() throws OmException, OmConnectionException{
		if (logger.isDebugEnabled()) { logger.debug(">> noOpCommand("+OM_READY_CMD+ ")");}

		this.om.getOMConnection().execute(OM_READY_CMD);

		if (logger.isDebugEnabled()) { logger.debug("<< noOpCommand("+OM_READY_CMD+ ")");}
	}

	public OmResultSet executeCommand(String callingMethodName, String command) throws OmException,OmConnectionException {
		if (logger.isDebugEnabled()) {
			logger.debug(">> executeCommand(" + callingMethodName +", " +command+ ")");
		}

		InputStream inputStream = om.getOMConnection().execute(command);

		this.omResultSet = processCmdResponse(inputStream,command);

		//Adding the Env and IMSPlex to the resultset so callers can know in live mode where the command originated
		this.omResultSet.setEnvironment(String.valueOf(om.getOMConnection().getEnvironment()));
		this.omResultSet.setImsplex(om.getOMConnection().getImsplex());

		//Flag the service is in live mode
		//this.omResultSet.getOmInteractionContext().setLiveModeEnabled(true);

		//if the method is getResourceVersion, do not add it to the message context
		//if (!callingMethodName.equals("getResourceVersions")) {
		om.addOmMessageContext(callingMethodName, this.omResultSet.getOmMessageContext());
		//}
		//        
		//        //Check if the attributes in the context is null before setting them else you might override others set attributes
		//        if(this.omResultSet.getOmInteractionContext().getImsAttributes() == null){
		//        	this.omResultSet.getOmInteractionContext().setImsAttributes(this.omResultSet.getResponsePropertiesHeaders());
		//        }

		if (logger.isDebugEnabled()){
			logger.debug("<< executeCommand(" + callingMethodName +", " +command+ ")");
		}

		return this.omResultSet;
	}

	//    public OmResultSet executeCommand(String command) throws OmException,OmConnectionException, OmDatastoreException {
	//        if (logger.isDebugEnabled()) {
	//            logger.debug(">> executeCommand(" + command+ ")");
	//        }
	//        
	//        //TODO: we can check that this.methodKey is not null, if it is null then we could use the callCurrentMethod or use the the 
	//        //the command as the key
	//        
	//        InputStream inputStream = om.getOMConnection().execute(command);
	//        
	//        this.omResultSet = processCmdResponse(inputStream,command);
	//        
	//        //Adding the Env and IMSPlex to the resultset so callers can know in live mode where the command originated
	//        this.omResultSet.setEnvironment(String.valueOf(om.getOMConnection().getEnvironment()));
	//        this.omResultSet.setImsplex(om.getOMConnection().getImsplex());
	//        
	//        //Flag the service is in live mode
	////		this.omResultSet.getOmInteractionContext().setLiveModeEnabled(true);
	////		
	//        om.addOmMessageContext(this.methodKey, this.omResultSet.getOmMessageContext());
	////        
	////        //Check if the attributes in the context is null before setting them else you might override others set attributes
	////        if(this.omResultSet.getOmInteractionContext().getImsAttributes() == null){
	////        	this.omResultSet.getOmInteractionContext().setImsAttributes(this.omResultSet.getResponsePropertiesHeaders());
	////        }
	//        
	//        if (logger.isDebugEnabled()){
	//            logger.debug("<< executeCommand(" + command+ ")");
	//        }
	//        
	//        return this.omResultSet;
	//    }

	//    @Deprecated
	//    public OmResultSet executeCommand(String command) throws OmException,OmConnectionException, OmDatastoreException {
	//        if (logger.isDebugEnabled())
	//            logger.debug(">> executeCommand(" +command+ ")");
	//        OmResultSet resultSet = new OmResultSet();
	//        
	//        InputStream inputStream = om.getOMConnection().execute(command);
	//        
	//        // resultSet = processCmdResponse(inputStream);
	//        resultSet = processCmdResponse(inputStream,command);
	//
	//        if (logger.isDebugEnabled())
	//            logger.debug("<< executeCommand(" +command+ ")");
	//        return resultSet;
	//    }

	private OmResultSet processResponse(InputStream inputStream, String command) throws OmException, IOException, OmConnectionException{
		if(logger.isDebugEnabled()) { 
			logger.debug(">> processType2Response(...)"); 
		}
		//Long starTimeProcessResponce = System.nanoTime();
		//Long startTime = null;

		Properties[] propertiesResponse     = new Properties[0];
		Type2CmdResponse type2CmdResponse   = null;

		inputStream.reset(); //Reset the stream because it surely is read prior to this use
		byte[] byteArrayResponse = inputStreamToByteArray(inputStream); //convert inputstream to byte array 
		//TraceUtil.dumpBytesInHex(byteArrayResponse);

		try {      
			//startTime = System.nanoTime();
			//Pass the Connect API the byte array for the response
			type2CmdResponse = new Type2CmdResponse("IBM-037", byteArrayResponse);  //TODO: Map the encoding to the helper class 
			//System.out.println("Time to process response as Type2CommandResponse for command [" + command + "] = " + ((double)(System.nanoTime()-startTime)/ 1000000000.0));
		} catch (ImsConnectApiException e) {
			OmException omException = new OmException(e); //Not everything can be set for the OmException at this point
			omException.setErrorNumber(e.getErrorNumber());
			throw omException;
		}

		try {
			//startTime = System.nanoTime();
			Properties[] propertiesRspElements = type2CmdResponse.getRspElementPropertiesObject();
			//System.out.println("Time to map the type2CmdResponse into a Properties ojbect in Connect API [" + command + "] = " + ((double)(System.nanoTime()-startTime)/ 1000000000.0));

			if(propertiesRspElements != null && propertiesRspElements.length > 0){

				this.omResultSet.getOmMessageContext().setOmCommandType(COMMAND_TYPE.TYPE2);

				//There can be MsgDataElm in Type2 command responses 
				//Response is greater than zero so prepare to append the response objects
				if(type2CmdResponse.isResponseContainsMsgdataElement()){
					Msgdata msgdata     = type2CmdResponse.getMsgdata();
					ArrayList<Mbr> mbrs = msgdata.getMbr();
					int mbrsSize        = mbrs.size();

					Properties[] propertiesMsgData = null;//new Properties[mbrsSize];

					if(mbrsSize > 0){
						propertiesMsgData = new Properties[mbrsSize];
					}else{
						propertiesMsgData = new Properties[0];
					}

					for(int i = 0; i < mbrsSize; i++){
						Mbr mbr = mbrs.get(i);
						int msgsLength = mbr.getMsg().length;
						StringBuffer msgBuffer = null;

						for(int j = 0; j < msgsLength; j++){
							String val = mbr.getMsg(j);

							if(msgBuffer == null){
								msgBuffer = new StringBuffer(val);
							}else{
								if(!val.contains("---")){
									msgBuffer.append(SPACES_COMMA).append(NEW_LINE).append(val);
								}
							}
						}


						Properties properties = new Properties();
						properties.setProperty(mbr.getElementText(), msgBuffer.toString());
						propertiesMsgData[i] = properties;
					}

					//Case we have both a RspElm and MsgDataElm, join them together and set them in the result set
					if(propertiesMsgData.length > 0){
						propertiesResponse = ArrayUtils.mergeArrays(propertiesRspElements, propertiesMsgData);
					}else{
						propertiesResponse = propertiesRspElements;
					}

					for(Properties prop: propertiesResponse){
						Set<java.util.Map.Entry<Object, Object>> entrySet = prop.entrySet();

						for(Map.Entry<Object, Object> entry : entrySet) {
							prop.setProperty((String)entry.getKey(), ((String) entry.getValue()).trim());

						}
					}

					this.omResultSet.setResponseProperties(propertiesResponse);
					this.omResultSet.setResponsePropertiesHeaders(type2CmdResponse.getAttributesForAllHdrsAsPropertiesObjects());
					this.omResultSet.getOmInteractionContext().setResourceAttributes(type2CmdResponse.getAttributesForAllHdrsAsPropertiesObjects());
					this.omResultSet.setResponseMsgData(propertiesMsgData);


				}else{

					//Case there was no MsgDataElm so set the RspElm
					propertiesResponse = propertiesRspElements; 
					for(Properties prop: propertiesResponse){
						Set<java.util.Map.Entry<Object, Object>> entrySet = prop.entrySet();

						for(Map.Entry<Object, Object> entry : entrySet) {
							prop.setProperty((String)entry.getKey(), ((String) entry.getValue()).trim());
						}
					}

					this.omResultSet.setResponseProperties(propertiesResponse);
					this.omResultSet.setResponsePropertiesHeaders(type2CmdResponse.getAttributesForAllHdrsAsPropertiesObjects());
					this.omResultSet.getOmInteractionContext().setResourceAttributes(type2CmdResponse.getAttributesForAllHdrsAsPropertiesObjects());
				}

			}else if(type2CmdResponse.isResponseContainsMsgdataElement()){
				//Since there was no RspElm there still can be a MsgDataElm such the case of a TYPE1 command
				this.omResultSet.getOmMessageContext().setOmCommandType(COMMAND_TYPE.TYPE1);

				Msgdata msgdata     = type2CmdResponse.getMsgdata();
				ArrayList<Mbr> mbrs = msgdata.getMbr();
				int mbrsSize        = mbrs.size();
				propertiesResponse = new Properties[mbrsSize];

				//If this is a DISPLAY Type1 command, we use the new Type1 processor

				//Loop through Type1 object returned
				for(int i = 0; i < mbrsSize; i++){
					Mbr mbr = mbrs.get(i);
					int msgsLength = mbr.getMsg().length;
					StringBuffer msgBuffer = null;

					//For each Type1 object, loop there it's message, or response data
					for(int j = 0; j < msgsLength; j++){
						String val = mbr.getMsg(j);

						//Formatting the data
						if(msgBuffer == null){
							msgBuffer = new StringBuffer(val);
						}else{
							if(!val.contains("---")){
								msgBuffer.append(NEW_LINE).append(val); //append(SPACES_COMMA). - fix defect 32102 for trailing commas in Type1 command
							}
						}
					}

					//Add the formated data to properties object;
					Properties properties = new Properties();
					properties.setProperty(mbr.getElementText(), msgBuffer.toString());
					propertiesResponse[i] = properties;
				}

				this.omResultSet.setResponseProperties(propertiesResponse);

				//If this is a DISPLAY command with FIDs turned on....
				if (command.contains("DIS") && command.contains("AOPOUTPUT")) {
					//responseProperties in the resultSet will be a bean now. Can't print that to the text
					//grid. Have retain old formatting so it can be printed. 
					this.omResultSet.setResponseText(propertiesResponse);
					for(int i = 0; i < mbrsSize; i++){
						Mbr mbr = mbrs.get(i);
						String[] responseWithFid = mbr.getMsg();
						Properties[] displayPropertiesResponse = new Properties[mbrsSize];

						//Get version for each ims member, we can not use the latest version we must get the exact version to map it to the fid reader
						//Version version = om.getImsplexService().getImsPlexMemberVersion(QueryImsPlex.TypeOptions.IMS, mbr.getElementText());

						//Retrieve correct FID Reader from manager
						FIDReader reader = FIDManager.getInstance().getFIDReader(this.version.toString());
						displayPropertiesResponse = reader.parseResults(responseWithFid, mbr.getElementText(), command);
						this.omResultSet.setResponseProperties(displayPropertiesResponse);

						//This is a display command, so make sure resultSet knows how to handle text output
						this.omResultSet.setDisplayCommandStatus(true);
					}
				}
			}
		} catch (ImsConnectApiException e) {
			OmException omException = new OmException(e,e.getErrorNumber());
			omException.setOmReasonCode(type2CmdResponse.getCtl().getRsn());
			omException.setOmReasonMessage(type2CmdResponse.getCtl().getRsnmsg());
			omException.setOmReasonText(type2CmdResponse.getCtl().getRsntxt());
			omException.setOmReturnCode(type2CmdResponse.getCtl().getRc());
			throw omException;
		}  

		//Build up the resultSet to send back. Note that the command will be added by the caller because the inputstream would not contain added syntax such as ROUTE
		OmMessageContext omMessageContext = this.omResultSet.getOmMessageContext();
		omMessageContext.setOmCommandExecuted(command);
		omMessageContext.setOmReturnCode(type2CmdResponse.getCtl().getRc());
		omMessageContext.setOmReasonCode(type2CmdResponse.getCtl().getRsn());
		omMessageContext.setOmReasonMessage(type2CmdResponse.getCtl().getRsnmsg());
		omMessageContext.setOmReasonText(type2CmdResponse.getCtl().getRsntxt());
		omMessageContext.setOmName(type2CmdResponse.getCtl().getOmname());
		omMessageContext.setOmVersion(type2CmdResponse.getCtl().getOmvsn());
		omMessageContext.setOmXmlVersion(type2CmdResponse.getCtl().getXmlvsn());
		omMessageContext.setOmStartTime(type2CmdResponse.getCtl().getStatime());
		omMessageContext.setOmStopTime(type2CmdResponse.getCtl().getStotime());
		omMessageContext.setOmSequence(type2CmdResponse.getCtl().getStaseq());
		omMessageContext.setOmRequestToken1(type2CmdResponse.getCtl().getRqsttkn1() == null? "":new String(type2CmdResponse.getCtl().getRqsttkn1()));
		omMessageContext.setOmRequestToken2( type2CmdResponse.getCtl().getRqsttkn2() == null? "":new String(type2CmdResponse.getCtl().getRqsttkn2()));

		//Summarize the OM Message context in a readable message
		StringBuilder omMessageSummaryBuffer = new StringBuilder();  //  omMessageContext.setOmMessageSummary(OM_MESSAGE.OM_NON_ZERO_RC_MESG.msg(new Object[]{command, omMessageContext.getOmReturnCode(), omMessageContext.getOmReasonCode(),omMessageContext.getOmReasonMessage(),omMessageContext.getOmReasonText()}));
		omMessageSummaryBuffer.append(OM_MESSAGE.OM_COMMAND.msg(new Object[]{command})).append(OM_MESSAGE.FORMAT_DELIMITER.msg());

		if(!OmMessageContext.OM_RETURN_CODE_SUCCESS.equals(omMessageContext.getOmReturnCode())){
			omMessageContext.setOmMessageTittle(OM_MESSAGE.OM_NON_ZERO_RC_TITTLE.msg());
			omMessageSummaryBuffer.append(OM_MESSAGE.OM_RETURN_CODE.msg(new Object[]{omMessageContext.getOmReturnCode()})).append(OM_MESSAGE.FORMAT_DELIMITER.msg());
			omMessageSummaryBuffer.append(OM_MESSAGE.OM_REASON_CODE.msg(new Object[]{omMessageContext.getOmReasonCode()})).append(OM_MESSAGE.FORMAT_DELIMITER.msg());
			omMessageSummaryBuffer.append(OM_MESSAGE.OM_REASON_MEASSAGE.msg(new Object[]{ omMessageContext.getOmReasonMessage()})).append(OM_MESSAGE.FORMAT_DELIMITER.msg());
			omMessageSummaryBuffer.append(OM_MESSAGE.OM_REASON_TEXT.msg(new Object[]{omMessageContext.getOmReasonText()})).append(OM_MESSAGE.FORMAT_DELIMITER.msg());

			//Lets dynamically figure which IMS they are using to provide the right doc.
			//Version version = null;

			//			try {
			//				version = om.getImsplexService().getImsPlexMemberVersion(QueryImsPlex.TypeOptions.IMS, ImsCommandAttributes.ASTERISK);
			//			} catch (Exception e) {
			//				//If we catch an exception no point on trying to recover just use the latest supported doc at this point.
			//				version = Version.IMS_LATEST_RELEASE;
			//			}

			//Since we error, provide them with URL to get help and tweak the URL to include the correct verison of the doc by setting the version.
			omMessageSummaryBuffer.append(OM_MESSAGE.OM_URL_CSL_RC_RSN_CODE.msg(new Object[]{this.version})).append(OM_MESSAGE.FORMAT_DELIMITER.msg());
		} else{
			//If its a success add the return and reson code only for now
			omMessageContext.setOmMessageTittle(OM_MESSAGE.OM_ZERO_RC_TITTLE.msg());

			//Don't think we need to pass along return codes of zero , it seems pointless
			// omMessageSummaryBuffer.append(OM_MESSAGE.OM_RETURN_CODE.msg(new Object[]{omMessageContext.getOmReturnCode()})).append(OM_MESSAGE.FORMAT_DELIMITER.msg());
			// omMessageSummaryBuffer.append(OM_MESSAGE.OM_REASON_CODE.msg(new Object[]{omMessageContext.getOmReasonCode()}));//.append(OM_MESSAGE.FORMAT_DELIMITER.msg());
		}

		//Set the summary of this ommessage context
		omMessageContext.setOmMessageSummary(omMessageSummaryBuffer.toString());

		//Summarize the command error (cmderr) - not always present in all responses
		Cmderr cmderr = type2CmdResponse.getCmderr();
		if(cmderr != null){
			ArrayList<Mbr> cmderrMbrs = cmderr.getMbr();

			if(cmderrMbrs != null){
				Collection<OmCommandErrorMbr> omCommandErrorMbrs = new ArrayList<OmCommandErrorMbr>();

				for(Mbr cmderrMbr: cmderrMbrs){
					OmCommandErrorMbr commandErrorMbr = new OmCommandErrorMbr();
					StringBuilder omMemberMessageSummaryBuffer = new StringBuilder();

					//Set these for the OM Message Member Context - they don't need to be in the summary
					commandErrorMbr.setOmMemberTyp(cmderrMbr.getTyp());
					commandErrorMbr.setOmMemberStyp(cmderrMbr.getStyp());
					commandErrorMbr.setOmMemberVsn(cmderrMbr.getVsn());
					commandErrorMbr.setOmMemberJobname(cmderrMbr.getJobname());
					commandErrorMbr.setOmMemberName(cmderrMbr.getElementText().trim());

					//Start to build the summary of a command error
					omMemberMessageSummaryBuffer.append(OM_MESSAGE.OM_MBR_NAME.msg(new Object[]{cmderrMbr.getElementText()})).append(OM_MESSAGE.FORMAT_DELIMITER.msg());
					omMemberMessageSummaryBuffer.append(OM_MESSAGE.OM_COMMAND.msg(new Object[]{command})).append(OM_MESSAGE.FORMAT_DELIMITER.msg());

					if(cmderrMbr.isRcPresent()){
						commandErrorMbr.setOmMemberRc(cmderrMbr.getRc());
						omMemberMessageSummaryBuffer.append(OM_MESSAGE.OM_RETURN_CODE.msg(new Object[]{cmderrMbr.getRc()})).append(OM_MESSAGE.FORMAT_DELIMITER.msg());
					}

					if(cmderrMbr.isRsnPresent()){
						commandErrorMbr.setOmMemberRsn(cmderrMbr.getRsn());
						omMemberMessageSummaryBuffer.append(OM_MESSAGE.OM_REASON_CODE.msg(new Object[]{cmderrMbr.getRsn()})).append(OM_MESSAGE.FORMAT_DELIMITER.msg());
					}

					if(cmderrMbr.isRsntxtPresent()){
						commandErrorMbr.setOmMemberRsntxt(cmderrMbr.getRsntxt());
						omMemberMessageSummaryBuffer.append(OM_MESSAGE.OM_REASON_TEXT.msg(new Object[]{cmderrMbr.getRsntxt()})).append(OM_MESSAGE.FORMAT_DELIMITER.msg());
					}

					if(cmderrMbr.isMsgPresent()){
						commandErrorMbr.setOmMemberMsg(Arrays.asList(cmderrMbr.getMsg()));
						for(String str: cmderrMbr.getMsg()){
							omMemberMessageSummaryBuffer.append(OM_MESSAGE.OM_MEASSAGES.msg(new Object[]{str})).append(OM_MESSAGE.FORMAT_DELIMITER.msg());
						}
					}

					//In the case of a type-1 command, there can be valuable information regarding an error in the msgdata element. Therefore we need to coordinate it with the member summary.
					String imsMemberName = cmderrMbr.getElementText();
					if (imsMemberName != null && propertiesResponse != null && propertiesResponse.length > 0) {
						for (Properties prop : propertiesResponse) {
							if (prop.containsKey(imsMemberName)) {
								String str = (String) prop.get(cmderrMbr.getElementText());
								String[] splits = str.split(",");
								int len = splits.length;

								//Skip Position zero, it is just the command
								for (int i = 1; i < len; i++) {
									omMemberMessageSummaryBuffer.append(OM_MESSAGE.OM_MEASSAGES.msg(new Object[] {splits[i].trim()}));
								}
							}
						}
					}

					if(!OmMessageContext.OM_RETURN_CODE_SUCCESS.equals(cmderrMbr.getRc())){
						commandErrorMbr.setOmMemberMessageTittle(OM_MESSAGE.OM_MBR_NON_ZERO_RC_TITTLE.msg());
						omMemberMessageSummaryBuffer.append(OM_MESSAGE.OM_URL_COMPONENT_RC_RSN_CODE.msg());
					}

					commandErrorMbr.setOmMemberMessageSummary(omMemberMessageSummaryBuffer.toString());
					omCommandErrorMbrs.add(commandErrorMbr);
				}

				//Add the commandErrorMbr to the result set
				omMessageContext.setOmCommandErrorMbrs(omCommandErrorMbrs);
			}
		}

		if(logger.isDebugEnabled()) {
			logger.debug("processType2Response OmServices OmResultSet \n", this.omResultSet);
			logger.debug("<< processType2Response(...)"); 
		}

		//System.out.println("Time to processResponse Total is for command [" + command + "] = " + ((double)(System.nanoTime()- starTimeProcessResponce)/ 1000000000.0));

		return this.omResultSet;
	}

	/**
	 * Private method will process {@link InputStream} that has returned from an OM Type1 or Type2 command
	 * and return a {@link OmResultSet}. Command is passed so it can be attached to the omMessageconext.
	 * @param inputStream
	 * @return
	 * @throws OmException
	 * @throws OmConnectionException 
	 */
	protected OmResultSet processCmdResponse(InputStream inputStream, String command) throws OmException, OmConnectionException {
		if(logger.isDebugEnabled()) { logger.debug(">> processCmdResponse(...)"); }

		OmResultSet resultSet    = null;

		if(command != null){
			try {
				inputStream.reset(); //Reset the stream because it surely is read prior to this use
				resultSet = processResponse(inputStream,command); 

				if(logger.isDebugEnabled()){
					logger.debug(resultSet.toString());
				}
			} catch (OmException e) {
				e.setOmCommandExecuted(command);
				throw e;
			} catch (IOException e) {
				OmException omException = new OmException(e);
				omException.setOmCommandExecuted(command);
				throw omException;
			}
		}

		if(logger.isDebugEnabled()) { logger.debug("<< processCmdResponse(...) "); }
		return resultSet;
	}

	/**
	 * Private method to convert an inputStream from an OM Command response to a byte[]
	 * for use with ICON API
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	private byte[] inputStreamToByteArray(InputStream inputStream) throws IOException {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] bArry = new byte[4096];
		int ret = 0;

		while ((ret = inputStream.read(bArry)) > 0) {
			bos.write(bArry, 0, ret);
		}

		byte[] result = bos.toByteArray();
		return result;
	}

}
