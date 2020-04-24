var em = document.getElementById("email");
var pw = document.getElementById("password");
var txt = document.getElementById("infoText");

var button = document.createElement("button");
button.innerHTML = "Log In";
var body = document.getElementsByTagName("body")[0];
body.appendChild(button);
button.addEventListener ("click", function() {

	const URL='http://127.0.0.1:43594/login.html'

	const dat={
		email:em.value,
		password:pw.value
	}

	$.post(URL,dat,function(reply,status) {
		console.log('sent POST and got reply=<'+reply+'>')
		txt.innerHTML = '';
		if (reply == 'nouser') {
            txt.innerHTML = "No user is registered with this email address. Please <a href='register.html'>register.</a>";
        }
        else if (reply == 'invalid') {
            txt.innerHTML = "Invalid password. Please enter the correct password.";
        }
        else if (reply == 'loginok') {
            txt.innerHTML = "Login successful! Check out your <a href='user.html'>user page.</a>.";
        }
        else {
            txt.innerHTML = "invalid server response";
        }
	});
});