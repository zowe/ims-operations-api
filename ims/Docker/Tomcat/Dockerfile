FROM tomee:11-jre-8.0.0-M3-plus

MAINTAINER jerryli

COPY ims.war /usr/local/tomee/webapps/
COPY tomcat-users.xml /usr/local/tomee/conf
COPY context.xml /usr/local/tomee/webapps/manager/META-INF/context.xml
COPY context.xml /usr/local/tomee/webapps/host-manager/META-INF/context.xml
COPY context.xml /usr/local/tomee/ims/host-manager/META-INF/context.xml
