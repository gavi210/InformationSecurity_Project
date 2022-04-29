<%@ page import="jakarta.servlet.http.HttpSession" %>
<%@ page import="java.util.Map" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="ISO-8859-1">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<link rel="stylesheet" href="style.css" type="text/css" />
	
	<title>Home page</title>
</head>
<body>
	<nav class="navbar">
	  <div class="box">
	  	<div class="box">
			<img src="images/email_icon.jpg" align="left" />
			<p>Welcome: <%
				Map<String, String> userInfo = (Map<String, String>) session.getAttribute("user");
				String userEmail = null;
				if (userInfo != null)
					userEmail = userInfo.get("email");
				out.println(userEmail);
			%></p>
	  	</div>
	  	<form class="btn-group" action="LogoutServlet" method="post">
			<input type="submit" name="logout" value="Logout">
		</form>
	  </div>
	</nav>
	
	<div class="grid-container">
		<form class="btn-group" action="NavigationServlet" method="post">
			<input type="hidden" name="email" value="<%= request.getAttribute("email") %>">
			<input type="hidden" name="password" value="<%= request.getAttribute("password") %>">
			<input type="submit" name="newMail" value="New Mail">
			<input type="submit" name="inbox" value="Inbox">
			<input type="submit" name="sent" value="Sent">
		</form>

		<!-- content shown retrieved form the servlet. the servlet filters and encapsulates the data to be shown -->
		<%= request.getAttribute("content") !=null ? request.getAttribute("content") : "" %>
	</div>
</body>
</html>