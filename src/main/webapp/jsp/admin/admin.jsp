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
    <div class="gd-page">
      <div class="gd-header">
        <span class="gd-game-heading">GameData</span>
        <span class="gd-slogan">Administration</span>
      </div>
      <div class="gd-header-right">
        <img src="images/tudelft.png" />
        <p><a href="/gamedata-admin">LOGOUT</a></p>
        <span style="font-size: 12px; padding-left: 20px; position:relative; top:-4px; color:black;">v0.1</span>
      </div>
      <div class="gd-header-game-user">
        <p>&nbsp;</p>
        <p>User:&nbsp;&nbsp;&nbsp; ${adminData.getUser().getName()}</p>
      </div>

      <div class="gd-body">
      
        <div class="gd-admin-menu">
          ${adminData.getTopMenu()}
        </div>
        <div class="gd-admin" id="gd-admin">
          ${adminData.getContentHtml()}
        </div>
        
      </div> <!-- gd-body -->
      
    </div> <!-- gd-page -->
    
    <!-- modal window with potential error message or extra confirmation (e.g., delete) -->
    
    ${adminData.getModalWindowHtml()}

    <form id="clickForm" action="/gamedata-admin/admin" method="POST" style="display:none;">
      <input id="click" type="hidden" name="click" value="tobefilled" />
      <input id="recordNr" type="hidden" name="recordNr" value="0" />
    </form>

  </body>

</html>