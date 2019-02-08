/**
 *  Copyright IBM Corporation 2018, 2019
 */

package om.version;


/**
 * Class manages a version object for supported IMS Levels. The class has helper methods
 * for making comparisons simpler when determining the version of IMS we are working with.
 * 
 * <pre>
 * Example usage below:h
    public static void main(String[] args) {
    	String lineSeparator = System.getProperty("line.separator");
    	
    	Version someVersion = new Version("12.1.0");
    	Version otherVersion = new Version("12.1.1");
    	System.out.println("Compare " + someVersion + " to " + otherVersion + " returns: " + someVersion.compareTo(otherVersion)); // return -1 (a<b)
    	System.out.println("Is " + someVersion + " equal to " + otherVersion +" returns: " + someVersion.equals(otherVersion));    // return false
    	System.out.println("Is " + someVersion + " greater than " + otherVersion +" returns: " + someVersion.greater(otherVersion) + lineSeparator);  //false
    	
    	someVersion = new Version("12.0.0");
    	otherVersion = new Version("11.9.9");
    	System.out.println("Compare " + someVersion + " to " + otherVersion + " returns: " + someVersion.compareTo(otherVersion)); // return 1 (a>b)
    	System.out.println("Is " + someVersion + " equal to " + otherVersion +" returns: " + someVersion.equals(otherVersion));   // return false
    	System.out.println("Is " + someVersion + " greater than " + otherVersion +" returns: " +someVersion.greater(otherVersion)+ lineSeparator);  //true

    	someVersion = new Version("12.0.1");
    	otherVersion = new Version("12.0.0");
    	System.out.println("Compare " + someVersion + " to " + otherVersion + " returns: " +someVersion.compareTo(otherVersion)); // return 0 (a=b)
    	System.out.println("Is " + someVersion + " equal to " + otherVersion +" returns: " + someVersion.equals(otherVersion)) ;   // return true
    	System.out.println("Is " + someVersion + " greater than " + otherVersion +" returns: " +someVersion.greater(otherVersion)+ lineSeparator); //false

    	someVersion = new Version("12.0.0");
    	otherVersion = null;
    	System.out.println("Compare " + someVersion + " to " + otherVersion + " returns: " +someVersion.compareTo(otherVersion)); // return 1 (a>b)
    	someVersion.equals("Is " + someVersion + " equal to " + otherVersion +" returns: " + otherVersion) ;   // return false
    	System.out.println("Is " + someVersion + " greater than " + otherVersion +" returns: " +someVersion.greater(otherVersion)+ lineSeparator);  //false

    	
   	 	someVersion = new Version("12.01.0");
   	 	otherVersion = new Version("12.020.0");
   	 	System.out.println("Is " + someVersion + " equal to " + otherVersion +" returns: " + someVersion.equals(otherVersion));   // return false
   	 	System.out.println("Is " + someVersion + " greater than " + otherVersion +" returns: " +someVersion.greater(otherVersion));  //false
   	 	System.out.println("Is " + otherVersion + " greater than " + someVersion +" returns: " +otherVersion.greater(someVersion)+ lineSeparator);  //true
   	 
   	 	java.util.ArrayList<Version> versions = new java.util.ArrayList<Version>();
    	versions.add(new Version("13.1.0"));
    	versions.add(new Version("12.1.9"));
    	versions.add(new Version("12.1.13"));
    	versions.add(new Version("12.0.1"));
    	System.out.println("The collection of versions is made up of: " + versions);
    	System.out.println("The minimum version in the collection is: " + java.util.Collections.min(versions).get()); // return min version
    	System.out.println("The maxium version in the collection is: " +java.util.Collections.max(versions).get()+ lineSeparator); // return max version
    	 
    	System.out.println("Is " + IMS_V12_1_0 + " greater than " + IMS_V13_1_0 +" returns: " +IMS_V12_1_0.greater(IMS_V13_1_0));
    	System.out.println("Is " + IMS_V12_1_0 + " less than " + IMS_V13_1_0 +" returns: " +IMS_V12_1_0.less(IMS_V13_1_0)+ lineSeparator);
	
    	someVersion = new Version("12.1.0");
    	otherVersion = new Version("12.5.0");
    	System.out.println("Is " + someVersion + " inMajor of " + otherVersion +" returns: " + someVersion.inMajor(otherVersion)+ lineSeparator);
    
    	otherVersion = new Version("13.5.0");
    	System.out.println("Is " + someVersion + " inMajor of " + otherVersion +" returns: " + someVersion.inMajor(otherVersion)+ lineSeparator);
        
    	someVersion = new Version("12.5.0");
    	otherVersion = new Version("12.5.3");
    	System.out.println("Is " + someVersion + " inMinor of " + otherVersion +" returns: " + someVersion.inMinor(otherVersion)+ lineSeparator);
    
    	otherVersion = new Version("12.4.0");
    	System.out.println("Is " + someVersion + " inMinor of " + otherVersion +" returns: " + someVersion.inMinor(otherVersion)+ lineSeparator);
        
    	
    	try{
    		Version versionInvalid = new Version("12.2.2.2.2");
    	System.out.println(versionInvalid.get());
    	}catch (IllegalArgumentException e){
    		System.out.println(" Is not a valid version");
    	}
    }
 * </pre>
 */
