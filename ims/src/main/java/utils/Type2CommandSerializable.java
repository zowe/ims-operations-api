/**
 *  Copyright IBM Corporation 2018, 2019
 */

package utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import om.exception.OmCommandGenerationException;

/**
 * Parses Type2Command json objects into a string submittable IMS command
 * @author jerryli
 *
 */
public class Type2CommandSerializable {

	public static final Logger logger = LoggerFactory.getLogger(Type2CommandSerializable.class);

	public final static String PARAOP = "(";
	public final static String PARAC = ")";
	public final static String SPACE =" ";
	public final static String COMMA =",";

	protected Type2CommandMarshall marshaller = new Type2CommandMarshall();
	private String resourceType = "";
	private String verb = "";

	public String commandType;


	/**
	 * Returns the IMS Command Resource type
	 * @see <a href="http://publib.boulder.ibm.com/infocenter/dzichelp/v2r2/index.jsp?topic=%2Fcom.ibm.ims12.doc.cr%2Fimscmdsintro%2Fims_type2format.htm">IMS type-2 command format</a>
	 * @return
	 */
	public String getResourceType() {
		return this.resourceType;
	}

	/**
	 * Returns the IMS Command Verb type
	 * @see <a href="http://publib.boulder.ibm.com/infocenter/dzichelp/v2r2/index.jsp?topic=%2Fcom.ibm.ims12.doc.cr%2Fimscmdsintro%2Fims_type2format.htm">IMS type-2 command format</a>
	 * @return
	 */
	public String getVerbType() {
		return this.verb;
	}


	/**
	 * Serializes V12 type-2 commands. Command can be broken down into the prefix and the sub command.
	 * EX: UPDATE TRAN NAME (x, y) STOP(Q)
	 *      "UPDATE TRAN" is prefix, "NAME (x, y) STOP(Q)..." is sub command
	 * 
	 * @param type2Command
	 * @return
	 * @throws OmCommandGenerationException 
	 */
	public String fromType2CommandObject(Object type2Command) throws OmCommandGenerationException {
		if (logger.isDebugEnabled()) {
			logger.debug(">> fromType2CommandObject()");
		}

		StringBuilder cmd = new StringBuilder();
		String resourceString = "";
		Method verb;
		Method resource;

		try {
			//This version can be useful to check command object version or for logging
			//Method get_version = type2Command.getClass().getMethod("getVersion");
			//BigInteger version = (BigInteger) get_version.invoke(type2Command);

			verb = type2Command.getClass().getMethod("getVerb");
			resource = type2Command.getClass().getMethod("getResource");
			cmd.append("CMD").append(PARAOP);
			//Build prefixes
			resourceString = resource.invoke(type2Command).toString();
			// System.out.println(resourceString);
			cmd.append(verb.invoke(type2Command)).append(SPACE);

			//Retrieve and build subcommand using parser
			cmd.append(resource.invoke(type2Command)).append(SPACE);

			Class<?> Type2Command = type2Command.getClass();
			Field[] fields = Type2Command.getDeclaredFields();
			for (Field field : fields) {
				if (field.isAnnotationPresent(XmlElement.class)) {
					field.setAccessible(true);
					Object obj = field.get(type2Command);
					if (obj != null) {
						Class<?> clazz = obj.getClass();
						this.commandType = resourceString;
						cmd.append(parse(obj, clazz));
					}
				}
			}

			format(cmd);
			cmd.append(PARAC);
			Method route;
			ArrayList<String> routes = null;

			route = type2Command.getClass().getMethod("getRoute");
			routes = (ArrayList<String>) route.invoke(type2Command);

			if (routes != null && routes.size() > 0) {
				retrieveRouteCommand("ROUTE", routes, cmd);
			}

		} catch (SecurityException e) {
			String error = "Unable to generate IMS Command. Verb: " + this.verb + ", Resource: " + this.resourceType + ", Exception Type: " + e.getClass().getSimpleName();
			OmCommandGenerationException omCommandGenerationException = new OmCommandGenerationException(error, e);
			throw omCommandGenerationException;
		} catch (NoSuchMethodException e) {
			String error = "Unable to generate IMS Command. Verb: " + this.verb + ", Resource: " + this.resourceType + ", Exception Type: " + e.getClass().getSimpleName();
			OmCommandGenerationException omCommandGenerationException = new OmCommandGenerationException(error, e);
			throw omCommandGenerationException;
		} catch (IllegalArgumentException e) {
			String error = "Unable to generate IMS Command. Verb: " + this.verb + ", Resource: " + this.resourceType + ", Exception Type: " + e.getClass().getSimpleName();
			OmCommandGenerationException omCommandGenerationException = new OmCommandGenerationException(error, e);
			throw omCommandGenerationException;
		} catch (IllegalAccessException e) {
			String error = "Unable to generate IMS Command. Verb: " + this.verb + ", Resource: " + this.resourceType + ", Exception Type: " + e.getClass().getSimpleName();
			OmCommandGenerationException omCommandGenerationException = new OmCommandGenerationException(error, e);
			throw omCommandGenerationException;
		} catch (InvocationTargetException e) {
			String error = "Unable to generate IMS Command. Verb: " + this.verb + ", Resource: " + this.resourceType + ", Exception Type: " + e.getClass().getSimpleName();
			OmCommandGenerationException omCommandGenerationException = new OmCommandGenerationException(error, e);
			throw omCommandGenerationException;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("<< fromType2CommandObject()");
		}

		return cmd.toString();
	}

