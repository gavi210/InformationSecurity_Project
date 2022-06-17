# InformationSecurity_Project

## ExamProject
[ExamProject](ExamProject) folder contains the code for the Web Application running the safe version.
To execute the application, follow the steps suggested at the beginning of the project.
They are listed below...

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
2) Import the [ExamProject](ExamProject) folder in Eclipse. ExamProject should be the root folder
4) Right-click on the project -> Build path -> Configure build path...
5) Go to Libraries -> Click on classpath -> Add External Jars... -> Add the file "servlet-api.jar" from lib directory inside the Apache Tomcat folder

### Set-up Sql Server
Set up an instance of Sql Server with the hints [here](ExamProject/SQL%20Server%20setup.txt) and create a database with the SQL Script provided below:
```
CREATE TABLE [user] (
	name varchar(50)  NOT NULL,
	surname varchar(50) NOT NULL,
	email varchar(320) NOT NULL,
	password varchar(65) NOT NULL,
	CONSTRAINT user_PK PRIMARY KEY (email)
);

CREATE TABLE mail (
	sender varchar(320) NOT NULL,
	receiver varchar(320) NOT NULL,
	subject varchar(100) NULL,
	body text NOT NULL,
	[time] datetime2(3) NOT NULL,
	CONSTRAINT mail_FK FOREIGN KEY (sender) REFERENCES [user](email),
	CONSTRAINT mail_FK_1 FOREIGN KEY (receiver) REFERENCES [user](email)
);
```

**Please note that the Database schema slightly changed form the initial unsafe version**. 
Please change your database schema accordingly. 

### Connect the application with the database
1) Make a copy of the file [dbConfigExample.properties](ExamProject/src/main/java/it/unibz/examproject/config/dbConfigExample.properties)
2) rename the copy to **dbConfig.properties**
3) edit the **dbConfig.properties** file and replace the **<someRequiredField>** to fit your environment
4) note that even the **db.url** has required fields to be inserted
5) place the **dbConfig.properties** file under [webapp](ExamProject/src/main/webapp)

### Run the application
1) Run the application with: right click on project -> Run As -> Run on Server -> Select the Apache Tomcat server -> Run -> Go to your browser on URL "http://localhost:8080/ExamProject/"
2) Use the application from your favourite browser
	
### Vulnerability Assessment and Penetration Testing
As instructed from the lecturer, a detailed explaination of the vulnerability assessment of the unsafe app and how these vulnerabilities have been patched is available [here on YouTube](https://youtu.be/VlPkjYCt8Dg).
