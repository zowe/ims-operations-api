/**
 *  Copyright IBM Corporation 2018, 2019
 */

package application.rest.responses.tran.query;

import java.lang.reflect.Field;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "POJO from a Query TRAN command that represents output for one transaction")
public class QueryTransaction {
	
	@Schema(description = "Affinity of the transaction messages on the shared queues, or affinity registration of the transactions for this IMS.")
	String afin;
	@Schema(description = "Transaction supports AOI CMD calls (CMD, TRAN, or Y) or not (N). The output value is obtained from the local IMS.")
	String aocmd;
	@Schema(description = "Completion code. The completion code indicates whether IMS was able to process the command for the specified resource. The completion code is always returned. ")
	String cc;
	@Schema(description = "	Completion code text that briefly explains the meaning of the nonzero completion code.")
	String cctxt;
	@Schema(description = "Commit mode for the transaction: commit after a single message (SNGL) or multiple messages (MULT). The output value is obtained from the local IMS.")
	String cmtm;
	@Schema(description = "Conversation option. Transaction is conversational (Y), or not (N). The output value is obtained from the local IMS.")
	String conv;
	@Schema(description = "Conversation ID for transaction that has a conversation in progress.")
	String convid;
	@Schema(description = "Perform log write-ahead for recoverable, nonresponse mode input messages and transaction output messages (Y) or not (N). The output value is obtained from the local IMS.")
	String dclw;
	@Schema(description = "Definition type")
	String dfnt;
	@Schema(description = "Supports MSC directed routing (Y) or not (N). The output value is obtained from the local IMS.")
	String drrt;
	@Schema(description = "Input edit routine name.")
	String edtr;
	@Schema(description = "Input data is to be translated to uppercase (Y) or not (N). The output value is obtained from the local IMS.")
	String edtt;
	@Schema(description = "EMH buffer size. The output value is obtained from the local IMS.")
	String emhbs;
	@Schema(description = "Indicates whether the transaction has been exported to the IMSRSC repository. The value can be Y or N.")
	String expn;
	@Schema(description = "Transaction expiration time. The output value is obtained from the local IMS.")
	String exprt;
	@Schema(description = "Fast Path potential candidate (P), Fast Path exclusive (E), or FP option not enabled (N). The output value is obtained from the local IMS.")
	String fp;
	@Schema(description = "Returns the IMSIDs that have the resource defined. The output value is obtained from the repository.")
	String imsid;
	@Schema(description = "Inquiry transaction (Y) or not (N). The output value is obtained from the local IMS.")
	String inq;
	@Schema(description = "Scheduling class used to determine which message regions can process the transaction locally on a particular IMS.")
	String lcls;
	@Schema(description = "Local current scheduling priority. The current scheduling priority is used to calculate which transaction is selected for scheduling.")
	String lcp;
	@Schema(description = "Limit count in the local IMS. The limit count is the number that, when compared to the number of input transactions queued and waiting to be processed, determines whether the normal or limit priority value is assigned to this transaction.")
	String llct;
	@Schema(description = "Local limit scheduling priority. The limit scheduling priority is the priority to which this transaction is raised when the number of input transactions enqueued and waiting to be processed is equal to or greater than the limit count value.")
	String llp;
	@Schema(description = "Local maximum region count. The maximum region count is the maximum number of message processing program (MPP) regions that can be concurrently scheduled to process a transaction that is eligible for parallel scheduling.")
	String lmrg;
	@Schema(description = "Local normal scheduling priority. The normal scheduling priority is the priority assigned to this transaction when the number of input transactions enqueued and waiting to be processed is less than the limit count value.")
	String lnp;
	@Schema(description = "Local processing limit count. The processing limit count is the number of transaction messages a program can process in a single scheduling.")
	String lplct;
	@Schema(description = "Local parallel processing limit count. The parallel limit count is the maximum number of messages that can currently be queued, but not yet processed, by each active message region currently scheduled for this transaction. An additional message region is scheduled whenever the transaction queue count exceeds the PARLIM value multiplied by the number of regions currently scheduled for this transaction.")
	String lplm;
	@Schema(description = "Local transaction message queue count.")
	String lq;
	@Schema(description = "Local application program output segment limit allowed in message queues for each GU call.")
	String lsno;
	@Schema(description = "Local application program output segment size limit allowed in the message queues for each GU call.")
	String lssz;
	@Schema(description = "Local transaction status.")
	String lstt;
	@Schema(description = "APPC LU name that initiated conversation.")
	String lu;
	@Schema(description = "Model name. Name of the resource used as a model to create this resource. DFSDSTR1 is the IMS descriptor name for transactions.")
	String mdln;
	@Schema(description = "Model type, either RSC or DESC. RSC means that the resource was created using another resource as a model. DESC means that the resource was created using a descriptor as a model.")
	String mdlt;
	@Schema(description = "IMSplex member that built the output line. IMS identifier of IMS that built the output. The IMS identifier is always returned.")
	String mbr;
	@Schema(description = "Message type of single segment (SNGLSEG) or multiple segment (MULTSEG). The output value is obtained from the local IMS.")
	String msgt;
	@Schema(description = "Logical link path name. The output value is obtained from the local IMS.")
	String msn;
	@Schema(description = "Node name that initiated conversation.")
	String node;
	@Schema(description = "Processing limit count time.")
	String plctt;
	@Schema(description = "PSB name associated with the transaction. The output value is obtained from the local IMS.")
	String psb;
	@Schema(description = "Global transaction message queue count on the shared queues. Q is displayed only if shared queues are used.")
	String qcnt;
	@Schema(description = "Transaction supports AOI CMD calls (CMD, TRAN, or Y) or not (N). The output value is obtained from the repository.")
	String raocmd;
	@Schema(description = "	Class value in the repository.")
	String rcls;
	@Schema(description = "Commit mode for the transaction: commit after a single message (SNGL) or multiple messages (MULT). The output value is obtained from the repository.")
	String rcmtm;
	@Schema(description = "Conversation ID if a conversation is in progress in the repository.")
	String rconv;
	@Schema(description = "Recovered during an IMS emergency or normal restart (Y) or not (N). The output value is obtained from the local IMS.")
	String rcv;
	@Schema(description = "Perform log write-ahead for recoverable, nonresponse mode input messages and transaction output messages (Y) or not (N). The output value is obtained from the repository.")
	String rdclw;
	@Schema(description = "Supports MSC directed routing (Y) or not (N). The output value is obtained from the repository.")
	String rdrrt;
	@Schema(description = "Edit routine value obtained from the repository.")
	String redtr;
	@Schema(description = "Input data is to be translated to uppercase (Y) or not (N). The output value is obtained from the repository. For the values to be returned, see LEditUC in this table.")
	String redtt;
	@Schema(description = "EMH buffer size. The output value is obtained from the repository.")
	String remhbs;
	@Schema(description = "Indicates whether the output line contains the stored resource definitions.")
	String repo;
	@Schema(description = "Transaction expiration time. The output value is obtained from the repository.")
	String rexprt;
	@Schema(description = "Fast Path potential candidate (P), Fast Path exclusive (E), or FP option not enabled (N). The output value is obtained from the repository. For the values to be returned, see the description for LFP in this table.")
	String rfp;
	@Schema(description = "Number of regions the transaction is currently scheduled in the local IMS. The output value is obtained from the local IMS.")
	String rgc;
	@Schema(description = "Inquiry transaction (Y) or not (N). The output value is obtained from the repository. For the values to be returned, see the description for LInq in this table.")
	String rinq;
	@Schema(description = "Limit count value in the repository. The limit count is the number that, when compared to the number of input transactions queued and waiting to be processed, determines whether the normal or limit priority value is assigned to this transaction.")
	String rlct;
	@Schema(description = "Local limit scheduling priority value in the repository. The limit scheduling priority is the priority to which this transaction is raised when the number of input transactions enqueued and waiting to be processed is equal to or greater than the limit count value.")
	String rlp;
	@Schema(description = "Maximum region count obtained from the repository. The maximum region count is the maximum number of message processing program (MPP) regions that can be concurrently scheduled to process a transaction that is eligible for parallel scheduling.")
	String rmrg;
	@Schema(description = "Message type of single segment (SNGLSEG) or multiple segment (MULTSEG). The output value is obtained from the repository. For the values to be returned, see the description for LMsgType in this table.")
	String rmsgt;
	@Schema(description = "Remote transaction (Y) or not (N). The output value is obtained from the local IMS.")
	String rmt;
	@Schema(description = "Normal scheduling priority value obtained from the repository. The normal scheduling priority is the priority assigned to this transaction when the number of input transactions enqueued and waiting to be processed is less than the limit count value.")
	String rnp;
	@Schema(description = "Processing limit count obtained from the repository. The processing limit count is the number of transaction messages a program can process in a single scheduling.")
	String rplct;
	@Schema(description = "Processing limit count time value in the repository.")
	String rplctt;
	@Schema(description = "	Parallel processing limit count obtained from the repository. The parallel limit count is the maximum number of messages that can currently be queued, but not yet processed, by each active message region currently scheduled for this transaction. An additional message region is scheduled whenever the transaction queue count exceeds the PARLIM value multiplied by the number of regions currently scheduled for this transaction.")
	String rplm;
	@Schema(description = "PSB name associated with the transaction. The output value is obtained from the repository.")
	String rpsb;
	@Schema(description = "Recovered during an IMS emergency or normal restart (Y) or not (N). The output value is obtained from the repository. For the values to be returned, see the description for LRecover in this table.")
	String rrcv;
	@Schema(description = "Remote transaction (Y) or not (N). The output value is obtained from the repository. For the values to be returned, see the description for LRemote in this table.")
	String rrmt;
	@Schema(description = "Response mode transaction (Y) or not (N). The output value is obtained from the repository. For the values to be returned, see the description for LResp in this table.")
	String rrsp;
	@Schema(description = "Transaction is processed serially (Y) or not (N). The output value is obtained from the repository. For the values to be returned, see the description for LSerial in this table.")
	String rser;
	@Schema(description = "Local system ID. The output value is obtained from the repository.")
	String rsidl;
	@Schema(description = "Remote system ID. The output value is obtained from the repository.")
	String rsidr;
	@Schema(description = "Application program output segment limit allowed in message queues for each GU call. The value is obtained from the repository.")
	String rsno;
	@Schema(description = "Response mode transaction (Y) or not (N). The output value is obtained from the local IMS.")
	String rsp;
	@Schema(description = "Conversational transaction scratchpad area size. The output value is obtained from the repository.")
	String rspasz;
	@Schema(description = "Conversational transaction SPA data should be truncated (R) or preserved (S) across a program switch to a transaction that is defined with a smaller SPA. ")
	String rspatr;
	@Schema(description = "Application program output segment size limit allowed in the message queues for each GU call. The value is obtained from the repository.")
	String rsssz;
	@Schema(description = "Transaction level statistics logged (Y) or not (N). The output value is obtained from the repository. For the values to be returned, see the description for LTranStat in this table.")
	String rtls;
	@Schema(description = "Create time from the repository. This is the time the resource was first created in the repository.")
	String rtmcr;
	@Schema(description = "Update time from the repository. This is the time the resource was last updated in the repository.")
	String rtmup;
	@Schema(description = "Wait-for-input transaction (Y) or not (N). The output value is obtained from the repository. For the values to be returned, see the description for LWFI in this table.")
	String rwfi;
	@Schema(description = "Transaction is processed serially (Y) or not (N). The output value is obtained from the local IMS.")
	String ser;
	@Schema(description = "Local system ID. The output value is obtained from the local IMS.")
	String sidl;
	@Schema(description = "Remote system ID. The output value is obtained from the local IMS.")
	String sidr;
	@Schema(description = "Conversational transaction scratchpad area size. The output value is obtained from the local IMS.")
	String spasz;
	@Schema(description = "Conversational transaction SPA data should be truncated (R) or preserved (S) across a program switch to a transaction that is defined with a smaller SPA. The output value is obtained from the local IMS.")
	String spatr;
	@Schema(description = "Global transaction status")
	String stt;
	@Schema(description = "Transaction level statistics logged (Y) or not (N). The output value is obtained from the local IMS.")
	String tls;
	@Schema(description = "The time that the resource was last accessed. The output value is obtained from the local IMS.")
	String tmac;
	@Schema(description = "The time that the resource was created. The output value is obtained from the local IMS.")
	String tmcr;
	@Schema(description = "OTMA tmember that initiated conversation.")
	String tmem;
	@Schema(description = "The time that the resource was last imported. The import time is retained across warm start and emergency restart. The output value is obtained from the local IMS.")
	String tmim;
	@Schema(description = "The last time the attributes of the runtime resource definition were updated as a result of the UPDATE TRAN, a type-1 command, or the IMPORT command. The update time is retained across warm start and emergency restart. The output value is obtained from the local IMS.")
	String tmup;
	@Schema(description = "OTMA tpipe that initiated conversation.")
	String tpip;
	@Schema(description = "Transaction name. A transaction defines the processing characteristics of messages destined for an application program.")
	String tran;
	@Schema(description = "User that initiated conversation.")
	String user;
	@Schema(description = "Wait-for-input transaction (Y) or not (N). The output value is obtained from the local IMS.")
	String wfi;
	@Schema(description = "Work is in progress for the transaction or one of its associated resources.")
	String wrk;
	
