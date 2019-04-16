# IMS Operations API

## Overview

The IMS Operations API is a service that provides REST APIs for managing the IMS resources required for IMS application deployment. The REST APIs invoke the IMS commands that are required to manage and query the IMS application resources.

You can use the IMS Operations API with the Zowe CLI Plug-in for IBM IMS. Used together, the REST APIs that are delivered with the IMS Operation API enable communication between the Zowe CLI and an IMS system and, therefore, enable you to use the Zowe CLI commands to interact with IMS resources. Alternatively, instead of using the Zowe CLI Plug-in for IMS, you can call the APIs from any REST client. 

Because the REST APIs can be called from a familiar environment, the learning curve and time that’s needed for you to develop and deliver business critical applications is significantly reduced. With the IMS Operations API, an application developer no longer needs to learn how to use ISPF editors, TSO, and SPOC for deploying and provisioning IMS application resources. 

Also, because you can write scripts that use the Zowe CLI commands, which, in turn, call the IMS Operations API, you can use the Zowe CLI commands and REST APIs to implement an automated CI/CD pipeline for building, delivering, and testing your IMS applications. 

## Use cases and scenario

You can use the REST APIs that are provided with IMS Operations API to perform the following tasks:

•	Create, define, manage, query, and delete IMS program resources for application programs.

•	Create, manage, query, and delete IMS transactions and transaction resources.

•	Start, stop, and display information about IMS message and application processing regions.

### Scenario: Updating an existing IMS application by using the IMS Operations API

Susan, an application developer, needs to add a new feature to an existing IMS application. To integrate and deliver the updated code for the new feature, she needs to refresh the application resources in the IMS system. 

To refresh existing IMS application resources, Susan issues the stop region API followed by the start region API:

•	Stop the IMS application region
<p><t><code>{host}:{port}/ims/apis/v1/{imsplex}/region/stop?jobname={jobname}</code>

•	Start the IMS application region
<p><t><code>{host}:{port}/ims/apis/v1/{imsplex}/region/start?jobname={jobname}</code>

•	Verify the application region status
<p><t><code>{host}:{port}/ims/apis/v1/{imsplex}/region</code>

If the transaction or program resource does not run properly, Susan might need to restart the resource. To do this, she can invoke the following APIs to start, stop, or query the transaction or program resources:

<p><t><code>{host}:{port}/ims/apis/v1/{imsplex}/pgm?names={appname}&show=STATUS</code>

<p><t><code>{host}:{port}/ims/apis/v1/{imsplex}/tran?names={traname}&show=STATUS</code>
  
## Procedure

To use the IMS Operations API to deploy IMS applications and manage application resources, perform the following high-level steps:

1.	Ensure that all prerequisites are met.
2.	Download the IMS Operations API.
3.	Install the IMS Operations API on your Java application server.
4.	View and test the IMS Operations API in Swagger UI.
5.	Call the IMS Operations API.

## Prequisites

Before you install the IMS Operations API, ensure that the following prerequisites are met:

•	IMS V14.1.0 or later is installed and running in your mainframe environment.

•	The IMS type-2 command environment is set up.

•	IMS Connect is configured and IMS Connect support for IMSplex is installed.

•	Maven is installed.

•	A Java web application server, such as WebSphere Liberty or Apache TomEE version 8.0.0-M1 or later, is installed.

## Downloading the IMS Operations API

Download and import the project ims from this Github repository into your preferred Integrated Development Environment (IDE), such as Eclipse, by using one of the following methods:

•	In the upper-right corner of this Github repository, click Clone or download > Download ZIP.
  ![clone repo image](https://github.ibm.com/ims/ims-operations-api/blob/master/wiki/clonepicture.png)
 
•	Issue the following Git command:
git clone https://github.com/zowe/ims-operations-api.git

## Installing the IMS Operations API

Install the IMS Operations API on a Java web application server. In this section, we provide instructions on how to install the service on either WebSphere Liberty or TomEE. 

### Installing on TomEE

•	To manually install the ims.war file on TomEE, copy the file to your TomEE webapps folder. Then, the .war file is automatically extracted in the same webapps folder.  
![tomcat webapp image](https://github.ibm.com/ims/ims-operations-api/blob/master/wiki/tomcatwebapp.png)
 
 
•	To install the ims.war file by using Tomcat Web Application Manager, perform the following steps:

1.	Ensure TomEE is started.
2.	If you are using Tomcat Web Application Manager for the first time, define your username and password in the $CATALINA_HOME$\conf\tomcat-users.xml file.
3.	In a web browser, enter the following URL:
http://localhost:8080/manager
4.	In the Deploy section, select the ims.war file, and then click Deploy. After the ims.war file is successfully deployed, the IMS Operations API is displayed in the Applications section of the Tomcat Web Application Manager.

### Installing on Websphere Liberty

To install the IMS Operations API on WebSphere Liberty, place the ims.war file in the dropins folder of your Liberty runtime. Then, when you start Liberty, the dropins folder is automatically scanned by Liberty, and the WAR files in the folder are automatically deployed.

## Viewing and Testing the IMS Operations API

You can view and test in Swagger UI, which you can access in a web browser, the REST APIs that are provided in the IMS Operations API. In Swagger UI, you can also see the documentation for the APIs.

1.	Ensure that the application server that you deployed the IMS Operations API on is started.
2.	In a web browser, enter the following URL:
http://{host}:{port}/ims
3.	To test an API, expand an endpoint, click Try it out, and specify the API parameters. Then, click Execute.  


## Calling the IMS Operations API

You can use one of the following methods to call the REST APIs that are provided with the IMS Operations API to deploy IMS applications and manage application resources:

•	By using the Zowe CLI plug-in for IBM IMS. For more information on how to download, install, and use the commands that are provided in the plug-in, see Zowe CLI plug-in for IBM IMS.

•	By using a REST client of your choice.