	/**
	 * Parses subcommands (UpdateTran, Query Tran etc.)
	 * @param subCommand
	 * @param clazz
	 * @return
	 * @throws OmCommandGenerationException 
	 */
	private String parse(Object subCommand, Class<?> clazz) throws OmCommandGenerationException {
		StringBuilder cmd = new StringBuilder();
		try {
			Class<?> commandClass = clazz;
			//Get all fields
			Field[] fields = commandClass.getDeclaredFields();
			for (Field field: fields) {
				//Check if annotation is of type XmlElement
				if (field.isAnnotationPresent(XmlElement.class)) {
					//If so, retrieve annotation
					Annotation[] annotations = field.getDeclaredAnnotations();
					//Set fields to be accessible
					field.setAccessible(true);
					//loop through annotaitons (only one annotation in this cas)
					for (Annotation annotateField: annotations) {
						XmlElement anno = (XmlElement) annotateField;
						//get keyword
						/*Added replaceAll method call to get rid of digits EX: SHOW2 -> SHOW
						 * It's expensive but this is a quick fix until I figure out what to do
						 * about some of the prefixes containing numbers (See QueryRtc.xsd)
						 */
						String prefix = anno.name().replaceAll("\\d", "");
						if (!Collection.class.isAssignableFrom(field.getType())) {
							Object object = field.get(subCommand);
							if (object != null) {
								retrieveCommand(prefix, object, cmd);
							}
						} else {
							List<?> list = (List<?>) field.get(subCommand);
							//if list exists, get attributes
							if (list != null) {
								retrieveCommand(prefix, list, cmd);
							}
						}
					}
				}

			}
			String str = cmd.toString();
			String newstr = str.replace('_', '-');
			return newstr;

		} catch (SecurityException e) {
			String error = "Unable to generate IMS Command. Verb: " + this.verb + ", Resource: " + this.resourceType + ", Exception Type: " + e.getClass().getSimpleName();
			OmCommandGenerationException omCommandGenerationException = new OmCommandGenerationException(error, e);
			throw omCommandGenerationException;
		} catch (IllegalArgumentException e) {
			String error = "Unable to generate IMS Command. Verb: " + this.verb + ", Resource: " + this.resourceType + ", Exception Type: " + e.getClass().getSimpleName();
			OmCommandGenerationException omCommandGenerationException = new OmCommandGenerationException(error, e);
			throw omCommandGenerationException;
		} catch (IllegalAccessException e) {
			String error = "Unable to generate IMS Command. Verb: " + this.verb + ", Resource: " + this.resourceType + ", Exception Type: " + e.getClass().getSimpleName();
			OmCommandGenerationException omCommandGenerationException = new OmCommandGenerationException(error, e);
			throw omCommandGenerationException;
		}
	}


