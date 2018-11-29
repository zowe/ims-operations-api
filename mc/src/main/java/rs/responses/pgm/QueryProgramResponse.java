package rs.responses.pgm;

import java.lang.reflect.Field;

import io.swagger.v3.oas.annotations.media.Schema;

public class QueryProgramResponse {

	@Schema(description = "Completion Code")
	String cc;
	@Schema(description = "Local runtime value of the resident option. Indicates whether the program PSB resides in local storage.")
	String lrsdnt;
	String fp;
	String schd;
	String tmcr;
	String dopt;
	String bmpt;
	String pgm;
	String dfnt;
	String gpsb;
	String tls;
	String mbr;
	String rgnt;

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
