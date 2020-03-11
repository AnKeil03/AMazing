import React, {Component} from 'react'
import ReactDOM from 'react-dom'
//const React = require('react');
//const ReactDOM = require('react-dom');
import '../webpack/css/main.css'
//const client = require('./client');
import axios from 'axios';

class Main extends Component {
    constructor(props) {
        super(props);
        this.loginSuccessful = false;
        this.username = "";
        this.email = "";
        this.password = "";
        this.registerUser = this.registerUser.bind(this);
        this.handlePassword = this.handlePassword.bind(this);
        this.handleLogin = this.handleLogin.bind(this);
    }
    registerUser() {
            this.username = document.getElementById("enterUsername").value;
            this.email = document.getElementById("enterEmail").value;
            if (this.username == "" ){
                document.getElementById("statusCode").innerHTML = "Username cannot be empty.";
             }
            else if ( this.email == "" ){
                document.getElementById("statusCode").innerHTML = "Email cannot be empty.";
            }
            else{
                var passval = document.getElementById("enterPassword").value;
                if (passval == ""){
                    document.getElementById("statusCode").innerHTML = "Password cannot be empty.";
                }
                else if (passval.length<8 ){
                    document.getElementById("statusCode").innerHTML = "Password must be more than 8 characters long.";
                }
                else if (passval.length>20 ){
                    document.getElementById("statusCode").innerHTML = "Password must be less than 20 characters long.";
                }
                else if (!/[A-Z]/.test(passval)){
                    document.getElementById("statusCode").innerHTML = "Password must have at least 1 uppercase character.";
                }
                else if (!/[a-z]/.test(passval)){
                    document.getElementById("statusCode").innerHTML = "Password must have at least 1 lowercase character.";
                }
                else if (!/\d/.test(passval)){
                    document.getElementById("statusCode").innerHTML = "Password must have at least 1 number.";
                }
                else{
                    document.getElementById("enterUsername").placeholder= "Username";
                    document.getElementById("enterUsername").value= "";
                    document.getElementById("enterEmail").placeholder= "Email";
                    document.getElementById("enterEmail").value= "";
                    document.getElementById("enterPassword").placeholder= "Password";
                    document.getElementById("enterPassword").value= "";
                    var passsend = this.XOR_hex(passval);
                    this.password = passsend;
                    console.log("user: "+this.username+" ; pass: "+this.password);
                    document.getElementById("statusCode").innerHTML = "Waiting for reply...";
                    axios.get('/user/add', {params: {user:this.username, pass:this.password, email:this.email}})
                        .then(function (response) {
                            if (response.data == "registersuccess") {
                                document.getElementById("statusCode").innerHTML = "New user created successfully! Please login.";
                            } else if (response.data == "registeralreadyexists"){
                                document.getElementById("statusCode").innerHTML = "Error creating user: User already exists. Please login.";
                            } else {
                                document.getElementById("statusCode").innerHTML = "Error creating user: Unspecified error.";
                            }
                            console.log(response);
                        })
                        .catch(function (error) {
                            console.log(error);
                        });
                }
            }

    }
    XOR_hex(a) {
        var res = '';
        var i = 0;
        var key = '23482342234239472349898';
        while (i<a.length) {
            var xorr = (a.charCodeAt(i) ^ key.charCodeAt(i%key.length));
            var xorrr = xorr+'';
            var addme = xorrr.padStart(3,'0');
            res = res + addme;
            //console.log(addme);
            i=i+1;

        }
        return res;
    }
    handleLogin(){
        this.username = document.getElementById("enterUsername").value;
        document.getElementById("enterUsername").placeholder= "Username";
        document.getElementById("enterUsername").value= "";
        this.email = document.getElementById("enterEmail").value;
        document.getElementById("enterEmail").placeholder= "Email";
        document.getElementById("enterEmail").value= "";
        var passval = document.getElementById("enterPassword").value;
        document.getElementById("enterPassword").placeholder= "Password";
        document.getElementById("enterPassword").value= "";
        var passsend = this.XOR_hex(passval);
        this.password = passsend;
        console.log("user: "+this.username+" ; pass: "+this.password);
        document.getElementById("statusCode").innerHTML = "Waiting for reply...";
        axios.get('/user/checkLogin', {params: {user:this.username, pass:this.password, email:this.email}})
            .then(function (response) {
                if (response.data == "loginsuccess") {
                    document.getElementById("statusCode").innerHTML = "Login successful!";
                } else if (response.data == "logininvalid") {
                    document.getElementById("statusCode").innerHTML = "Invalid password.";
                } else if (response.data == "loginnouser") {
                    document.getElementById("statusCode").innerHTML = "User does not exist. Please register.";
                } else if (response.data == "logout") {
                    document.getElementById("statusCode").innerHTML = "Error, already logged in. Logging out."
                } else{
                    document.getElementById("statusCode").innerHTML = "Error communicating with server."
                }

                console.log(response);
            })
            .catch(function (error) {
                document.getElementById("statusCode").innerHTML = "Error communicating with server."
                console.log(error);
            });
    }
    handlePassword(){
     console.log("Forgot Pass")
     this.username = document.getElementById("enterUsername").value;
     document.getElementById("enterUsername").placeholder= "Username";
     document.getElementById("enterUsername").value= "";
     this.email = document.getElementById("enterEmail").value;
     document.getElementById("enterEmail").placeholder= "Email";
     document.getElementById("enterEmail").value= "";
     document.getElementById("statusCode").innerHTML = "Waiting for reply...";
     axios.get('/mail/sendemail', {params: {email:this.email, header:"Password Reset",body:"This is where the password will be set"}})
         .then(function (response) {
             if (response.data == "Email sent successfully") {
                 document.getElementById("statusCode").innerHTML = "Password reset email sent.";
             } else{
                 document.getElementById("statusCode").innerHTML = "Unable to Reset Password Via this Email."
             }

             console.log(response);
         })
         .catch(function (error) {
             console.log(error);
         });
    }
    render() {
        return (
        <div id = 'Main'>
            <h1>Login Screen</h1>
            <input type="text" name="Username" placeholder="Username" id = "enterUsername"></input><br />
            <input type="text" name="Email" placeholder="Email" id = "enterEmail"></input><br />
            <input type="password" name="Password" placeholder="Password" id = "enterPassword"></input><br />
            <button onClick={this.handleLogin} >Login</button><br />
            <button onClick={this.handlePassword} >Forgot Password</button>
            <button onClick={this.registerUser} >Create Account</button>
            <p id = "statusCode"></p>
        </div>
    );
    }
}

class UserList extends Component{
    constructor(props) {
        super(props);
        this.state = {users:[]};
    }

    componentDidMount(){
        let self = this;
        fetch('/user/all', {
            method: 'GET'
        }).then(function(response) {
            if (response.status >= 400) {
                throw new Error("Bad response from server");
            }
            return response.json();
        }).then(function(data) {
            self.setState({users: data});
        }).catch(err => {
            console.log('caught it!',err);
        })
    }

    render() {
        if (!this.state.users) {
            return <div>No Users yet...</div>
        }
        let users = Array.from(this.state.users)
        return (
            <div className="container">
                <div className="panel panel-default p50 uth-panel">
                    <table className="table table-hover">
                        <thead>
                        <tr>
                            <th>Member name</th>
                            <th>Member email</th>
                        </tr>
                        </thead>
                        <tbody>
                        {users.map(member =>
                            <tr key={member.id}>
                                <td>{member.name} </td>
                                <td>{member.email}</td>
                            </tr>
                        )}
                        </tbody>
                    </table>
                </div>
            </div>
        );
    }
}

class User extends Component{

}

ReactDOM.render(
    <Main />,
    document.getElementById('react-mountpoint')
);
