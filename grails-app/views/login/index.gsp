<%--
  Created by IntelliJ IDEA.
  User: zhengli
  Date: 2018-01-16
  Time: 8:44 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>POP - Homepage</title>
    <meta name="layout" content="main" />
</head>
<body>
<g:if test="${session?.user}">
</g:if>

<g:form action='login' controller='login' class="simpleform" style="width:50%;" >
    <fieldset>
        <legend>Login</legend>
        <p class="info">
            Please login with your username and password. <br />
            Don't have an account?
            <g:link controller="user" action="register">Sign up now!</g:link>
        </p>
        <p>
            <label for="username">Username</label>
            <g:textField name="username" />
        </p>
        <p>
            <label for="password">Password</label>
            <g:passwordField name="password" />
        </p>
        <p class="button">
            <label>&nbsp;</label>
            <g:submitButton class="button" name="submitButton" value="Login" />
        </p>
    </fieldset>
</g:form>

</body>
</html>