public class Version implements Comparable<Version> {
	
	/**
	 * Zero value released is a base version that can be used for comparison
	 * instead of creating one multiple times, this is provided for you.
	 */
	public static final Version IMS_ZERO_RELEASE = new Version("0.0.0");
	public static final Version IMS_V12_1_0 = new Version("12.1.0");
	public static final Version IMS_V13_1_0 = new Version("13.1.0");
	public static final Version IMS_V14_1_0 = new Version("14.1.0");
	public static final Version IMS_V15_1_0 = new Version("15.1.0");
	public static final Version IMS_LATEST_RELEASE = IMS_V14_1_0;
	public static final Version IMS_MINIMUM_SUPPORTED_RELEASE = IMS_V12_1_0;
	
	/**
	 * The mimimum supported release of IMS for the Management console
	 */
	public static final Version IMS_MINIMUM_RELEASE = IMS_V12_1_0;
	
	
    private String version;

    /**
     * Return the version value. The version is a value which has two
     * decimals to match IMS's Major.Minor.Point (14.1.0).
     * @return
     */
    public final String get() {
        return this.version;
    }

    /**
     * <pre>
     * Constructor to instantiate the version. Versions must be in the format of Major.Minor.Point.
     * Example:
     * 		new Version("12.1.0")
     * 
     * @param version
     * </pre>
     */
    public Version(String version) throws IllegalArgumentException{
        if(version == null)
            throw new IllegalArgumentException("Version instantiated is null and is not allowed");
        	//Set it to the latest release in service. ????
        	//version = IMS_LATEST_RELEASE.get();
        if(!version.matches("\\d+\\.\\d+\\.\\d+")){  //("[0-9]+(\\.[0-9]+)*")
            throw new IllegalArgumentException("Version instantiated is of invalid format, format must be Major.Minor.Point ");
            //TODO: Could consider evaluating the value and then padding if within range, corner case is not really a good use of time
        }
        
        this.version = version;
    }

    public int compareTo(Version other) {
        if(other == null){
            return 1;
        }
        
        String[] thisVersionSplits 	= this.get().split("\\.");
        String[] otherVersionSplits	= other.get().split("\\.");
        
        int length = Math.max(thisVersionSplits.length, otherVersionSplits.length);
        
        for(int i = 0; i < length; i++) {
            int thisVersionSplit  = i < thisVersionSplits.length ? Integer.parseInt(thisVersionSplits[i]) : 0;
            int otherVersionSplit = i < otherVersionSplits.length ? Integer.parseInt(otherVersionSplits[i]) : 0;
            
            if(thisVersionSplit < otherVersionSplit){
                return -1;
            }
            
            if(thisVersionSplit > otherVersionSplit){
                return 1;
            }
        }
        return 0;
    }

    /**
     * Method will compare if some version is equal to another version. If some
     * version is equal it will return true, if the other version is null, it will
     * return false, if the objects being compared are not of equal class then 
     * false will be returned. 
     */
    @Override 
    public boolean equals(Object other) {
        if(this == other){
            return true;
        }
        
        if(other == null){
            return false;
        }
        
        if(this.getClass() != other.getClass()){
            return false;
        }
        
        return this.compareTo((Version) other) == 0;
    }
    
    /**
     * Method will compare some version against another version, if some version is 
     * greater than another version it will return true, else false. If the version is
     * equal it will also return false.
     * @param other
     * @return
     */
    public boolean greater(Version other){
    	if(this.compareTo(other) < 0 || this.compareTo(other) == 0){
    		return false;
    	}
    	return true;
    }
    
    /**
     * Method will compare some version against another version, if some version is 
     * less than another version, it will return true, else false. If the version is
     * equal it will return false;
     * @param other
     * @return
     */
    public boolean less(Version other){
    	if(this.compareTo(other) > 0 || this.compareTo(other) == 0){
    		return false;
    	}
    	return true;
    	
    }
    
    /**
     * Method will return true if some version is in the same Major release
     * as another version. For example, 12.1.0 is inMajor of 12.5.0 because
     * both are are of release 12 which is the Major of a a release.
     * @param other
     * @return
     */
    public boolean inMajor(Version other){
    	String[] thisVersionSplits 	= this.get().split("\\.");
        String[] otherVersionSplits	= other.get().split("\\.");
        
        if(thisVersionSplits[0].equals(otherVersionSplits[0])){
        	return true;
        }
        
    	return false;
    }
    
    /**
     * Method will return true if some version is in the same Major release
     * as another version. For example, 12.1.0 is inMajor of 12.5.0 because
     * both are are of release 12 which is the Major of a a release.
     * @param other
     * @return
     */
    public boolean inMinor(Version other){
    	String[] thisVersionSplits 	= this.get().split("\\.");
        String[] otherVersionSplits	= other.get().split("\\.");
        
        if(thisVersionSplits[0].equals(otherVersionSplits[0]) && thisVersionSplits[1].equals(otherVersionSplits[1])){
        	return true;
        }
        
    	return false;
    }
    
