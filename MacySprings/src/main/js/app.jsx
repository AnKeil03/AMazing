import React, {Component} from 'react'
import ReactDOM from 'react-dom'
//const React = require('react');
//const ReactDOM = require('react-dom');
import '../webpack/css/main.css'
//const client = require('./client');

class Main extends Component {

    render() {
        return (
        <div id = 'Main'>
            <h1>Login Screen</h1>
            <input type="text" defaultValue="Username" ></input><br />
            <input type="text" defaultValue="Password" ></input><br />
            <button>Login</button><br />
            <button>Forgot Password</button><button>Create Account</button>

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
