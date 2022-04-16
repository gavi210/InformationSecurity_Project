# Vulnerabilities in the System

- LoginServlet: sends back in the response the password for the logged user: but it shouldn't be there. 
Or at least, why should it be there? is it used in some way?
In the fileInspector, there are the hidden inputs visible
- Traffic between Application and Client is not encrypted, it should be made encrypted. 
Leverage the capabilities for TomCat and the servlets to encrypt communication.
link solution: https://stackoverflow.com/questions/53922416/how-to-set-https-for-java-servlet-application
 ... the main idea is to make tomcat accepting only https requests rather than http.
Look at Wireshark screen to notice that the communication was not encrypted and in plain text.
- Authorization and Authentication for the users submitting the requests. 

| Name                             | Description                                                                                                         | Solution                                                                                                                                |
|----------------------------------|---------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------|
| Password sent around             | The password for a logged user is stored in the html page, it is hidden, but could be read from the page inspector. | Remove the password form the webpage, since it is useless                                                                               |
| HTTP                             | Since HTTP is used instead of HTTPS, the data being exchanged could be sniffed.                                     | Adopt HTTPS communication to encrypt and hide the exchange of information                                                               |
| Authorization and Authentication | There is no mean to check whether the requests come from an authorized and trusted user.                            | Add an authorization and authentication mechanisms. https://www.mulesoft.com/tcat/tomcat-ssl maybe useful to set up the HTTPs mechanism |