	/**
	 * Parses nested objects within the java command objects. For example, within CreatePgm, there is a SET object with it's own fields and values. 
	 * Parses nested objects into a string.
	 * Ex: CREATE PGM SET(GPSB(N))
	 * @param prefix
	 * @param object
	 * @param cmd
	 * @throws OmCommandGenerationException
	 */
	private void retrieveCommand(String prefix, Object object, StringBuilder cmd) throws OmCommandGenerationException {
		if (prefix.equalsIgnoreCase("SET") && commandType.equals("DB")) {
			cmd.append(retrieveSetSingle(object));
			return;
			//	}else if (prefix.equalsIgnoreCase("STARTSETAREA") && commandType.equals("DB"))   {
			//		cmd.append(retrieveSTARTSETAREA(object));
			//		return;
		}else if (prefix.equalsIgnoreCase("SET")&& commandType.equals("TRAN")) {
			cmd.append(retrieveSetSingle(object));
			return;
		}else if (prefix.equalsIgnoreCase("LIKE")&& commandType.equals("TRAN")) {
			cmd.append(retrieveLikeSingle(object));
			return;
		}else if (prefix.equalsIgnoreCase("SET") && commandType.equals("RTC")) {
			cmd.append(retrieveSetSingle(object));
			return;
		}else if (prefix.equalsIgnoreCase("SET") && commandType.equals("PGM")) {
			cmd.append(retrieveSetSingle(object));
			return;
		}else if (prefix.equalsIgnoreCase("QCNT") && commandType.equals("TRAN")) {
			cmd.append(retrieveQCNT(object));
			return;
		}

		if (object != null) {
			cmd.append(prefix).append(PARAOP).append(object).append(PARAC).append(SPACE);
		}
	}


	/**
	 * Loops through the list, getting all attributes and appends them
	 * to command
	 * @param prefix
	 * @param list
	 * @param cmd
	 */
	private void retrieveCommand(String prefix, List<?> list, StringBuilder cmd) {
		if (list.size() > 0) {
			cmd.append(prefix).append(PARAOP);
			for (int i = 0; i <list.size() ; i++) {
				cmd.append(list.get(i));
				if (i != list.size()-1) {
					cmd.append(COMMA).append(SPACE);
				}
			}
			cmd.append(PARAC).append(SPACE);
		}
	}

	private void retrieveRouteCommand(String prefix, List<?> list, StringBuilder cmd) {
		if (list.size() > 0) {
			cmd.append(prefix).append(PARAOP);
			for (int i = 0; i <list.size() ; i++) {
				cmd.append(list.get(i));
				if (i != list.size()-1) {
					cmd.append(COMMA);
				}
			}
			cmd.append(PARAC).append(SPACE);
		}
	}

