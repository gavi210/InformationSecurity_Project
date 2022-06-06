# InformationSecurity_Project

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

### Install the client
To install the java client perform the following actions:
1) Import the [MailClient](MailClient) folder in another Eclipse window. MailClient should be the root folder.

### Set-up Sql Server
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

### Connect the application with the database
1) Make a copy of the file [dbConfigExample.properties](ExamProject/src/main/java/it/unibz/examproject/config/dbConfigExample.properties)
2) rename the copy to **dbConfig.properties**
3) edit the **dbConfig.properties** file and replace the **<someRequiredField>** to fit your environment
4) note that even the **db.url** has required fields to be inserted
5) place the **dbConfig.properties** file under [webapp](ExamProject/src/main/webapp)


### Use the application
To use the application: 
1) Run the web server with: right click on project -> Run As -> Run on Server -> Select the Apache Tomcat server -> Run -> Go to your browser on URL "http://localhost:8080/ExamProject/"
2) Create under [test](MailClient/src/test/java) your tests or use the already defined ones and run the tests to interact with the Web Server from the client

