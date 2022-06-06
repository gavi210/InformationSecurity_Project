# InformationSecurity_Project

## Install and Run the application 
This version of the application is composed of two projects, namely the client and the server. 
It follows a description of how to set up and run both the client and the server. 
Additionally, it follows a brief demo on how the client should be used to interact with the application.

## Set-Up and Run the Web Server
### Setting up Eclipse Environment
1) Install Java JDK 11: it is the one used for the project
2) Install Eclipse latest version (currently 2021-12)
   3.1) Go to Help -> Install new software... -> Work with: "Latest Eclipse Simultaneous Release - https://download.eclipse.org/releases/latest"
   3.2) In the tab below, expand the "Web, XML, Java EE and OSGi Enterprise Development" checkbox
   3.3) Check the following elements:
    - Eclipse Java EE Developer Tools
    - Eclipse Java Web Developer Tools
    - Eclipse Java Web Developer Tools - JavaScript Support
    - Eclipse Web Developer Tools
    - Eclipse Web JavaScript Developer Tools
    - JST Server Adapters
    - JST Server Adapters Extension (Apache Tomcat)
      3.4) Click Next two times, then accept the licence and click Finish
4) Restart Eclipse
5) Go to Window -> Preferences -> Server -> Runtime Environments -> Add... -> Apache -> Apache Tomcat v10.0 -> Thick 'Create new a local server' -> Next
6) Click 'Download and install...', that should install the latest stable version (currently 10.0.13) -> Choose your favourite folder for Tomcat installation
7) Since now, you can see your installed web servers in the Eclipse 'Server' tab, if it is not displayed by default, you can enable it by going to Window -> Show view -> Server

### Import the Application From GitHub
1) Download the project from our repository... it should be public and accessible

### Install Web Server
To install the web server perform the following actions:
1) Import the [ExamProject](ExamProject) folder in Eclipse. ExamProject should be the root folder
2) Right-click on the project -> Build path -> Configure build path...
3) Go to Libraries -> Click on classpath -> Add External Jars... -> Add the file "servlet-api.jar" from lib directory inside the Apache Tomcat folder

### Set-Up Sql Server
Set up an instance of Sql Server with the hints [here](ExamProject/SQL%20Server%20setup.txt) and create a database with the SQL Script provided below:
```
create table [user]
(
    name     varchar(50)  not null,
    surname  varchar(50)  not null,
    email    varchar(320) not null
        constraint user_PK
            primary key,
    password varchar(65)  not null,
    val      int          not null,
    n        int          not null
);

create table mail
(
    sender    varchar(320) not null
        constraint sender_fk
            references [user]
            on update cascade on delete cascade,
    receiver  varchar(320) not null
        constraint receiver_fk
            references [user],
    subject   varchar(100),
    body      text         not null,
    time      datetime2(3) not null,
    signature text
);
```

### Connect the Web Server with the database
1) Make a copy of the file [dbConfigExample.properties](ExamProject/src/main/java/it/unibz/examproject/config/dbConfigExample.properties)
2) rename the copy to **dbConfig.properties**
3) edit the **dbConfig.properties** file and replace the **<someRequiredField>** to fit your environment
4) note that even the **db.url** has required fields to be inserted
5) place the **dbConfig.properties** file under [webapp](ExamProject/src/main/webapp)

## Install and Run the client
To install the java client perform the following actions:
- import the [MailClient](MailClient) folder in another Eclipse window. MailClient should be the root folder.

### Use the application
To use the application: 
1) Run the web server with: right click on project -> Run As -> Run on Server -> Select the Apache Tomcat server -> Run -> Go to your browser on URL "http://localhost:8080/ExamProject/"
2) Use the client to interact with the web server.

## Demo how to use the client
The client has one main entry point, the [Operations.java](MailClient/src/main/java/it/unibz/mailclient/Operations.java) class.
This class should be used to interact with the Web Server. 

### Operations supported
The class offers a set of basic operations that the user could perform: 
- register
- login
- logout
- reset the database
- send an email 
- read the received emails

### User the Operations.java class
Let's start by creating a new Operations instance. 
```
    String baseUrlWebServer = "<address_for_the_web_server>";
    Operations op = new Operarations(baseUrlWebServer);
```

The Operations instance receives as input the URL to reach the server instance.  
Once the new object is created, the user could perform any of the previous listed operations. 

For example, let's create a new user and send an email... 
```
    op.register("Name", "Surname", "mail", "password");
    op.sendEmail("Receiver", "mail Subject", "mail Body", false);
```

Note that the last parameter of ``sendEmail()`` determines whether to digitally sign or not the email's body.

### Private RSA Keys and Sessions
The ``Operations`` class stores locally the private RSA keys for the users that registered. Nevertheless, such storage
is not persistent. Therefore, the private RSA keys are lost once the ``Operations`` object is destroyed. 
The same holds for ``Session Cookies`` being transmitted during Client Server interaction. The object stores locally the cookies
and automatically adds them to the new requests. Once the object is destroyed, they are lost though.

### Examples of Operations class in use
We developed some automated unit tests for the Operations class. They are available under [MailClient/src/test/java/](MailClient/src/test/java).
They provide some working examples on how to use the client. 

### Reset the database
As you can see from the tests' source code, we always reset the database through ``Operations.resetDatabase()``. 
This feature has been introduced to support automated testing, so that previous execution does not affect subsequent tests. 
Please, for the tester... do not consider that as a vulnerability, since this method would not be part of the code deployed in real environments. 
The method is there even for you, so that you don't have to manually remove previously registered users and sent emails during your application 
analysis!. Thank you ;)