	/*
	 * Commented out because we don't need to implement db commands for zowe yet
	 */
	//	private String retrieveSTARTSETAREA(Object set) throws OmCommandGenerationException {
	//		StringBuilder cmd = new StringBuilder();
	//		boolean startSetAreaPopulated = false;
	//
	//		cmd.append("STARTSETAREA").append(PARAOP);
	//
	//		try {
	//			Class<?> SET12 = com.ibm.ims.ea.om.cmd.v12.update.db.UpdateDb.STARTSETAREA.SET.class;
	//			Class<?> SET13 = com.ibm.ims.ea.om.cmd.v13.update.db.UpdateDb.STARTSETAREA.SET.class;
	//			Class<?> SET14 = com.ibm.ims.ea.om.cmd.v14.update.db.UpdateDb.STARTSETAREA.SET.class;
	//			Class<?> startsetarea = set.getClass();
	//			Field[] fields = startsetarea.getDeclaredFields();
	//			for (Field field: fields) {
	//				if (field.isAnnotationPresent(XmlElement.class)) {
	//					Annotation[] annotations = field.getDeclaredAnnotations();
	//					field.setAccessible(true);
	//					for (Annotation annotateField: annotations) {
	//						XmlElement anno = (XmlElement) annotateField;
	//						Object obj =  field.get(set);
	//						if (obj != null && (obj.getClass().equals(SET12) || obj.getClass().equals(SET13) || obj.getClass().equals(SET14))) {
	//							cmd.append(retrieveSetSingle(obj));
	//							format(cmd);
	//							cmd.append(COMMA).append(SPACE);
	//							startSetAreaPopulated = true;
	//						}
	//						else if (obj != null) {
	//							cmd.append(anno.name()).append(PARAOP);
	//							cmd.append(obj.toString());
	//							cmd.append(PARAC).append(COMMA).append(SPACE);
	//							startSetAreaPopulated = true;
	//						}
	//					}
	//				}
	//			}
	//		}
	//		catch (SecurityException e) {
	//			String error = "Unable to generate IMS Command. Verb: " + this.verb + ", Resource: " + this.resourceType + ", Exception Type: " + e.getClass().getSimpleName();
	//			OmCommandGenerationException omCommandGenerationException = new OmCommandGenerationException(error, e);
	//			throw omCommandGenerationException;
	//		} catch (IllegalArgumentException e) {
	//			String error = "Unable to generate IMS Command. Verb: " + this.verb + ", Resource: " + this.resourceType + ", Exception Type: " + e.getClass().getSimpleName();
	//			OmCommandGenerationException omCommandGenerationException = new OmCommandGenerationException(error, e);
	//			throw omCommandGenerationException;
	//		} catch (IllegalAccessException e) {
	//			String error = "Unable to generate IMS Command. Verb: " + this.verb + ", Resource: " + this.resourceType + ", Exception Type: " + e.getClass().getSimpleName();
	//			OmCommandGenerationException omCommandGenerationException = new OmCommandGenerationException(error, e);
	//			throw omCommandGenerationException;
	//		}
	//
	//		if (!startSetAreaPopulated) {
	//			String error = "Unable to generate IMS Command. Verb: " + this.verb + ", Resource: " + this.resourceType + ", Exception Reason: Empty STARTSETAREA attribute (nothing populated)";
	//			OmCommandGenerationException omCommandGenerationException = new OmCommandGenerationException(error);
	//			throw omCommandGenerationException;
	//		}
	//
	//		format(cmd);
	//		format(cmd);
	//		cmd.append(PARAC).append(SPACE);
	//		return cmd.toString();
	//	}


	private String retrieveQCNT(Object qcnt) throws OmCommandGenerationException {
		StringBuilder cmd = new StringBuilder();
		boolean setPopulated = false;
		cmd.append("QCNT").append(PARAOP);
		try {
			Class<?> dbSET = qcnt.getClass();
			Field[] fields = dbSET.getDeclaredFields();
			for (Field field: fields) {
				if (field.isAnnotationPresent(XmlElement.class)) {
					field.setAccessible(true);
					Object obj = field.get(qcnt);
					if (obj != null) {
						setPopulated = true;
						cmd.append(obj);
						cmd.append(COMMA);
					}
				}
			}
		}
		catch (SecurityException e) {
			String error = "Unable to generate IMS Command. Verb: " + this.verb + ", Resource: " + this.resourceType + ", Exception Type: " + e.getClass().getSimpleName();
			OmCommandGenerationException omCommandGenerationException = new OmCommandGenerationException(error, e);
			throw omCommandGenerationException;
		} catch (IllegalArgumentException e) {
			String error = "Unable to generate IMS Command. Verb: " + this.verb + ", Resource: " + this.resourceType + ", Exception Type: " + e.getClass().getSimpleName();
			OmCommandGenerationException omCommandGenerationException = new OmCommandGenerationException(error, e);
			throw omCommandGenerationException;
		} catch (IllegalAccessException e) {
			String error = "Unable to generate IMS Command. Verb: " + this.verb + ", Resource: " + this.resourceType + ", Exception Type: " + e.getClass().getSimpleName();
			OmCommandGenerationException omCommandGenerationException = new OmCommandGenerationException(error, e);
			throw omCommandGenerationException;
		}

		if (!setPopulated) {
			cmd.append(PARAC);
			return cmd.toString();
		}

		format(cmd);
		cmd.append(PARAC).append(SPACE);
		return cmd.toString();
	}


