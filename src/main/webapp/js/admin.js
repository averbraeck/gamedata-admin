/* load the page with the right information (all passed in the request) */
function initPage() {

  /* logged in? */
  var rn = String("${adminData}");
  if (rn.length == 0 || rn == "null") {
    window.location = "/housinggame-admin/login";
  }
}

/* handle click on button */
function clickMenu(button) {
  document.getElementById("click").setAttribute("value", button);
  document.getElementById("clickForm").submit();
}

/* handle click on button with record number */
function clickRecordId(button, recordId) {
  document.getElementById("click").setAttribute("value", button);
  document.getElementById("recordNr").setAttribute("value", recordId);
  document.getElementById("clickForm").submit();
}

/* submit edit form */
function submitEditForm(click, recordNr) {
  document.getElementById("editClick").setAttribute("value", click);
  document.getElementById("editRecordNr").setAttribute("value", recordNr);
  document.getElementById("editForm").submit();
}


/* create the preview for the image when a file is selected */
function previewImage(event, imageId) 
{
  var reader = new FileReader();
  reader.onload = function()
  {
    var output = document.getElementById(imageId);
    output.src = reader.result;
  }
  reader.readAsDataURL(event.target.files[0]);
  var image_reset = document.getElementById(imageId + "_reset");
  image_reset.value = 'normal';
}

function resetImage(imageId) 
{
  var image = document.getElementById(imageId);
  image.src = '';
  image.value = '';
  var image_reset = document.getElementById(imageId + "_reset");
  image_reset.value = 'delete';
}


/* Make the modal window div element draggable: */
function dragElement(elmnt) {
  var pos1 = 0, pos2 = 0, pos3 = 0, pos4 = 0;
  if (document.getElementById(elmnt.id + "header")) {
    // if present, the header is where you move the DIV from:
    document.getElementById(elmnt.id + "header").onmousedown = dragMouseDown;
  } else {
    // otherwise, move the DIV from anywhere inside the DIV:
    elmnt.onmousedown = dragMouseDown;
  }

  function dragMouseDown(e) {
    e = e || window.event;
    e.preventDefault();
    // get the mouse cursor position at startup:
    pos3 = e.clientX;
    pos4 = e.clientY;
    document.onmouseup = closeDragElement;
    // call a function whenever the cursor moves:
    document.onmousemove = elementDrag;
  }

  function elementDrag(e) {
    e = e || window.event;
    e.preventDefault();
    // calculate the new cursor position:
    pos1 = pos3 - e.clientX;
    pos2 = pos4 - e.clientY;
    pos3 = e.clientX;
    pos4 = e.clientY;
    // set the element's new position:
    elmnt.style.top = (elmnt.offsetTop - pos2) + "px";
    elmnt.style.left = (elmnt.offsetLeft - pos1) + "px";
  }

  function closeDragElement() {
    // stop moving when mouse button is released:
    document.onmouseup = null;
    document.onmousemove = null;
  }
}
