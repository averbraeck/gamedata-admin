<%@ page language="java" contentType="text/html; charset=utf-8"
 pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>GameData Admin Login</title>

<!--  favicon -->
<link rel="shortcut icon" href="/gamedata-admin/favicon.ico" type="image/x-icon">

<style>
html, body {
  font-family: Arial, Helvetica, sans-serif;
}

.gd-login-page {
  display: flex;
  flex-direction: column;
  align-items: stretch;
  position: absolute;
  top: 20px;
  bottom: 20px;
  left: 20px;
  right: 20px;
  min-height: 0px;
}

.gd-header, .gd-body, .gd-footer {
  flex-shrink: 0;
}

.gd-login-header {
  background: navy;
  padding: 5px;
  height: 50px;
  text-align: left;
  line-height: 50px;
  color: white;
  font-weight: bold;
  font-size: 2em;
  border-radius: 10px;
  margin-bottom: 20px;
}

.gd-login-header-right {
  position: absolute;
  background: white;
  margin: 2px;
  height: 46px;
  width: 300px;
  right: 8px;
  border-radius: 5px;
  flex-direction: row;
}

.gd-login-header-right > img {
  margin-left: 20px;
  margin-top: 3px;
  height: 40px;
  width: auto;
}

.gd-login-body {
  display: flex;
  flex-direction: column;
  align-items: stretch;
  min-height: 0;
  height: calc(100vh - 200px);
  width: 600px;
  margin-left: calc(50vw - 300px);
  padding-right: 20px;
  overflow-y: auto;
  overflow-x: hidden;
}

.gd-login-footer {
  background: navy;
  padding: 5px;
  height: 50px;
  line-height: 50px;
  color: white;
  font-weight: bold;
  font-size: 1em;
  text-align: right;
  border-radius: 10px;
  margin-top: 20px;
}

.gd-login-top-message {
  font-style: normal;
  font-size: 1em;
  margin-bottom: 20px;
}

.gd-login-top-message > h1 {
  font-style: normal;
  font-weight: bold;
  font-size: 1.8em;
  text-align: left;
  color: orange;
}

.gd-login-top-message > p {
  text-align: justify;
  line-height: 1.2;
}

.gd-login-bottom-message {
  font-style: normal;
  font-size: 0.8em;
  margin-top: 20px;
}

.gd-login-bottom-message > p {
  text-align: justify;
  line-height: 1.2;
}

.gd-login {
  width: 595px;
  border: 3px solid orange;
  border-radius: 10px;
  padding-top: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.gd-login-button {
  display: block;
  width: 595px;
  border: none;
  background-color: orange;
  padding: 14px 28px;
  cursor: pointer;
  text-align: center;
  font-size: 1.3em;
  font-weight: bold;
}

</style>
</head>

<body>
  <div class="gd-login-page">
    <div class="gd-login-header">
      <div class="gd-login-header-right">
        <img src="images/tudelft.png" />
        <span style="font-size: 12px; padding-left: 20px; position:relative; top:-4px; color:black;">GameData v0.1</span>
      </div>
    </div>
  
    <div class="gd-login-body">
    
      <div class="gd-login-top-message">
        <h1>GameData Administration</h1> 
        <p>The following functions are available:</p>
        <ul>
          <li>Creating and maintaining organizations</li>
          <li>Creating and maintaining users</li>
          <li>Allocating roles to users</li>
          <li>Creating access tokens</li>
          <li>Maintaining games and game versions</li>
        </ul>
      </div>

      <div class="gd-login">
        <form action="/gamedata-admin/login" method="post">
         <table>
           <tr>
             <td width="60px">&nbsp;</td>
             <td>UserName &nbsp; </td>
             <td><input type="text" name="username" /></td>
           </tr>
           <tr>
             <td width="150px">&nbsp;</td>
             <td>Password &nbsp; </td>
             <td><input type="password" name="password" /></td>
           </tr>
         </table>
         <br/>
         <span>
           <input type="submit" value="ADMINISTRATOR LOGIN" class="gd-login-button" />
         </span>
        </form>
      </div>
  
      <div class="gd-login-bottom-message">
        <p> 
          Contact: Alexander Verbraeck at TU Delft (<a href="mailto:a.verbraeck@tudelft.nl">a.verbraeck@tudelft.nl</a>).
        </p>
      </div>
  
    </div>
    
    <div class="gd-login-footer">
      <!-- logo's at top right -->
    </div>
    
  </div>      
</body>
</html>