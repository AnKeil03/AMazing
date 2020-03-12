import React, {Component} from 'react'
import ReactDOM from 'react-dom'
//const React = require('react');
//const ReactDOM = require('react-dom');
import '../webpack/css/main.css'
//const client = require('./client');
import axios from 'axios';

class Main extends Component {

render() {
        return (
        <div id = 'Home-admin'>
            <h1>This is Admin Screen!</h1>
        </div>
    );
    }
}
ReactDOM.render(
    <Home-admin />,
    document.getElementById('admin-Home')
);
