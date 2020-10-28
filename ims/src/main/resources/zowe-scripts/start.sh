#!/bin/sh

################################################################################
# This program and the accompanying materials are made available under the terms of the
# Eclipse Public License v2.0 which accompanies this distribution, and is available at
# https://www.eclipse.org/legal/epl-v20.html
#
# SPDX-License-Identifier: EPL-2.0
#
# Copyright IBM Corporation 2020
################################################################################

# Variables required on shell:
# - ZOWE_PREFIX
# - ZOWE_IMS_OPS_API_PORT - The port on which the Springboot server listens
# - ZOWE_EXPLORER_HOST 
# - ZOWE_IP_ADDRESS
# - DISCOVERY_PORT - The port the APIML Discovery server is listening (that the IMS Ops API registers with)
# - KEYSTORE - The keystore to use for SSL certificates
# - KEYSTORE_TYPE - The keystore type to use for SSL certificates
# - KEYSTORE_PASSWORD - The password to access the keystore supplied by KEYSTORE
# - KEY_ALIAS - The alias of the key within the keystore
# - TRUSTSTORE - The truststore to use for SSL certificates
# - TRUSTSTORE_PASSWORD - The password to access the truststore supplied by TRUSTSTORE

IMS_OPS_CODE=IM

 _BPX_JOBNAME=${ZOWE_PREFIX}${IMS_OPS_CODE} java -Xms16m -Xmx512m \
	-Dapiml.service.register=true \
	-Dapiml.service.id=IMS \
	-Dservice.title=IMS_Operations \
	-Dservice.description=IMS_Operations_API \
	-Dapiml.service.port=${ZWE_IMS_OPS_API_PORT} \
	-Dapiml.service.ipAddress=${ZOWE_IP_ADDRESS} \
	-Dapiml.service.scheme=https \
	-Dapiml.service.hostname=${ZOWE_EXPLORER_HOST} \
	-Dapiml.service.contextPath=/api/v1/ims \
	-Dapiml.service.discoveryServiceUrls=${ZWE_DISCOVERY_SERVICES_LIST} \
	-Dserver.ssl.protocol="TLSv1.2" \
	-Dserver.ssl.enabled=true \
	-Dserver.ssl.keyStore=${KEYSTORE} \
	-Dserver.ssl.keyStoreType=${KEYSTORE_TYPE} \
	-Dserver.ssl.keyStorePassword=${KEYSTORE_PASSWORD} \
	-Dserver.ssl.keyAlias=${KEY_ALIAS} \
	-Dserver.ssl.keyPassword=${KEYSTORE_PASSWORD} \
	-Dserver.ssl.trustStore=${TRUSTSTORE} \
	-Dserver.ssl.trustStoreType=${KEYSTORE_TYPE} \
	-Dserver.ssl.trustStorePassword=${KEYSTORE_PASSWORD} \
	-jar ${LAUNCH_COMPONENT}/../ims-ops-api-1.0.0.jar &