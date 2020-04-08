var gt = document.getElementById("gamerTag");
var em = document.getElementById("email");
var pw = document.getElementById("password");
var pw2 = document.getElementById("confirmPassword");
var txt = document.getElementById("errorText");

var button = document.createElement("button");
button.innerHTML = "Register";
var body = document.getElementsByTagName("body")[0];
body.appendChild(button);
button.addEventListener ("click", function() {

    console.log("clicked register button")

	const URL='http://127.0.0.1:43594/register/'
	var sendmaze = '';

	const dat={
		gamertag:gt.value,
		email:em.value,
		password:pw.value,
		confPassword:pw2.value
	}
	$.post(URL,dat,function(reply,status) {
		console.log('sent POST and got reply=<'+reply+'>')
		txt.innerHTML = '';
		if (reply == 'nomatch') {
            txt.innerHTML = "Passwords do not match.";
        }
        else if (reply == 'alreadyexists') {
            txt.innerHTML = "A user with this gamertag already exists. Please choose a different name or <a href='login.html'>log in.</a>";
        }
        else if (reply == 'emailalreadyexists') {
            txt.innerHTML = "This email address already has a user associated with it. Please use a different email or <a href='login.html'>log in.</a>";
        }
        else if (reply == 'registerok') {
            txt.innerHTML = "Account created successfully! Please <a href='login.html'>log in.</a>";
        }
        else if (reply == 'small') {
            txt.innerHTML = "Password must be at least 8 characters long.";
        }
        else {
            txt.innerHTML = "invalid server response";
        }
	});
});