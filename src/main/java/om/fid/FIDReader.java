/**
 *  Copyright IBM Corporation 2018, 2019
 */

package om.fid;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * This class contains methods needed to read a TYPE 1 DISPLAY COMMAND FID file. It
 * takes the contents of the file and builds it into a map. The keys of the map 
 * are the actual FIDs (eg. A00). The value is an ArrayList of integers. These integers are the space
 * mappings for the ResultSet. 
 * 
 * Example of processing: 
 * 
 * Example ResultSet:
 * S71 STRUCTURE NAME    TYPE  STATUS    ,
 * S30 IMSMSGQ01         MSGQ  CONNECTED, AVAILABLE    ,
 * S30 IMSEMHQ01         EMHQ  CONNECTED, AVAILABLE    ,
 * 
 * 
 * 1. Parse the header and make the properties object and add the keys. 
 *  
 * ResultSet first line = "S71 STRUCTURE NAME    TYPE  STATUS   "  
 * 
 * The first 4 characters are "S71 ". We parse these to consult the FIDMap. We then look at the first element in the ArrayList. 
 * It's 16. So the next 16 characters is the key "STRUCTURE NAME". We add key to properties object. Then we skip the remaining white spaces.
 * The next element is 4. So the next 4 characters is the key "TYPE". We add the key to the properties object, then skip remaining white 
 * spaces. Next element is 6. So "STATUS" is the next key etc.  
 * 
 * 
 * 2. Add values to the properties object 
 * 
 * The next line of the result set is: "S30 IMSMSGQ01         MSGQ  CONNECTED, AVAILABLE"
 * 
 * Using the same logic as before, we this time populate the values of the properties object. There
 * might be multiple S30 lines, so we dynamically generate as many properties objects as we need. 
 * 
 * In the end we return a properties object array that could look like this:
 * 		[   {"Structure name"    : "IMSMSGQ01"
 * 				"Type			 : "MSGQ
 * 				"Status			 : "Connected, Available"
 * 	   		}
 * 			,
 * 	   		{"Structure name"    : "IMSEMHQ01"
 * 			 "Type			     : "EMHQ
 * 			 "Status			 : "Connected, Available"
 * 	   		}
 * 		]
 * 
 * @author Jerry Li
 *
 */
public class FIDReader {

	//macro file path, the file with space mappings to parse
	public static String filePath = "";

	//Map that holds fid space mappings 
	public ConcurrentHashMap<String, ArrayList<Integer>> FIDMap = new ConcurrentHashMap<String, ArrayList<Integer>>();

	//For continuation lines. Maps correct header fids to value fids. Otherwise would not which values belong
	//to which keys.
	public HashMap<String, String> fidToFid = new HashMap<String, String>();

	/**
	 * Set the filepath and automatically build objects.
	 * @param path
	 */
	public FIDReader(String path) {
		setFilePath(path);
		buildFIDObjects();
	}

