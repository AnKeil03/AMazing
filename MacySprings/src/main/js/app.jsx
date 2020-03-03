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
        this.handleClick = this.handleClick.bind(this);
        this.handleLogin = this.handleLogin.bind(this);
    }
    handleClick() {
            console.log('Click happened');

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
        axios.get('/user/checkLogin', {params: {user:this.username, pass:this.password}})
            .then(function (response) {
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
            <input type="text" name="Password" placeholder="Password" id = "enterPassword"></input><br />
            <button onClick={this.handleLogin} >Login</button><br />
            <button onClick={this.handleClick} >Forgot Password</button>
            <button onClick={this.handleClick} >Create Account</button>

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
