# IMS Operations API

- [Downloading IMS Operations API](#downloading-ims-operations-api)
- [Configuring the IMS Operations API as a Zowe component](#configuring-the-ims-operations-api-as-a-zowe-component)
- [Using the IMS Operations API](#using-the-ims-operations-api)

## Downloading IMS Operations API

On USS create a `/ims` directory that you will put one .jar file into, and a subdirectory `ims/bin` where you will put one script into.  

Download the jar file `IMS-1.0.1-BYPASS.remame_to_jar`, which will be in [this github folder](https://github.com/zowe/ims-operations-api/tree/master/ims/jar).   Rename it to `IMS-1.0.1-BYPASS.jar`.  Transfer this in binary mode to the USS `ims` USS directory.

Copy the file `start.sh` from [github](https://github.com/zowe/ims-operations-api/tree/master/ims/src/main/resources/zowe-scripts#:~:text=2%20years%20ago-,start.sh,-Update%20to%20use) into the `ims/bin` directory.  You can do this using ftp in txt mode, or just open a USS file editor and copy and paste the contents as the file isn't too long.  

On USS you should see a structure like:

```
../ims
  ims-1.0.1-BYPASS.jar
  bin/
    start.sh
```

The `.jar` file contains the Java code for the Spring Boot server that uses the API Mediation Layer static registration pattern to add the IMS Operations API.  

## File Permissions

The Zowe started task `ZWESVSTC` runs under the user ID `ZWESVUSR` and needs to be able to access these files. To see the permissions use the unix command `ls -alT`.  **Note** The dates and sizes of the actual files may be different, and the USERID will be the userID that was used to do the transfer, and the group TSOUSER may be different.  

```
>ls -alT
- untagged  T=off -rw-------    1 USERID     TSOUSER     262144    Jul 10 12:09 ims-BYPASS-1.0.1.jar
- untagged  T=off drw-------    1 USERID     TSOUSER       8192    Jul 10 12:09 bin
```

The file permissions of `rw-------` only give the owner read (and write) access, so the user ID `ZWESVUSR` will be unable to open the files.  To grant `ZWESVUSR` permission allow all users the ability to read the .jar file, and all users the ability to read and execute the directory.  (Execute permission is required for the directory as this is needed to expand directory contents).

```
>chmod a+r ims-ops-api-1.0.0.jar
>chmod a+rw zowe-scripts
```

The permissions should now have been set correctly, and you can check this using `ls -alT`.

```
>ls -alT
- untagged  T=off -rw-r--r--    1 USERID     TSOUSER     262144    Jul 10 12:09 ims-ops-api-1.0.0.jar
- untagged  T=off drw-r-xr-x    1 USERID     TSOUSER       8192    Jul 10 12:09 zowe-scripts
```

The next step is to configure a Zowe instance to point to the IMS Operations API directory so that is recognized as an external component.

## Configuring the IMS Operations API as a Zowe component

A Zowe started task `ZWESVSTC` is launched from an instance directory that contains configuration information such as which Zowe runtime to use, as well as any Zowe extensions to include.  

### Update the Zowe instance

To configure Zowe to use the IMS Operations API the instance directory file `instance.env` in the instance directory used to launch Zowe needs to be updated so  the `EXTERNAL_COMPONENTS` value points to the `zowe-scripts` directory created earlier.

For example, if you expanded into `/usr/lpp/zowe-extensions/ims` then the `EXTERNAL_COMPONENTS` would point to `usr/lpp/zowe-extensions/ims/bin`.

```sh
EXTERNAL_COMPONENTS=/usr/lpp/zowe-extensions/ims/bin # For third-party extender to add the full path to the directory containing their component lifecycle scripts
```

If the `instance.env` file has more than one `EXTERNAL_COMPONENTS` path because you have more than one extension component then semi colons `;` should be used to separate the paths. 

### Set the port

The IMS Operations API is a Java Spring Boot server that will be started by the Zowe started task `ZWESVSTC`.  The server needs to use a port that it uses to receive REST API requests from the API gateway.  The value of this port is specified in the `/bin/start.sh` in the variable `ZWE_IMS_OPS_API_PORT`.

```sh
export ZWE_IMS_OPS_API_PORT=8888
```
The default value of the port is 8888.  You should update this port if required to use a different value depending on your z/OS environment.  

## Using the IMS Operations API

When you launch Zowe the started task `ZWESVSTC` will open a number of address spaces.  These are named to allow you to distinguish them in RMF records or SDSF views.  For more information see [Address space names](https://docs.zowe.org/stable/user-guide/configure-instance-directory.html#address-space-names).

The IMS Operations API address space will be given a subcomponent value of `IM`, so its name will be `ZWE1IM`.  The address space will be started and stopped with Zowe.

### API Catalog

When you log onto the API Catalog the IMS Operations API will appear as a tile.  If it is not there check the `STDERR` file for the `ZWESVSTC` started task for any messages.  

<img src="./images/api_catalog.png" alt="Zowe API Catalog Diagram with IMS" width="700px"/> 

Selecting the IMS Operations API tile will allow you to browser the available APIs as well as test them.  For more information see [Using the IMS Operations API](./using-ims-operations-api.md)

<img src="./images/api_region.gif" alt="Zowe API Catalog Diagram with IMS" width="700px"/> 



<!--
pax -wvf ims-ops-api-1.0.0.pax .
pax -ppx -rf ims-ops-api-1.0.0.pax
-->