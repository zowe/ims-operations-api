
/**
* This program and the accompanying materials are made available under the terms of the
* Eclipse Public License v2.0 which accompanies this distribution, and is available at
* https://www.eclipse.org/legal/epl-v20.html
*
* SPDX-License-Identifier: EPL-2.0
*
* Copyright IBM Corporation 2019
*/

package application.rest.responses.tran.create;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "POJO from a Create TRAN command that represents output for one transaction")
public class CreateTransaction {
	
	@Schema(description = "Completion Code")
	String cc;
	@Schema(description = "Completion code text that briefly explains the meaning of the non-zero completion code.")
	String cctxt;
	@Schema(description = "IMSplex member that built the output line.")
	String mbr;
	@Schema(description = "Transaction Name")
	String tran;
	
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
	public String getMbr() {
		return mbr;
	}
	public void setMbr(String mbr) {
		this.mbr = mbr;
	}
	public String getTran() {
		return tran;
	}
	public void setTran(String tran) {
		this.tran = tran;
	}
	
	
	
	
	

}
