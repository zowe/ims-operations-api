/**
 *  Copyright IBM Corporation 2018, 2019
 */

package application.rest.responses.pgm.start;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import io.swagger.v3.oas.annotations.media.Schema;

public class StartProgram {

	@Schema(description = "A map that represents the JSON data response from a START PGM program. The key is the key in the JSON object, usually the IMS mbr. The value is the message")
	Map<String, String> mbr = new HashMap<>();

	@JsonAnySetter
	public void set(String key, String value) {
		this.mbr.put(key, value);
	}

	@JsonAnyGetter
	public Map<String, String> get() {
		return this.mbr;
	}

}