	/**
	 * This method takes in a String array of results, gleaned from the resultSet. 
	 * Remove static when done testing
	 * @param results
	 * @param mbr
	 * @return
	 */
	public Properties[] parseResults(String[] results, String mbr, String command) {

		//System.out.println("raw response from imscon: " + Arrays.deepToString(results));
		ArrayList<Properties> responseProps = new ArrayList<Properties>();

		/*
		 * If results are less than 2, that means error message. We treat error messages differently in
		 * that we don't put them into a bean. 
		 */
		if (results.length < 2) {
			Properties error = new Properties();
			error.put(mbr, results[0]);
			responseProps.add(error);
			Properties[] responseErrorArray = listToArray(responseProps);
			return responseErrorArray;

		}

		//Calculate the number of header lines (these lines contain keys, other lines contain values).
		int numOfHeaders = (processFids(results, command));

		//Holds all the keyIndexes. Each headerline has a keyIndex arraylist integer mapping. 
		HashMap<String, ArrayList<String>> keyIndexes = new HashMap<String, ArrayList<String>>();

		//We build up the keys. The keys are in the FID header lines. Need to parse them out. So
		//we need th FIDe header space mappings to get the key, to do so......
		String firstFid = "";
		for (int a =0 ; a <numOfHeaders; a++) {

			ArrayList<String> keyIndex = new ArrayList<String>();
			//...we need to get header FID. This is the first 4 characters of string (includes extra space at the end)
			String fid = results[0+a].substring(0, 4);
			if (firstFid.isEmpty()) {
				firstFid = fid;
			}
			//Edge case (1): If there's a bunch of spaces following fid in line, that means one of the 
			//keys is from the above line. need to check for that. See DISPLAY OTMA command FIDs T88 and T96
			//(1) only way to check for this edge case is to check for a lot of consecutive blanks. 
			/*
			 * Example:
			 * T88 GROUP/MEMBER      XCF-STATUS   USER-STATUS    SECURITY   TIB  INPT SMEM
			   T96                     DRUEXIT  T/O TPCNT ACEEAGE MAXTP  
			   (notice spaces after T96. That means T96 uses T88's "GROUP/MEMBER" key       
			 */
			if (results[0+a].substring(4, results[0+a].length()).charAt(4) == ' ') {
				keyIndex.add(keyIndexes.get(firstFid).get(0));
			}
			String remain = results[0+a].substring(4, results[0+a].length()).trim();
			//Retrieve the correct header fid spacings to retrieve keys. 
			ArrayList<Integer> mapping = FIDMap.get(fid.trim());

			/*
			 * Now we have the space mappings for the header FID. 
			 */

			/*
			 * Construct key index. This is needed when we start adding values. Why not pre-create prop objects and add keys 
			 * with empty values? A sort of lazy load? Then put values in later? This is fine for a few values, but not 
			 * ideal for lots of values because have to search the Props array constantly. With a lot of values it's
			 * easier to consult a key index and dynamically generate and add Prop objects when needed. 
			 */
			for (int i = 0; i < mapping.size(); i++) {
				int numSpaces = mapping.get(i);
				String key = "";
				//If last key, instead of parsing remaining string just get it.
				if (i == mapping.size()-1) {
					key = remain;
				}
				else {

					//If last key, but spacing defines more, just get last key and
					//set remain to empty. This will create empty keys, which will be
					//filtered out in the next step. 
					if (remain.length() < numSpaces) {
						key = remain;
						remain = "";
					}
					//Else, parse the remaing string using spacings provided
					else {
						key = (remain.substring(0, numSpaces));
						remain = remain.substring(numSpaces);
					}
				}
				/*
				 * Here we begin key modification to make the map work with the beans. Lots of specific cases 
				 * here. Empty keys before filtered out here. 
				 */
				if (!key.trim().isEmpty()) {
					//If contain dash, replace with underscore
					if (key.contains("-")) {
						key = key.replace('-', '_');
					}
					//If key is "STRUCTURE NAME" we must add underscore because enums can't have spaces.
					//NOTE: Can implement something in the future where if space replace with underscore. 
					else if (key.trim().equals("STRUCTURE NAME")) {
						key = "STRUCTURE_NAME";
					}
					//If "GROUP/MEMBER" replace with MEMBER, as that's what the command is really looking
					//for. See "DISPLAY OTMA" FID T96
					else if (key.trim().equals("GROUP/MEMBER")) {
						key = "MEMBER";
					}
					//If key contains slash replace with empty. Enums can't handle slashes. 
					else if(key.contains("/")) {
						key = key.replace("/", "");
					}

					//After all is said and done add to keyIndex.
					keyIndex.add(key.trim());
				}
			}
			keyIndexes.put(fid, keyIndex);
		}


		//Add values now
		//loop through results set array
		outerloop:
			for (int j = numOfHeaders; j <results.length; j++) {
				//System.out.println(results[j]);
				//if result line contains asterisk it's not valid data
				if (results[j].contains("*")){
					break outerloop;
				}
				Properties propObj = new Properties();
				//retrieve the correct FID map:
				String valueFid = results[j].substring(0, 4);
				String valueRemain = results[j].substring(4, results[j].length());
				String headerFid = fidToFid.get(valueFid);
				ArrayList<Integer> valueMappings = FIDMap.get(valueFid.trim()); 

				//loop through array mappings to insert values
				for (int k = 0; k < keyIndexes.get(headerFid).size(); k++) {
					//Sometimes keys don't have a value set. We check if all 
					//the values are extracted. If so, there will be unpopulated keys. We 
					//set those keys with empty values. Example: DRUEXIT="", TIB="" etc.
					if (valueMappings.size()-1 < k) {
						propObj.put(keyIndexes.get(headerFid).get(k), "");
						continue;
					}
					int numSpaces = valueMappings.get(k);
					String value =""; 
					//If this is last value, just get the remaining string and trim. This avoids potential
					//spacing errors (FID map specifying more spaces than actually there)
					if (k == valueMappings.size()-1 || valueRemain.length() <= numSpaces) {
						value = valueRemain.trim();
						propObj.put(keyIndexes.get(headerFid).get(k), value.trim());
						valueRemain = "";
					}
					//otherwise parse the corect value. 
					else {
						value = (valueRemain.substring(0, numSpaces));
						valueRemain = valueRemain.substring(numSpaces);
					}

					propObj.put(keyIndexes.get(headerFid).get(k), value.trim());
				}
				//Finally we append the correct member (passed in from Service)
				propObj.put("MBR", mbr);

				//add to Properties array
				responseProps.add(propObj);

			}
		//If this is DIS OTMA command (meaning it has continuation lines)
		//we need to combine the continuation objects. Right now, each continuation line
		//with its values are a separate props object. We need to combine them into one object.
		if (command.contains("DIS") && command.contains("OTMA")) {
			return listToArray(coalesceContinuationLine(responseProps));
		}
		Properties[] responsePropsArray = listToArray(responseProps);
		return responsePropsArray;
	}