	/**
	 * Loops through SET objects retrieving all attributes. A SET can be thought of as a 
	 * sub-subcommand, it in itself contains lists of attributes. 
	 * @param set
	 * @return
	 * @throws OmCommandGenerationException 
	 */
	private String retrieveSetSingle(Object set) throws OmCommandGenerationException {	
		StringBuilder cmd = new StringBuilder();
		boolean setPopulated = false;
		cmd.append("SET").append(PARAOP);
		try {
			Class<?> dbSET = set.getClass();
			Field[] fields = dbSET.getDeclaredFields();
			for (Field field: fields) {
				if (field.isAnnotationPresent(XmlElement.class)) {
					Annotation[] annotations = field.getDeclaredAnnotations();
					field.setAccessible(true);
					for (Annotation annotateField: annotations) {
						XmlElement anno = (XmlElement) annotateField;
						Object obj = field.get(set);
						if (obj != null) {
							setPopulated = true;
							cmd.append(anno.name()).append(PARAOP);
							cmd.append(obj);
							cmd.append(PARAC).append(COMMA).append(SPACE);
						}
					}
				}
			}
		}
		catch (SecurityException e) {
			String error = "Unable to generate IMS Command. Verb: " + this.verb + ", Resource: " + this.resourceType + ", Exception Type: " + e.getClass().getSimpleName();
			OmCommandGenerationException omCommandGenerationException = new OmCommandGenerationException(error, e);
			throw omCommandGenerationException;
		} catch (IllegalArgumentException e) {
			String error = "Unable to generate IMS Command. Verb: " + this.verb + ", Resource: " + this.resourceType + ", Exception Type: " + e.getClass().getSimpleName();
			OmCommandGenerationException omCommandGenerationException = new OmCommandGenerationException(error, e);
			throw omCommandGenerationException;
		} catch (IllegalAccessException e) {
			String error = "Unable to generate IMS Command. Verb: " + this.verb + ", Resource: " + this.resourceType + ", Exception Type: " + e.getClass().getSimpleName();
			OmCommandGenerationException omCommandGenerationException = new OmCommandGenerationException(error, e);
			throw omCommandGenerationException;
		}

		if (!setPopulated) {
			cmd.append(PARAC);
			return cmd.toString();
		}

		format(cmd);
		format(cmd);
		cmd.append(PARAC).append(SPACE);
		return cmd.toString();
	}

