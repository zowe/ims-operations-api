package rs.responses;

import java.util.List;

public class QueryProgramResponses {

	List<QueryProgramResponse> data;

	public List<QueryProgramResponse> getData() {
		return data;
	}

	public void setData(List<QueryProgramResponse> data) {
		this.data = data;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("Query Program Response Data");
		for (QueryProgramResponse q : data) {
			sb.append(q.toString());
		}
		
		return sb.toString();
	}
	
	
	
}