	/**
	 * Combines separate props objects under the same command into one props object.
	 * @param otmaList
	 * @return
	 */
	public static ArrayList<Properties> coalesceContinuationLine(ArrayList<Properties> otmaList) {

		ArrayList<Properties> combinedOTMAList = new ArrayList<Properties>();


		Properties newOtma = new Properties();
		String group = "";
		String member = "";
		for(int i = 0; i <otmaList.size(); i++) {
			String value = otmaList.get(i).getProperty("MEMBER");
			//If this doesn't hyphen, it's a "GROUP"
			if (!value.contains("-")) {
				group = value;
			}
			else if (value.contains("-")) {
				//else it's a "MEMBER"
				if (member.equals(value)) {
					newOtma.putAll(otmaList.get(i));
					combinedOTMAList.add(newOtma);
				}
				else {
					member = value;
					newOtma = new Properties();
					newOtma.putAll(otmaList.get(i));
					newOtma.put("GROUP", group);
				}
			}
		}
		return combinedOTMAList;


	}

	/**
	 * This builds the map of FID information. We're parsing a large file with
	 * a lot of information. Added comments where needed
	 * needed. 
	 * @param fileName
	 */
	public void buildFIDObjects() {
		
		
		//Get file
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(filePath);
		Scanner input = new Scanner(is);

		//Read each line
		while (input.hasNextLine()) {
			String line = input.nextLine();
			/*
			 * We separate the file by specific lines. The lines are the 
			 * header of each segment
			 * Example: "*        S30 - /DISPLAY STRUCTURE etc."
			 * We're looking for this specific format "XXX - /"
			 */
			if (line.matches(".*[A-Z](\\d\\d)(\\s)[-](\\s)[/D].*")) {
				String key = "";
				//We parse the FID designation in the line we found and add the key to the map
				Pattern pattern = Pattern.compile("(\\w\\d\\d)");
				Matcher matcher = pattern.matcher(line);
				if (matcher.find()) {
					key = matcher.group(1);
					FIDMap.put(key, new ArrayList<Integer>());
				}

				/*
				 * You may be asking, why not use the content to get the FID, such as this line right here:
				 * "S30FID   DS    CL3                 *FORMAT ID = S30" 
				 * The problem is that "*FORMAT ID" is not always there. It could be "*FID = S30*" or even just
				 * "*FID". The header lines are always consistent, so it's safer to get the FID designation from there. 
				 * We're only trying to get the keys for the FID map, we don't need spacing information because FID will 
				 * always be 3 spaces and at the beginning if each line of the results set.  
				 */

				/*
				 * This is so we skip the next line of asterisks. Asterisks are our delimiter here. The FID file uses
				 *two lines of asterisks and a header line sandwiched in between to organize the file. This puts a wrinkle 
				 *in our logic. We have to ignore one of the lines of asterisks (in this case, the bottom one)
				 *or else our next while loop will break before even starting.
				 */ 
				while (line.matches(("\\*.*"))){
					line = input.nextLine();
				}


				/*
				 *Now we add ArrayList values to the FID keys. This part is tricky. Some spacing info we don't need, so we have 
				 *to extract the ones we do. This requires coding in some specific flags. Easy to break. Unfortunately
				 *no idea how consistent the FID file contents are, and its a huge file so going through it will take a while.
				 *A quick run through the file suggests the following code will work.
				 */
				boolean hadBlankPreviously = false;
				while(input.hasNextLine()) {
					line = input.nextLine();
					boolean hasBlanks = false;
					//Relevant lines include this string: "DS    CL(XX)" These lines contain the 
					//important character spacing information. This narrows down the lines we need.
					if (line.matches("(.*DS)(\\s\\s\\s\\s)(C.*)") || line.matches("(.*DS)(\\s\\s\\s\\s\\d)(C.*)")) {
						//							try {
						if (line.contains("*BLANK") || line.contains("*RESERVED") || line.contains("* BLANK") || line.contains("*   ")
								|| line.contains("*TWO BLANKS") || line.contains("*ONE BLANK")) {
							hasBlanks = true;
						}
						//These strings are stuff that we don't need, so we skip these lines that contain
						//these flags. This part of the code is the most fragile, don't know if there are other 
						//flags to keep an eye out. 
						if (line.contains("*LLZZ") || line.contains("*X'15'") ||
								line.contains("*FORMAT ID") || line.contains("*FID")) {
							continue;
						}

						//Everything else we want, so we add to the ArrayList
						else {
							pattern = Pattern.compile("[C][L](\\d.)");
							matcher = pattern.matcher(line);
							Pattern pattern2 = Pattern.compile("(\\s\\s)[C](\\s\\s.)");
							Matcher matcher2 = pattern2.matcher(line);
							Pattern pattern3 = Pattern.compile("(.\\d)[C](\\s\\s\\s\\s.)");
							Matcher matcher3 = pattern3.matcher(line);
							ArrayList<Integer> indexList = FIDMap.get(key);
							/*The next bit of code is probably the messiest you will see. The FID Mappings are maddeningly inconsistent when it 
							 *describes blanks. Sometimes it will denote it with "*Blank". Sometimes it says "* Blank". Sometimes it says "* BLANKS".
							 *Sometimes it says nothing at all "*   ". The worst part is the "*    " designation can mean a blank OR spaces that
							 *may or may not be actually used! "*   " designations may be followed with actual blanks and vice versa. As if this 
							 *wasn't enough to worry about, the number designations change. Sometimes it's CLX where X denotes a number, sometimes it's just C,
							 *and sometimes it's XC, where X denotes the number! There is no consistency. It's dangerously inconsistent and this code is the most 
							 *fragile in the class. This is the best I can make out of a random non-well formed document. You have been warned. 
							 */

							//If line has "CLX" pattern where "X" denotes integer
							if (matcher.find()) {
								//If this is a blank and previous line was not blank
								if (hasBlanks && !hadBlankPreviously) {
									//add blank spacing to previous spacing (so we skip the blanks when parsing)
									if (indexList.size()>0) {
										int tempValue = indexList.get(indexList.size()-1);
										int newValue = tempValue + Integer.parseInt(matcher.group(1).trim());
										indexList.set(indexList.size()-1, newValue);
									}
									hadBlankPreviously = true;
								}
								//else just add the spacing to the map. So if this line is blank AND previous line was 
								//blank, this line actually must be valid. I know right....
								else {
									FIDMap.get(key).add(Integer.parseInt(matcher.group(1).trim()));
									hadBlankPreviously = false;
								}
							}
							//If line has "C" pattern. So far, all of these patterns mean it's some sort of blank
							else if (matcher2.find()) {
								//Add to previous spacing
								if (indexList.size()>0) {
									int tempValue = indexList.get(indexList.size()-1);
									int newValue;
									if (line.contains("*TWO BLANKS")) {
										newValue = tempValue + 2;
									}
									newValue = tempValue + 1;
									indexList.set(indexList.size()-1, newValue);
								}
								hadBlankPreviously = true;
							}
							//If line has "XC" pattern where "X" denotes integer
							else if (matcher3.find()) {
								//If line is blank and did not have blank previously, add
								//to previous spacing
								if (hasBlanks && !hadBlankPreviously) {
									if (indexList.size()>0) {
										int tempValue = indexList.get(indexList.size()-1);
										int newValue = tempValue + Integer.parseInt(matcher3.group(1).trim());
										indexList.set(indexList.size()-1, newValue);
									}
									hadBlankPreviously = true;
								}
								//else just add the spacing to the map
								else {
									FIDMap.get(key).add(Integer.parseInt(matcher3.group(1).trim()));
									hadBlankPreviously = false;
								}

							}
						}

					}

					//Asterisks are our delimiter. We break from this while loop section when we encounter an asterisks line. 
					else if (line.contains("***********")) {
						break;
					}

				}

			}

		}

		input.close();
	}

