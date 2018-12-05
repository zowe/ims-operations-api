package application.rest.responses.pgm;

import java.lang.reflect.Field;

public class UpdateProgramsResponse {
	
	String cc;
	String cctxt;
	String errt;
	String job;
	String mbr;
	String pgm;
	String rgnn;
	String rgnt;
	
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
	public String getErrt() {
		return errt;
	}
	public void setErrt(String errt) {
		this.errt = errt;
	}
	public String getJob() {
		return job;
	}
	public void setJob(String job) {
		this.job = job;
	}
	public String getMbr() {
		return mbr;
	}
	public void setMbr(String mbr) {
		this.mbr = mbr;
	}
	public String getPgm() {
		return pgm;
	}
	public void setPgm(String pgm) {
		this.pgm = pgm;
	}
	public String getRgnn() {
		return rgnn;
	}
	public void setRgnn(String rgnn) {
		this.rgnn = rgnn;
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