	public String getAfin() {
		return afin;
	}
	public void setAfin(String afin) {
		this.afin = afin;
	}
	public String getAocmd() {
		return aocmd;
	}
	public void setAocmd(String aocmd) {
		this.aocmd = aocmd;
	}
	public String getCc() {
		return cc;
	}
	public void setCc(String cc) {
		this.cc = cc;
	}
	public String getCctxt() {
		return cctxt;
	}
	public void setCctxt(String cctxt) {
		this.cctxt = cctxt;
	}
	public String getCmtm() {
		return cmtm;
	}
	public void setCmtm(String cmtm) {
		this.cmtm = cmtm;
	}
	public String getConv() {
		return conv;
	}
	public void setConv(String conv) {
		this.conv = conv;
	}
	public String getConvid() {
		return convid;
	}
	public void setConvid(String convid) {
		this.convid = convid;
	}
	public String getDclw() {
		return dclw;
	}
	public void setDclw(String dclw) {
		this.dclw = dclw;
	}
	public String getDfnt() {
		return dfnt;
	}
	public void setDfnt(String dfnt) {
		this.dfnt = dfnt;
	}
	public String getDrrt() {
		return drrt;
	}
	public void setDrrt(String drrt) {
		this.drrt = drrt;
	}
	public String getEdtr() {
		return edtr;
	}
	public void setEdtr(String edtr) {
		this.edtr = edtr;
	}
	public String getEdtt() {
		return edtt;
	}
	public void setEdtt(String edtt) {
		this.edtt = edtt;
	}
	public String getEmhbs() {
		return emhbs;
	}
	public void setEmhbs(String emhbs) {
		this.emhbs = emhbs;
	}
	public String getExpn() {
		return expn;
	}
	public void setExpn(String expn) {
		this.expn = expn;
	}
	public String getExprt() {
		return exprt;
	}
	public void setExprt(String exprt) {
		this.exprt = exprt;
	}
	public String getFp() {
		return fp;
	}
	public void setFp(String fp) {
		this.fp = fp;
	}
	public String getImsid() {
		return imsid;
	}
	public void setImsid(String imsid) {
		this.imsid = imsid;
	}
	public String getInq() {
		return inq;
	}
	public void setInq(String inq) {
		this.inq = inq;
	}
	public String getLcls() {
		return lcls;
	}
	public void setLcls(String lcls) {
		this.lcls = lcls;
	}
	public String getLcp() {
		return lcp;
	}
	public void setLcp(String lcp) {
		this.lcp = lcp;
	}
	public String getLlct() {
		return llct;
	}
	public void setLlct(String llct) {
		this.llct = llct;
	}
	public String getLlp() {
		return llp;
	}
	public void setLlp(String llp) {
		this.llp = llp;
	}
	public String getLmrg() {
		return lmrg;
	}
	public void setLmrg(String lmrg) {
		this.lmrg = lmrg;
	}
	public String getLnp() {
		return lnp;
	}
	public void setLnp(String lnp) {
		this.lnp = lnp;
	}
	public String getLplct() {
		return lplct;
	}
	public void setLplct(String lplct) {
		this.lplct = lplct;
	}
	public String getLplm() {
		return lplm;
	}
	public void setLplm(String lplm) {
		this.lplm = lplm;
	}
	public String getLq() {
		return lq;
	}
	public void setLq(String lq) {
		this.lq = lq;
	}
	public String getLsno() {
		return lsno;
	}
	public void setLsno(String lsno) {
		this.lsno = lsno;
	}
	public String getLssz() {
		return lssz;
	}
	public void setLssz(String lssz) {
		this.lssz = lssz;
	}
	public String getLstt() {
		return lstt;
	}
	public void setLstt(String lstt) {
		this.lstt = lstt;
	}
	public String getLu() {
		return lu;
	}
	public void setLu(String lu) {
		this.lu = lu;
	}
	public String getMdln() {
		return mdln;
	}
	public void setMdln(String mdln) {
		this.mdln = mdln;
	}
	public String getMdlt() {
		return mdlt;
	}
	public void setMdlt(String mdlt) {
		this.mdlt = mdlt;
	}
	public String getMbr() {
		return mbr;
	}
	public void setMbr(String mbr) {
		this.mbr = mbr;
	}
	public String getMsgt() {
		return msgt;
	}
	public void setMsgt(String msgt) {
		this.msgt = msgt;
	}
	public String getMsn() {
		return msn;
	}
	public void setMsn(String msn) {
		this.msn = msn;
	}
	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
	}
	public String getPlctt() {
		return plctt;
	}
	public void setPlctt(String plctt) {
		this.plctt = plctt;
	}
	public String getPsb() {
		return psb;
	}
	public void setPsb(String psb) {
		this.psb = psb;
	}
	public String getQcnt() {
		return qcnt;
	}
	public void setQcnt(String qcnt) {
		this.qcnt = qcnt;
	}
	public String getRaocmd() {
		return raocmd;
	}
	public void setRaocmd(String raocmd) {
		this.raocmd = raocmd;
	}
	public String getRcls() {
		return rcls;
	}
	public void setRcls(String rcls) {
		this.rcls = rcls;
	}
	public String getRcmtm() {
		return rcmtm;
	}
	public void setRcmtm(String rcmtm) {
		this.rcmtm = rcmtm;
	}
	public String getRconv() {
		return rconv;
	}
	public void setRconv(String rconv) {
		this.rconv = rconv;
	}
	public String getRcv() {
		return rcv;
	}
	public void setRcv(String rcv) {
		this.rcv = rcv;
	}
	public String getRdclw() {
		return rdclw;
	}
	public void setRdclw(String rdclw) {
		this.rdclw = rdclw;
	}
	public String getRdrrt() {
		return rdrrt;
	}
	public void setRdrrt(String rdrrt) {
		this.rdrrt = rdrrt;
	}
	public String getRedtr() {
		return redtr;
	}
	public void setRedtr(String redtr) {
		this.redtr = redtr;
	}
	public String getRedtt() {
		return redtt;
	}
	public void setRedtt(String redtt) {
		this.redtt = redtt;
	}
	public String getRemhbs() {
		return remhbs;
	}
	public void setRemhbs(String remhbs) {
		this.remhbs = remhbs;
	}
	public String getRepo() {
		return repo;
	}
	public void setRepo(String repo) {
		this.repo = repo;
	}
	public String getRexprt() {
		return rexprt;
	}
	public void setRexprt(String rexprt) {
		this.rexprt = rexprt;
	}
	public String getRfp() {
		return rfp;
	}
	public void setRfp(String rfp) {
		this.rfp = rfp;
	}
	public String getRgc() {
		return rgc;
	}
	public void setRgc(String rgc) {
		this.rgc = rgc;
	}
	public String getRinq() {
		return rinq;
	}
	public void setRinq(String rinq) {
		this.rinq = rinq;
	}
	public String getRlct() {
		return rlct;
	}
	public void setRlct(String rlct) {
		this.rlct = rlct;
	}
	public String getRlp() {
		return rlp;
	}
	public void setRlp(String rlp) {
		this.rlp = rlp;
	}
	public String getRmrg() {
		return rmrg;
	}
	public void setRmrg(String rmrg) {
		this.rmrg = rmrg;
	}
	public String getRmsgt() {
		return rmsgt;
	}
	public void setRmsgt(String rmsgt) {
		this.rmsgt = rmsgt;
	}
	public String getRmt() {
		return rmt;
	}
	public void setRmt(String rmt) {
		this.rmt = rmt;
	}
	public String getRnp() {
		return rnp;
	}
	public void setRnp(String rnp) {
		this.rnp = rnp;
	}
	public String getRplct() {
		return rplct;
	}
	public void setRplct(String rplct) {
		this.rplct = rplct;
	}
	public String getRplctt() {
		return rplctt;
	}
	public void setRplctt(String rplctt) {
		this.rplctt = rplctt;
	}
	public String getRplm() {
		return rplm;
	}
	public void setRplm(String rplm) {
		this.rplm = rplm;
	}
	public String getRpsb() {
		return rpsb;
	}
	public void setRpsb(String rpsb) {
		this.rpsb = rpsb;
	}
	public String getRrcv() {
		return rrcv;
	}
	public void setRrcv(String rrcv) {
		this.rrcv = rrcv;
	}
	public String getRrmt() {
		return rrmt;
	}
	public void setRrmt(String rrmt) {
		this.rrmt = rrmt;
	}
	public String getRrsp() {
		return rrsp;
	}
	public void setRrsp(String rrsp) {
		this.rrsp = rrsp;
	}
	public String getRser() {
		return rser;
	}
	public void setRser(String rser) {
		this.rser = rser;
	}
	public String getRsidl() {
		return rsidl;
	}
	public void setRsidl(String rsidl) {
		this.rsidl = rsidl;
	}
	public String getRsidr() {
		return rsidr;
	}
	public void setRsidr(String rsidr) {
		this.rsidr = rsidr;
	}
	public String getRsno() {
		return rsno;
	}
	public void setRsno(String rsno) {
		this.rsno = rsno;
	}
	public String getRsp() {
		return rsp;
	}
	public void setRsp(String rsp) {
		this.rsp = rsp;
	}
	public String getRspasz() {
		return rspasz;
	}
	public void setRspasz(String rspasz) {
		this.rspasz = rspasz;
	}
	public String getRspatr() {
		return rspatr;
	}
	public void setRspatr(String rspatr) {
		this.rspatr = rspatr;
	}
	public String getRsssz() {
		return rsssz;
	}
	public void setRsssz(String rsssz) {
		this.rsssz = rsssz;
	}
	public String getRtls() {
		return rtls;
	}
	public void setRtls(String rtls) {
		this.rtls = rtls;
	}
	public String getRtmcr() {
		return rtmcr;
	}
	public void setRtmcr(String rtmcr) {
		this.rtmcr = rtmcr;
	}
	public String getRtmup() {
		return rtmup;
	}
	public void setRtmup(String rtmup) {
		this.rtmup = rtmup;
	}
	public String getRwfi() {
		return rwfi;
	}
	public void setRwfi(String rwfi) {
		this.rwfi = rwfi;
	}
	public String getSer() {
		return ser;
	}
	public void setSer(String ser) {
		this.ser = ser;
	}
	public String getSidl() {
		return sidl;
	}
	public void setSidl(String sidl) {
		this.sidl = sidl;
	}
	public String getSidr() {
		return sidr;
	}
	public void setSidr(String sidr) {
		this.sidr = sidr;
	}
	public String getSpasz() {
		return spasz;
	}
	public void setSpasz(String spasz) {
		this.spasz = spasz;
	}
	public String getSpatr() {
		return spatr;
	}
	public void setSpatr(String spatr) {
		this.spatr = spatr;
	}
	public String getStt() {
		return stt;
	}
	public void setStt(String stt) {
		this.stt = stt;
	}
	public String getTls() {
		return tls;
	}
	public void setTls(String tls) {
		this.tls = tls;
	}
	public String getTmac() {
		return tmac;
	}
	public void setTmac(String tmac) {
		this.tmac = tmac;
	}
	public String getTmcr() {
		return tmcr;
	}
	public void setTmcr(String tmcr) {
		this.tmcr = tmcr;
	}
	public String getTmem() {
		return tmem;
	}
	public void setTmem(String tmem) {
		this.tmem = tmem;
	}
	public String getTmim() {
		return tmim;
	}
	public void setTmim(String tmim) {
		this.tmim = tmim;
	}
	public String getTmup() {
		return tmup;
	}
	public void setTmup(String tmup) {
		this.tmup = tmup;
	}
	public String getTpip() {
		return tpip;
	}
	public void setTpip(String tpip) {
		this.tpip = tpip;
	}
	public String getTran() {
		return tran;
	}
	public void setTran(String tran) {
		this.tran = tran;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getWfi() {
		return wfi;
	}
	public void setWfi(String wfi) {
		this.wfi = wfi;
	}
	public String getWrk() {
		return wrk;
	}
	public void setWrk(String wrk) {
		this.wrk = wrk;
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		String newLine = System.getProperty("line.separator");

		result.append(newLine);

		//determine fields declared in this class only (no fields of superclass)
		Field[] fields = this.getClass().getDeclaredFields();

		//print field names paired with their values
		for ( Field field : fields  ) {
			result.append("  ");
			try {
				result.append( field.getName() );
				result.append(": ");
				//requires access to private field:
				result.append( field.get(this) );
			} catch ( IllegalAccessException ex ) {
				System.out.println(ex);
			}
			result.append(newLine);
		}
		return result.toString();
	}
	
	
	
	

}
