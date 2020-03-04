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
        this.username = "me";
        this.password = "you";
        this.registerUser = this.registerUser.bind(this);
        this.forgotPassword = this.forgotPassword.bind(this);
        //this.handleClick = this.handleClick.bind(this);
        this.handleLogin = this.handleLogin.bind(this);
    }
    registerUser() {
            this.username = document.getElementById("enterUsername").value;
            var passval = document.getElementById("enterPassword").value;
            var passsend = this.XOR_hex(passval);
            this.password = passsend;
            console.log("user: "+this.username+" ; pass: "+this.password);
            document.getElementById("statusCode").innerHTML = "Waiting for reply...";
            axios.get('/user/add', {params: {user:this.username, pass:this.password, email:"test@email.com"}})
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
    forgotPassword() {
            document.getElementById("statusCode").innerHTML = "This function is not supported yet.";
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
            console.log(addme);
            i=i+1;

        }
        return res;
    }
    handleLogin(){
        this.username = document.getElementById("enterUsername").value;
        var passval = document.getElementById("enterPassword").value;
        var passsend = this.XOR_hex(passval);
        this.password = passsend;
        console.log("user: "+this.username+" ; pass: "+this.password);
        document.getElementById("statusCode").innerHTML = "Waiting for reply...";
        axios.get('/user/checkLogin', {params: {user:this.username, pass:this.password}})
            .then(function (response) {
                if (response.data == "loginsuccess") {
                    document.getElementById("statusCode").innerHTML = "Login successful!";
                } else if (response.data == "logininvalid") {
                    document.getElementById("statusCode").innerHTML = "Invalid password.";
                } else if (response.data == "loginnouser") {
                    document.getElementById("statusCode").innerHTML = "User does not exist. Please register.";
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
            <input type="password" name="Password" placeholder="Password" id = "enterPassword"></input><br />
            <button onClick={this.handleLogin} >Login</button><br />
            <button onClick={this.forgotPassword} >Forgot Password</button>
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
