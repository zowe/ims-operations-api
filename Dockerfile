FROM websphere-liberty:webProfile8

COPY --chown=1001:0  target/ims.war /config/dropins/
COPY --chown=1001:0  server.xml /config/
ENV LICENSE accept

# add the files we require, jar + WLP files<br>
#ADD wlp-developers-extended-8.5.5.2.jar /root/<br>
#ADD wlp-developers-runtime-8.5.5.2.jar /root/<br>
#ADD JAXWSEJBSample.jar /root/<br>

# install the WLP<br>
#RUN apt-get update<br>
#RUN apt-get install -y default-jre<br>
#RUN java -jar /root/wlp-developers-runtime-8.5.5.2.jar --acceptLicense /root/<br>
#RUN java -jar /root/wlp-developers-extended-8.5.5.2.jar --acceptLicense /root/<br>
#RUN cd /root/wlp &amp;&amp; java -jar ../JAXWSEJBSample.jar /root/wlp<br>

#EXPOSE 9080<br> # Exposes the container's ports -- 9080 is the port WLP uses
#CMD /root/wlp/bin/server run JAXWSEJBSample<br>
