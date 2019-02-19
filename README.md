# IMS Operations API

IMS Operations API allows users to use RESTFul APIs to submit IMS commands for managing IMS resources in support of application deployment and other IMS operations needs.

The initial offering would provide APIs in support of application deployment.  The Operations API provides the ability to manage IMS program resources for application programs, manage and query IMS application program resources that have been scheduled for execution, and control availability of IMS message and application processing regions.  

Specificially, the initial offering will have include ability to 
- Create and define IMS program resources for application programs
- Update, start or stop IMS program resources
- Query information about IMS program resources
- Delete IMS program resources
- Create an IMS transaction code that associates an application program resource to be scheduled for execution
- Update, start or stop IMS transaction resources
- Query information about IMS transactions across IMSplex
- Delete IMS transactions
- Start and Stop IMS message and application processing regions
- Display a list of active message and application processing regions

## Application Deployment Use cases
### Update an existing IMS application
Susan, the Application developer, makes changes to an existing IMS application and needs to refresh the application resources in IMS to pick up the new changes. 

With the IMS Operations API, Susan can update the IMS application resources using REST API instead of conventional methods such as ISPF, TSO, and SPOC.  The REST APIs can also be integrated into a DevOps pipeline for the change and code delivery. 

To refresh an existing IMS application resource with the change, Susan will issue the stop region API and followed by a start region API: 
- Stop the IMS application region
<p><t><code>host:port/ims/apis/v1/{plex}/region/stop?jobname=jobname</code>
  
- Start the IMS application region
<p><t><code>host:port/ims/apis/v1/{plex}/region/start?jobname=jobname</code> 

- Verify the application region status
<p><t><code>host:port/ims/apis/v1/{plex}/region/start?jobname=jobname</code>

Optionally, Susan may need to restart the transaction or program resource if it hasn't been running properly.  She may invoke the following APIs to start, stop or query the transaction or program resources:
<p><code>host:port/ims/apis/v1/{plex}/pgm?names=appname&show=STATUS</code>
<p><code>host:port/ims/apis/v1/{plex}/tran?names=traname&show=STATUS</code>

### Create a new IMS application
TBD

## Installing the IMS Operations API

## Configuring the IMS Operations API