	/**
	 * Sets the file path. 
	 * @param path
	 */
	public static void setFilePath(String path) {
		filePath = path;
	}

	/**
	 * Returns file path
	 * @return
	 */
	public static String getFilePath() {
		return filePath;
	}

	/**
	 * Converts ArrayList<Properties> to Properties[]
	 * @param responseProps
	 * @return
	 */
	public static Properties[] listToArray(ArrayList<Properties> responseProps) {
		Properties[] responsePropsArray = new Properties[responseProps.size()];
		for (int z = 0; z <responsePropsArray.length; z ++) {
			responsePropsArray[z] = responseProps.get(z);
		}
		return responsePropsArray;
	}

	/**
	 * This number scans the number of unique FID's. This is needed because some results 
	 * have multiple headers, such as a main header then a sub header. It also maps value fids 
	 * to corresponding header fids. It returns the number of headers. 
	 * Remove static when done testing
	 * @param results
	 * @return
	 */
	public int processFids(String[] results, String command)	 {
		
		//Eliminate duplicate FIDs
		ArrayList<String> uniqueFids = new ArrayList<String>();
		for (int i = 0; i < results.length; i++) {
			if (!results[i].contains("*")) {
				String valueFid = results[i].substring(0, 4);
				if (!uniqueFids.contains(valueFid)){
					uniqueFids.add(valueFid);
				}
			}
		}
		
		//If OTMA command (or any other continuation line commandin the future,
		//must map header fids to value fids
		if (command.contains("OTMA")) {

			ArrayList<String> processedFids = new ArrayList<String>();
			String[] fidArray = new String[uniqueFids.size()];
			for (int j = 0; j<uniqueFids.size();j++) {
				fidArray[j] = uniqueFids.get(j);
			}

			//Reverse array because we want to map values to headers, not the other way around
			//Remember, we need to get the HEADER fid when building props objects, because we
			//would already have the value fid. 
			for (int j = 0; j<(fidArray.length)/2; j++) {
				String tmp = fidArray[j];
				fidArray[j] = fidArray[fidArray.length-1-j];
				fidArray[fidArray.length-1-j] = tmp;

			}

			int limit = ((fidArray.length)/2);
			for (int i =0; i<limit-1; i++){
				fidToFid.put(fidArray[i], "");
			}

			int count = 0;
			for (int k = limit; k<fidArray.length; k++) {
				if (!processedFids.contains(fidArray[k])) {
					fidToFid.put(fidArray[count], fidArray[k]);
					processedFids.add(fidArray[k]);
					count++;
				}
			}

			return (uniqueFids.size())/2;
		}
		//Else, if just one header fid, map this to other fids. 
		else {
			String headerFid = uniqueFids.get(0);
			for (int p = 1; p < uniqueFids.size(); p++) {
				fidToFid.put(uniqueFids.get(p), headerFid);
			}
			return 1;
		}
	}


