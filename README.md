# IMS Operations API

## Overview

The IMS Operations API is a service that provides REST APIs to manage the IMS resources required for IMS application deployment. The REST APIs invoke the IMS commands that are required to manage and query the IMS application resources.

You can use the IMS Operations API with the [Zowe CLI Plug-in for IBM IMS](https://github.com/zowe/zowe-cli-ims-plugin). Used together, the REST APIs that are delivered with the IMS Operation API enable communication between the Zowe CLI and an IMS system and, therefore, enable you to use the Zowe CLI commands to interact with IMS resources. Alternatively, instead of using the Zowe CLI Plug-in for IMS, you can call the APIs from any REST client.

Because the REST APIs can be called from a familiar environment, the learning curve and time that’s needed for you to develop and deliver business critical applications is significantly reduced. With the IMS Operations API, an application developer no longer needs to learn how to use ISPF editors, TSO, and SPOC for deploying and provisioning IMS application resources.

Also, because you can write scripts that use the Zowe CLI commands, which, in turn, call the IMS Operations API, you can use the Zowe CLI commands and REST APIs to implement an automated CI/CD pipeline for building, delivering, and testing your IMS applications.

## Use cases and scenario

For more information on using the IMS Operations API see [Using the IMS Operations API](./ims/docs/using-ims-operations-api.md)

## Install and test IMS Operations API

To obtain, install, and launch the IMS Operations API see [Installing IMS Operations API](./ims/docs/installing-ims-operations-api.md)

## Build and test IMS Operations API

The IMS Operations API have to be built locally using maven and the .jar file transferred to z/OS and paxed up.
This will be moved to the pipeline to get into artifactory but that work isn't done yet.

To build the IMS Operations API, see [Building IMS Operations API](./ims/docs/building-ims-operations-api.md)

( To build and test IMS Operations API, refer to the instructions in the wiki page ).
