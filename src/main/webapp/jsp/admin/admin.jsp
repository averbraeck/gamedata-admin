<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
  pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>

<html>
  <head>
    <meta charset="ISO-8859-1">
    <title>GameData Administration</title>

    <!--  favicon -->
    <link rel="shortcut icon" href="/gamedata-admin/favicon.ico" type="image/x-icon">

    <link rel="stylesheet" type="text/css" href="/gamedata-admin/css/admin.css" />
    <script src="/gamedata-admin/js/admin.js"></script>

    <style>
    table, th, td {
      border: 1px solid gray;
      border-spacing: 0px;
      border-collapse: collapse;
      padding: 5px;
      vertical-align: top;
    }
    
    body {
      line-height: 1.2;
    }
    </style>

  </head>

  <body onload="initPage()">
    <div class="hg-page">
      <div class="hg-header">
        <span class="hg-game-heading">GameData</span>
        <span class="hg-slogan">Game Administration</span>
      </div>
      <div class="hg-header-right">
        <img src="images/tudelft.png" />
        <p><a href="/gamedata-admin">LOGOUT</a></p>
        <span style="font-size: 12px; padding-left: 20px; position:relative; top:-4px; color:black;">v1.7.0</span>
      </div>
      <div class="hg-header-game-user">
        <p>&nbsp;</p>
        <p>User:&nbsp;&nbsp;&nbsp; ${adminData.getUser().getUsername()}</p>
      </div>

      <div class="hg-body">
      
        <div class="hg-admin-menu">
          ${adminData.getTopMenu()}
        </div>
        <div class="hg-admin" id="hg-admin">
          ${adminData.getContentHtml()}
        </div>
        
      </div> <!-- hg-body -->
      
    </div> <!-- hg-page -->
    
    <!-- modal window for the client information within an order -->
    
    ${adminData.getModalWindowHtml()}

    <form id="clickForm" action="/gamedata-admin/admin" method="POST" style="display:none;">
      <input id="click" type="hidden" name="click" value="tobefilled" />
      <input id="recordNr" type="hidden" name="recordNr" value="0" />
    </form>

  </body>

</html>