	//	/**
	//	 * For testing, delete when finished
	//	 * @param args
	//	 */
	//	public static void main (String[] args) {
	//		//A20 = A50, A31 = A55, A33 = A60
	//		String[] processFidTest = new String[] {"A20 ", "A31 ", "A33 ", "A50 ", "A55 ", "A60 "};
	//		int numFids = processFids(processFidTest, "DIS OTMA OPTION = AOPOUTPUT");
	//		assert numFids == 6;
	//
	//		String command = "(DISPLAY OTMA) OPTION=AOPOUTPUT";
	//		String[] resultTest = new String[10];
	//		resultTest[0] = "T88 GROUP/MEMBER      XCF-STATUS   USER-STATUS    SECURITY   TIB  INPT SMEM     ";
	//		resultTest[1] = "T96                     DRUEXIT  T/O TPCNT ACEEAGE MAXTP                                                                       ";
	//		resultTest[2] = "T27 XCFGRP1 ";
	//		resultTest[3] = "T27 -IMS2             ACTIVE       SERVER         FULL         0 10000     ";
	//		resultTest[4] = "T38 -IMS2               N/A        0     0 N/A         0         ";
	//		resultTest[5] = "T27 -HWS1B            ACTIVE       ACCEPT TRAFFIC FULL         0  5000 ";
	//		resultTest[6] = "T38 -HWS1B              HWSYDRU0 120     0  999999     0        ";
	//		resultTest[7] = "T27 -HWS2B            ACTIVE       ACCEPT TRAFFIC FULL         0  5000 ";
	//		resultTest[8] = "T38 -HWS2B              HWSYDRU0 120     0  999999     0   ";
	//		resultTest[9]= "X99 *14252/144026*";
	//
	//		String command2 = "(DISPLAY STRUC ALL) OPTION=AOPOUTPUT";
	//		String[] resultTest2 = new String[4];
	//		resultTest2[0] = "S71 STRUCTURE NAME    TYPE  STATUS";
	//		resultTest2[1] = "S30 IMSMSGQ01         MSGQ  CONNECTED, AVAILABLE";
	//		resultTest2[2] = "S30 IMSEMHQ01         EMHQ  CONNECTED, AVAILABLE";
	//		resultTest2[3] = "X99 *14275/121332*";
	//
	//		String command3 = "(DISPLAY ACT REGION) OPTION = AOPOUTPUT";
	//		String[] resultTest3 = new String[12];
	//		resultTest3[0] = "A70 REGID JOBNAME   TYPE  TRAN/STEP PROGRAM  STATUS           CLASS";
	//		resultTest3[1] = "A00       DLISDEP   DLS"; 
	//		resultTest3[2] = "A52     2 MPP02     TP                       WAITING            3,  3,  3,  3";
	//		resultTest3[3] = "A52     1 MPP01     TP                       WAITING            3,  3,  3,  3";
	//		resultTest3[4] = "A53       JMPRGN    JMP   NONE";
	//		resultTest3[5] = "A54       JBPRGN    JBP   NONE";
	//		resultTest3[6] = "A54       BATCHREG  BMP   NONE";
	//		resultTest3[7] = "A55       FPRGN     FP    NONE";
	//		resultTest3[8] = "A62       DBTRGN    DBT   NONE";
	//		resultTest3[9] = "A00       DBRACSAM  DBRC";                    
	//		resultTest3[10] = "A00       DLISDEP   DLS";  
	//		resultTest3[11] = "X99 *14275/155950* "; 
	//
	//
	//		buildFIDObjects();
	//		Properties[] results = parseResults(resultTest, "1", command);
	//		System.out.println(results);
	//
	//
	//
	//
	//	}
}