    @Override
    public String toString(){
    	return version.toString();
    }

/*    public static void main(String[] args) {
    	String lineSeparator = System.getProperty("line.separator");
    	
    	Version someVersion = new Version("12.1.0");
    	Version otherVersion = new Version("12.1.1");
    	System.out.println("Compare " + someVersion + " to " + otherVersion + " returns: " + someVersion.compareTo(otherVersion)); // return -1 (a<b)
    	System.out.println("Is " + someVersion + " equal to " + otherVersion +" returns: " + someVersion.equals(otherVersion));    // return false
    	System.out.println("Is " + someVersion + " greater than " + otherVersion +" returns: " + someVersion.greater(otherVersion) + lineSeparator);  //false
    	
    	someVersion = new Version("12.0.0");
    	otherVersion = new Version("11.9.9");
    	System.out.println("Compare " + someVersion + " to " + otherVersion + " returns: " + someVersion.compareTo(otherVersion)); // return 1 (a>b)
    	System.out.println("Is " + someVersion + " equal to " + otherVersion +" returns: " + someVersion.equals(otherVersion));   // return false
    	System.out.println("Is " + someVersion + " greater than " + otherVersion +" returns: " +someVersion.greater(otherVersion)+ lineSeparator);  //true

    	someVersion = new Version("12.0.1");
    	otherVersion = new Version("12.0.0");
    	System.out.println("Compare " + someVersion + " to " + otherVersion + " returns: " +someVersion.compareTo(otherVersion)); // return 0 (a=b)
    	System.out.println("Is " + someVersion + " equal to " + otherVersion +" returns: " + someVersion.equals(otherVersion)) ;   // return true
    	System.out.println("Is " + someVersion + " greater than " + otherVersion +" returns: " +someVersion.greater(otherVersion)+ lineSeparator); //false

    	someVersion = new Version("12.0.0");
    	otherVersion = null;
    	System.out.println("Compare " + someVersion + " to " + otherVersion + " returns: " +someVersion.compareTo(otherVersion)); // return 1 (a>b)
    	someVersion.equals("Is " + someVersion + " equal to " + otherVersion +" returns: " + otherVersion) ;   // return false
    	System.out.println("Is " + someVersion + " greater than " + otherVersion +" returns: " +someVersion.greater(otherVersion)+ lineSeparator);  //false

    	
   	 	someVersion = new Version("12.01.0");
   	 	otherVersion = new Version("12.020.0");
   	 	System.out.println("Is " + someVersion + " equal to " + otherVersion +" returns: " + someVersion.equals(otherVersion));   // return false
   	 	System.out.println("Is " + someVersion + " greater than " + otherVersion +" returns: " +someVersion.greater(otherVersion));  //false
   	 	System.out.println("Is " + otherVersion + " greater than " + someVersion +" returns: " +otherVersion.greater(someVersion)+ lineSeparator);  //true
   	 
   	 	java.util.ArrayList<Version> versions = new java.util.ArrayList<Version>();
    	versions.add(new Version("13.1.0"));
    	versions.add(new Version("12.1.9"));
    	versions.add(new Version("12.1.13"));
    	versions.add(new Version("12.0.1"));
    	System.out.println("The collection of versions is made up of: " + versions);
    	System.out.println("The minimum version in the collection is: " + java.util.Collections.min(versions).get()); // return min version
    	System.out.println("The maxium version in the collection is: " +java.util.Collections.max(versions).get()+ lineSeparator); // return max version
    	 
    	System.out.println("Is " + IMS_V12_1_0 + " greater than " + IMS_V13_1_0 +" returns: " +IMS_V12_1_0.greater(IMS_V13_1_0));
    	System.out.println("Is " + IMS_V12_1_0 + " less than " + IMS_V13_1_0 +" returns: " +IMS_V12_1_0.less(IMS_V13_1_0)+ lineSeparator);
	
    	someVersion = new Version("12.1.0");
    	otherVersion = new Version("12.5.0");
    	System.out.println("Is " + someVersion + " inMajor of " + otherVersion +" returns: " + someVersion.inMajor(otherVersion)+ lineSeparator);
    
    	otherVersion = new Version("13.5.0");
    	System.out.println("Is " + someVersion + " inMajor of " + otherVersion +" returns: " + someVersion.inMajor(otherVersion)+ lineSeparator);
        
    	someVersion = new Version("12.5.0");
    	otherVersion = new Version("12.5.3");
    	System.out.println("Is " + someVersion + " inMinor of " + otherVersion +" returns: " + someVersion.inMinor(otherVersion)+ lineSeparator);
    
    	otherVersion = new Version("12.4.0");
    	System.out.println("Is " + someVersion + " inMinor of " + otherVersion +" returns: " + someVersion.inMinor(otherVersion)+ lineSeparator);
        
    	
    	try{
    		Version versionInvalid = new Version("12.2.2.2.2");
    	System.out.println(versionInvalid.get());
    	}catch (IllegalArgumentException e){
    		System.out.println(" Is not a valid version");
    	}
    }*/
}	