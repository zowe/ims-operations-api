# IMS Operations API

## Overview

The IMS Operations API is a service that provides REST APIs to manage the IMS resources required for IMS application deployment. The REST APIs invoke the IMS commands that are required to manage and query the IMS application resources.

You can use the IMS Operations API with the [Zowe CLI Plug-in for IBM IMS](https://github.com/zowe/zowe-cli-ims-plugin). Used together, the REST APIs that are delivered with the IMS Operation API enable communication between the Zowe CLI and an IMS system and, therefore, enable you to use the Zowe CLI commands to interact with IMS resources. Alternatively, instead of using the Zowe CLI Plug-in for IMS, you can call the APIs from any REST client. 

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
<p><t><code>PUT {host}:{port}/ims/apis/v1/{imsplex}/region/stop?jobname={jobname}</code>

•	Start the IMS application region
<p><t><code>PUT {host}:{port}/ims/apis/v1/{imsplex}/region/start?jobname={jobname}</code>

•	Verify the application region status
<p><t><code>GET {host}:{port}/ims/apis/v1/{imsplex}/region</code>

If the transaction or program resource does not run properly, Susan might need to restart the resource. Susan might need to restart the resource. To check the status of the resources, she can invoke the following APIs:

<p><t><code>GET {host}:{port}/ims/apis/v1/{imsplex}/pgm?names={appname}&show=STATUS</code>

<p><t><code>GET {host}:{port}/ims/apis/v1/{imsplex}/tran?names={traname}&show=STATUS</code>
  
To start or stop the resources, Susan can invoke additional APIs outlined in the OpenAPI documentation. 

