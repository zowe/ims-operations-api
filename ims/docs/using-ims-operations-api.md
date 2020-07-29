# Using the IMS Operations APIs

You can use the REST APIs that are provided with IMS Operations API to perform the following tasks:

- Create, define, manage, query, and delete IMS program resources for application programs.
- Create, manage, query, and delete IMS transactions and transaction resources.
- Start, stop, and display information about IMS message and application processing regions.

## Updating an existing IMS application by using the IMS Operations API

In order to add a new feature to an existing IMS application APIs can be invoked to refresh application resources in the IMS system.

To refresh existing IMS application resources issues the stop region API followed by the start region API:

•	Stop the IMS application region
<p><t><code>PUT {host}:{port}/ims/apis/v1/{imsplex}/region/stop?jobname={jobname}</code>

•	Start the IMS application region
<p><t><code>PUT {host}:{port}/ims/apis/v1/{imsplex}/region/start?jobname={jobname}</code>

•	Verify the application region status
<p><t><code>GET {host}:{port}/ims/apis/v1/{imsplex}/region</code>

If the transaction or program resource does not run properly, Susan might need to restart the resource. Susan might need to restart the resource. To check the status of the resources, she can invoke the following APIs:

<p><t><code>GET {host}:{port}/ims/apis/v1/{imsplex}/pgm?names={appname}&show=STATUS</code>

<p><t><code>GET {host}:{port}/ims/apis/v1/{imsplex}/tran?names={traname}&show=STATUS</code>

Additional APIs are outlined in the OpenAPI documentation.

## IMS Commands

To learn more about the list of available IMS commands see the IBM Knowledge Center documentation for each command

- [Query Program](https://www.ibm.com/support/knowledgecenter/en/SSEPH2_15.1.0/com.ibm.ims15.doc.cr/imscmds/ims_querypgm.htm) 
- [Update Program](https://www.ibm.com/support/knowledgecenter/en/SSEPH2_15.1.0/com.ibm.ims15.doc.cr/imscmds/ims_updatepgm.htm) 
- [Create Program](https://www.ibm.com/support/knowledgecenter/en/SSEPH2_15.1.0/com.ibm.ims15.doc.cr/imscmds/ims_createpgm.htm) 
- [Delete Program](https://www.ibm.com/support/knowledgecenter/en/SSEPH2_15.1.0/com.ibm.ims15.doc.cr/imscmds/ims_deletepgm.htm) 
- [Query Transaction](https://www.ibm.com/support/knowledgecenter/en/SSEPH2_15.1.0/com.ibm.ims15.doc.cr/imscmds/ims_querytran.htm) 
- [Update Transaction](https://www.ibm.com/support/knowledgecenter/en/SSEPH2_15.1.0/com.ibm.ims15.doc.cr/imscmds/ims_updatetran.htm) 
- [Create Transaction](https://www.ibm.com/support/knowledgecenter/en/SSEPH2_15.1.0/com.ibm.ims15.doc.cr/imscmds/ims_createtran.htm) 
- [Delete Transaction](https://www.ibm.com/support/knowledgecenter/en/SSEPH2_15.1.0/com.ibm.ims15.doc.cr/imscmds/ims_deletetran.htm) 
- [Start Region](https://www.ibm.com/support/knowledgecenter/en/SSEPH2_15.1.0/com.ibm.ims15.doc.cr/imscmds/ims_startregion.htm)
- [Stop Region](https://www.ibm.com/support/knowledgecenter/en/SSEPH2_15.1.0/com.ibm.ims15.doc.cr/imscmds/ims_stopregion.htm)
- [Display ACT Region](https://www.ibm.com/support/knowledgecenter/en/SSEPH2_15.1.0/com.ibm.ims15.doc.cr/imscmds/ims_displayact.htm)