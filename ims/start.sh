## This is a sample for running the .jar file on a z/OS system.  
## Update as necessary, until we get this incorporated into the Zowe started task

java -Xms16m -Xmx512m \
	-Dapiml.service.id=zowe.org.ims.operations.api \
	-Dservice.title=IMS_Operations \
	-Dservice.description=IMS_Operations_API \
	-Dapiml.service.port=26510 \
	-Dapiml.service.ipAddress=9.20.5.48 \
	-Dapiml.service.scheme=https \
	-Dapiml.service.hostname=winmvs3b.hursley.ibm.com \
	-Dapiml.service.contextPath=/api/v1/ims \
	-Dapiml.service.discoveryServiceUrls=https://winmvs3b.hursley.ibm.com:26501/eureka/ \
	-Dserver.ssl.keyStore=/u/winchj/zowe-keystore-111/localhost/localhost.keystore.p12 \
	-Dserver.ssl.keyPassword=password \
	-Dserver.ssl.keyStorePassword=password \
	-Dserver.ssl.trustStore=/u/winchj/zowe-keystore-111/localhost/localhost.truststore.p12 \
	-Dserver.ssl.keyAlias=localhost \
	-Dserver.ssl.trustStorePassword=password \
	-Dserver.ssl.keyStoreType="PKCS12" \
	-Dserver.ssl.trustStoreType="PKCS12" \
	-jar zowe-ims-api-1.0.2.jar
	