	/**
	 * Loops through LIKE objects retrieving all attributes. A SET can be thought of as a 
	 * sub-subcommand, it in itself contains lists of attributes. 
	 * @param like
	 * @return
	 * @throws OmCommandGenerationException 
	 */
	private String retrieveLikeSingle(Object like) throws OmCommandGenerationException {	
		StringBuilder cmd = new StringBuilder();
		boolean setPopulated = false;
		cmd.append("LIKE").append(PARAOP);
		try {
			Class<?> dbSET = like.getClass();
			Field[] fields = dbSET.getDeclaredFields();
			for (Field field: fields) {
				if (field.isAnnotationPresent(XmlElement.class)) {
					Annotation[] annotations = field.getDeclaredAnnotations();
					field.setAccessible(true);
					for (Annotation annotateField: annotations) {
						XmlElement anno = (XmlElement) annotateField;
						Object obj = field.get(like);
						if (obj != null) {
							setPopulated = true;
							cmd.append(anno.name()).append(PARAOP);
							cmd.append(obj);
							cmd.append(PARAC).append(COMMA).append(SPACE);
						}
					}
				}
			}
		}
		catch (SecurityException e) {
			String error = "Unable to generate IMS Command. Verb: " + this.verb + ", Resource: " + this.resourceType + ", Exception Type: " + e.getClass().getSimpleName();
			OmCommandGenerationException omCommandGenerationException = new OmCommandGenerationException(error, e);
			throw omCommandGenerationException;
		} catch (IllegalArgumentException e) {
			String error = "Unable to generate IMS Command. Verb: " + this.verb + ", Resource: " + this.resourceType + ", Exception Type: " + e.getClass().getSimpleName();
			OmCommandGenerationException omCommandGenerationException = new OmCommandGenerationException(error, e);
			throw omCommandGenerationException;
		} catch (IllegalAccessException e) {
			String error = "Unable to generate IMS Command. Verb: " + this.verb + ", Resource: " + this.resourceType + ", Exception Type: " + e.getClass().getSimpleName();
			OmCommandGenerationException omCommandGenerationException = new OmCommandGenerationException(error, e);
			throw omCommandGenerationException;
		}

		if (!setPopulated) {
			cmd.append(PARAC);
			return cmd.toString();
		}

		format(cmd);
		format(cmd);
		format(cmd);
		cmd.append(PARAC).append(SPACE);
		return cmd.toString();
	}

	//	public String retrieveSetList(Object set) {
	//		StringBuilder cmd = new StringBuilder();
	//		cmd.append("SET").append(PARAOP);
	//		try {
	//			Class<?> tranSET = set.getClass();
	//			Field[] fields = tranSET.getDeclaredFields();
	//			for (Field field: fields) {
	//				if (field.isAnnotationPresent(XmlElement.class)) {
	//					Annotation[] annotations = field.getDeclaredAnnotations();
	//					field.setAccessible(true);
	//					for (Annotation annotateField: annotations) {
	//						XmlElement anno = (XmlElement) annotateField;
	//						List<?> obj = (List<?>) field.get(set);
	//						if (obj != null && obj.size() > 0) {
	//							cmd.append(anno.name()).append(PARAOP);
	//							for (int i = 0; i < obj.size(); i++) {
	//								if (obj.get(i) != null) {
	//									cmd.append(obj.get(i));
	//								}
	//								if (i != obj.size()-1) {
	//									cmd.append(SPACE);
	//								}
	//							}
	//							cmd.append(PARAC).append(COMMA).append(SPACE);
	//						}
	//					}
	//				}
	//			}
	//		}
	//		catch (SecurityException e) {
	//
	//		} catch (IllegalArgumentException e) {
	//		} catch (IllegalAccessException e) {
	//			e.printStackTrace();
	//		}
	//		format(cmd);
	//		cmd.append(PARAC).append(PARAC);
	//		return cmd.toString();
	//	}

	public void format(StringBuilder cmd) {
		if (cmd.charAt(cmd.length()-1) == ',' || cmd.charAt(cmd.length()-1) == ' ') {
			cmd.deleteCharAt(cmd.length()-1);
		}
	}

