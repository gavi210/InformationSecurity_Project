# InformationSecurity_Project
This is the repository that contains the code for the Information Security Project. 

## Repo Organisation
The repository is organized into 3 main branches: 
- **unsafe_app**: it contains the code of the initial application version, where no security mechanisms have been implemented,
- **safe_app**: it contains the code for the secured application, where the identified security threats have been hopefully solved, (note that encryption and decryption
are not supported here)
- **encryption_feature**: it contains the code for the secured application and support for email encryption/ decryption and body signature. 

## Branch for penetration testing
The **safe_app** branch is the one supposed to be used for penetration testing. 

Do not use the **encryption_feature** branch, since for that application's version, data is exchanged with json and no HTML is generated during the client/server 
communication. Therefore, it would be difficult to show some attacks like XSS!

## Notes regarding the application security
To consider our application secure, we made some assumptions. In particular, we assumed some parts of the application to be secure by default. 
These parts are: 
- the database: we assumed that no attacker is able to read/write any information from/to the database. Additionally, we assumed that the only operations
performed on it are those implemented by the application. We made this assumption since it would have been too complex to manage this vulnerability as well
- the http protocol: we assumed that the http protocol is secure with respect to confidentiality and integrity. We know that http transmits data in plaintext
and everyone on the Network could read the packets being transmitted. To overcome the lack of confidentiality and integrity of http, https has been introduced. 
[Here](https://docs.oracle.com/middleware/11119/edq/secure/ssl_tomcat.htm) is a tutorial to set up TomCat accepting https communications. We decided not
to invest time on making TomCat accepting only https connections to ensure confidentiality and integrity, but rather to focus on implementing the other security mechanisms. 
Therefore, for those who will test the application, please do not use the http vulnerabilities on your advantage.
For example, do not show that the user passwords are readable in the transmitted packets and do not steal the user session ids to pretend to be another user. With https 
you would not be able anymore to sniff the packets and inspect their content. Therefore, search for other issues ;) 

## How to install the application
In each branch you will find the respective README.md file explaining how to install, set-up and use the application.

