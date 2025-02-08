<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>

<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    
    <title>GameData Administration</title>

    <!--  favicon -->
    <link rel="shortcut icon" href="/gamedata-admin/favicon.ico" type="image/x-icon">

    <link href="/gamedata-admin/css/fa.all.min.css" rel="stylesheet"/>
    <script src="/gamedata-admin/js/admin.js"></script>
    <link href="/gamedata-admin/css/admin.css" rel="stylesheet"/>
  </head>

  <body onload="initPage()">
    <div class="gd-body">
       ${adminData.getTopbar()}
       <div class="gd-main">
	       ${adminData.getSidebar()}
	       ${adminData.getSubMenubar()}
	       <div class="gd-content">
  	       ${adminData.getContent()}
  	     </div>
	     </div>
    </div>
    
    <!-- modal window for the client information within an order -->
    ${adminData.getModalWindowHtml()}

    <form id="clickForm" action="/gamedata-admin/admin" method="POST" style="display:none;">
      <input id="click" type="hidden" name="click" value="tobefilled" />
      <input id="recordId" type="hidden" name="recordId" value="0" />
    </form>
         
  </body>
</html>