	//	//************* DON"T FORGET TO REMOVE THE MAIN or Comment it out ***************
	//	/**
	//	 * For testing purposes only, remove when finished
	//	 * @param args
	//	 * @throws OmCommandGenerationException 
	//	 */
	//	public static void main(String args[]) throws OmCommandGenerationException {
	//		
	//		/*
	//		 *CMD UPDATE TRAN NAME(TRAN1, TRAN2) START(Q, SCHD) SET(LOCK(ON))
	//		 */
	//		com.ibm.ims.ea.om.cmd.v13.type2.Type2Command test = new com.ibm.ims.ea.om.cmd.v13.type2.Type2Command();
	//		test.setVerb(com.ibm.ims.ea.om.cmd.v13.type2.Type2Command.VerbOptions.UPDATE);
	//		test.setResource(com.ibm.ims.ea.om.cmd.v13.type2.Type2Command.ResourceOptions.TRAN);
	//		com.ibm.ims.ea.om.cmd.v13.update.tran.UpdateTran updateTran = new com.ibm.ims.ea.om.cmd.v13.update.tran.UpdateTran();
	//		Collection<String> transactions = Arrays.asList("TRAN1", "TRAN2");
	//		updateTran.getNAME().addAll(transactions);
	//		updateTran.getSTART().add(com.ibm.ims.ea.om.cmd.v13.update.tran.UpdateTran.StartOptions.Q);
	//		updateTran.getSTOP().add(com.ibm.ims.ea.om.cmd.v13.update.tran.UpdateTran.StopOptions.Q);
	//		updateTran.getSTOP().add(com.ibm.ims.ea.om.cmd.v13.update.tran.UpdateTran.StopOptions.SCHD);
	//		updateTran.getCLASS().add(999);
	//		com.ibm.ims.ea.om.cmd.v13.update.tran.UpdateTran.SET set = new com.ibm.ims.ea.om.cmd.v13.update.tran.UpdateTran.SET();
	//		set.setLOCK(com.ibm.ims.ea.om.cmd.v13.update.tran.UpdateTran.SET.LockOptions.ON);
	//		set.setAOCMD(com.ibm.ims.ea.om.cmd.v13.update.tran.UpdateTran.SET.AocmdOptions.CMD);
	//		set.setCLASS(5);
	//		updateTran.setSET(set);
	//		test.getRoute().add("IMS1");
	//		test.getRoute().add("IMS2");
	//		test.setUpdateTran(updateTran);
	//
	//		/*
	//		 * CMD UPDATE DB NAME(DB1) STARTSETAREA(START(ACCESS) SET(LOCK(ON) AREA([testarea])) 
	//		 */
	////		com.ibm.ims.ea.om.cmd.v12.type2.Type2Command test1 = new com.ibm.ims.ea.om.cmd.v12.type2.Type2Command();
	////		test1.setVerb(com.ibm.ims.ea.om.cmd.v12.type2.Type2Command.VerbOptions.UPDATE);
	////		test1.setResource(com.ibm.ims.ea.om.cmd.v12.type2.Type2Command.ResourceOptions.DB);
	////		UpdateDb updateDb = new UpdateDb();
	////		Collection<String> databases = Arrays.asList("DB1");
	////		updateDb.getNAME().addAll(databases);
	////		com.ibm.ims.ea.om.cmd.v12.update.db.UpdateDb.STARTSETAREA.SET set1 = new com.ibm.ims.ea.om.cmd.v12.update.db.UpdateDb.STARTSETAREA.SET();
	////		set1.setLOCK(com.ibm.ims.ea.om.cmd.v12.update.db.UpdateDb.STARTSETAREA.SET.LockOptions.ON);
	////		com.ibm.ims.ea.om.cmd.v12.update.db.UpdateDb.STARTSETAREA setarea = new com.ibm.ims.ea.om.cmd.v12.update.db.UpdateDb.STARTSETAREA();
	////		setarea.setSTART(com.ibm.ims.ea.om.cmd.v12.update.db.UpdateDb.STARTSETAREA.StartOptions.ACCESS);
	////		setarea.setSET(set1);
	////		setarea.getAREA().add("testarea");
	////		updateDb.setSTARTSETAREA(setarea);
	////		test1.setUpdateDb(updateDb);
	////		
	////		
	//
	//		Type2CommandUnMarshall unmarshaller = new Type2CommandUnMarshall();
	//		Type2CommandMarshall marshaller = new Type2CommandMarshall();
	//	
	//		System.out.println(new Type2CommandSerializable().fromType2CommandObject(test));
	//		//System.out.println(new Type2CommandSerializable().fromType2CommandObject(test1));
	//
	//
	//	}


}