<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>

<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    
    <title>GameData Administration</title>

    <!--  favicon -->
    <link rel="shortcut icon" href="/gamedata-admin/favicon.ico" type="image/x-icon">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet" />
    <link href="https://fonts.googleapis.com/css?family=Roboto:300,400,500,700&display=swap" rel="stylesheet" />

    <link href="https://cdnjs.cloudflare.com/ajax/libs/mdb-ui-kit/8.1.0/mdb.min.css" rel="stylesheet"/>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/mdb-ui-kit/8.1.0/mdb.umd.min.js"></script>

    <script src="/gamedata-admin/js/admin.js"></script>
    <link href="/gamedata-admin/css/admin.css" rel="stylesheet"/>
  </head>

  <body onload="initPage()">
    <div class="container-fluid">
      <div class="row flex-nowrap">
        ${adminData.getHeader()}
        ${adminData.getSidebar()}
        <div class="col py-3">
          ${adminData.getContent()}
        </div>
      </div>
    </div>
    
    <form id="clickForm" action="/gamedata-admin/admin" method="POST" style="display:none;">
      <input id="click" type="hidden" name="click" value="tobefilled" />
      <input id="recordNr" type="hidden" name="recordNr" value="0" />
    </form>
         
  </body>
</html>
