FROM websphere-liberty:webProfile8

RUN liberty/wlp/bin/installUtility install mdb-3.2
RUN liberty/wlp/bin/installUtility install ejb-3.2

COPY key.jks /config/resources/security/
COPY --chown=1001:0  target/ims.war /config/apps/
COPY --chown=1001:0  server.xml /config/
ENV LICENSE accept