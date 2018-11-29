package rs.responses.pgm;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

public class StartProgramResponse {

	private Map<String, String> mbr = new HashMap<>();


	@JsonAnySetter
	public void set(String key, String value) {
		mbr.put(key, value);
	}

	@JsonAnyGetter
	public Map<String, String> get() {
		return this.mbr;
	}

}
