var infotxt = document.getElementById("infoText");
var usertxt = document.getElementById("yourName");
var emailtxt = document.getElementById("yourEmail");
var pwtxt = document.getElementById("changepwinfotxt");
var mazetxt = document.getElementById("mazelist");

var mazebutton = document.getElementById("getmazebutton");
mazebutton.addEventListener ("click", function() {

	const URL='http://127.0.0.1:43594/usermazes'

	const dat={
		sendstuff:1
	}

	$.post(URL,dat,function(reply,status) {
		if (reply.startsWith('mazes=') ){
		    var mzs = reply.split("=")[1];
		    var mm = mzs.split(";");
		    var build = "";
		    for (var i=0; i<mm.length; i++) {
		        var mazename = mm[i].split(",")[0];
		        var mazeid = mm[i].split(",")[1];
		        build = build+"<p><a href='mazeInteraction/"+mazeid+"'>"+mazename+"</a></p>";
		    }
            mazetxt.innerHTML = build;
        }
	});
});

var logoutButton = document.getElementById("logoutbutton");
logoutButton.addEventListener ("click", function() {

	const URL='http://127.0.0.1:43594/userlogout'

	const dat={
		saybye:1
	}

	$.post(URL,dat,function(reply,status) {
		if (reply.startsWith('gtfo') ){
            infotxt.innerHTML = "Logged out. <a href='login.html'>Click here</a> to log in again.";
            var p1 = document.getElementById("passwordcur");
                    var p2 = document.getElementById("passwordnew");
                    var p3 = document.getElementById("submitnewpw");
                    var p4 = document.getElementById("sendmazebutton");
                    var p5 = document.getElementById("changepwtxt");
                    var p6 = document.getElementById("passwordnewconf");
                    p1.setAttribute("hidden", true);
                    p2.setAttribute("hidden", true);
                    p3.setAttribute("hidden", true);
                    p4.setAttribute("hidden",true);
                    p5.setAttribute("hidden",true);
                    p6.setAttribute("hidden",true);
                    logoutButton.setAttribute("hidden",true);
                    usertxt.innerHTML = "";
                    emailtxt.innerHTML = "";
                    pwtxt.innerHTML = "";
        }
	});
});

var buttonPW = document.getElementById("submitnewpw");

buttonPW.addEventListener ("click", function() {
    pwtxt.innerHTML = "Waiting for reply...";
	const URL='http://127.0.0.1:43594/userchangepw'
	var p1 = document.getElementById("passwordcur");
    var p2 = document.getElementById("passwordnew");
    var p3 = document.getElementById("passwordnewconf");

	const dat={
		curpw: p1.value,
		newpw: p2.value,
		newpwconf: p3.value
	}

	$.post(URL,dat,function(reply,status) {
		if (reply.startsWith('pw-changed') ){
		    pwtxt.innerHTML = "Password changed successfully.";
        }
        else if (reply.startsWith('pw-nomatch')) {
            pwtxt.innerHTML = "New passwords do not match.";
        }
        else if (reply.startsWith('pw-invalid')) {
            pwtxt.innerHTML = "Current password invalid.";
        }
	});
});

//https://stackoverflow.com/questions/5639346/what-is-the-shortest-function-for-reading-a-cookie-by-name-in-javascript
function readCookie(name) {
  var nameEQ = name + "=";
  var ca = document.cookie.split(';');
  for(var i=0;i < ca.length;i++) {
    var c = ca[i];
    while (c.charAt(0)==' ') c = c.substring(1,c.length);
    if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
  }
  return null;
}

function render() {
    var cookb = readCookie("userSession");
    //var cooks = document.cookie;
    if (cookb!=null) {
    infotxt.innerHTML = ""+cookb;
    //console.log("read cookies: "+cooks);
    } else {
        infotxt.innerHTML = "Not logged in. Log in <a href='login.html'>here.</a>";
        var p1 = document.getElementById("passwordcur");
        var p2 = document.getElementById("passwordnew");
        var p3 = document.getElementById("submitnewpw");
        var p4 = document.getElementById("sendmazebutton");
        var p5 = document.getElementById("changepwtxt");
        var p6 = document.getElementById("passwordnewconf");
        p1.setAttribute("hidden", true);
        p2.setAttribute("hidden", true);
        p3.setAttribute("hidden", true);
        p4.setAttribute("hidden",true);
        p5.setAttribute("hidden",true);
        p6.setAttribute("hidden",true);
    }
}

function requestDetails() {
const URL='http://127.0.0.1:43594/user.html'

	const dat={
		sendstuff:1
	}

	$.post(URL,dat,function(reply,status) {
		console.log('sent POST and got reply=<'+reply+'>')

		if (reply.startsWith('username') ){
		    var namefield = reply.split(';')[0];
		    var name = namefield.split('=')[1];
		    var emailfield = reply.split(';')[1];
            var email = emailfield.split('=')[1];
            infotxt.innerHTML = "";
            usertxt.innerHTML = "Gamer tag: "+name;
            emailtxt.innerHTML = "Email: "+email;
        }
	});
}

requestDetails();
render();