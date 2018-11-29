package rs.responses;

import java.lang.reflect.Field;
import java.util.HashMap;

public class QueryProgramResponse {

	String cc;
	String lrsdnt;
	String fp;
	String schd;
	String method;
	String tmcr;
	String dopt;
	String bmpt;
	String pgm;
	String dfnt;
	String gpsb;
	String tls;
	String mbr;
	String rgnt;
	HashMap<String, Message> messages;

	public String getCc() {
		return cc;
	}
	public void setCc(String cc) {
		this.cc = cc;
	}
	public String getLrsdnt() {
		return lrsdnt;
	}
	public void setLrsdnt(String lrsdnt) {
		this.lrsdnt = lrsdnt;
	}
	public String getFp() {
		return fp;
	}
	public void setFp(String fp) {
		this.fp = fp;
	}
	public String getSchd() {
		return schd;
	}
	public void setSchd(String schd) {
		this.schd = schd;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getTmcr() {
		return tmcr;
	}
	public void setTmcr(String tmcr) {
		this.tmcr = tmcr;
	}
	public String getDopt() {
		return dopt;
	}
	public void setDopt(String dopt) {
		this.dopt = dopt;
	}
	public String getBmpt() {
		return bmpt;
	}
	public void setBmpt(String bmpt) {
		this.bmpt = bmpt;
	}
	public String getPgm() {
		return pgm;
	}
	public void setPgm(String pgm) {
		this.pgm = pgm;
	}
	public String getDfnt() {
		return dfnt;
	}
	public void setDfnt(String dfnt) {
		this.dfnt = dfnt;
	}
	public String getGpsb() {
		return gpsb;
	}
	public void setGpsb(String gpsb) {
		this.gpsb = gpsb;
	}
	public String getTls() {
		return tls;
	}
	public void setTls(String tls) {
		this.tls = tls;
	}
	public String getMbr() {
		return mbr;
	}
	public void setMbr(String mbr) {
		this.mbr = mbr;
	}
	public String getRgnt() {
		return rgnt;
	}
	public void setRgnt(String rgnt) {
		this.rgnt = rgnt;
	}
	public HashMap<String, Message> getMessages() {
		return messages;
	}
	public void setMessages(HashMap<String, Message> messages) {
		this.messages = messages;
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
