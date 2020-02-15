const React = require('react');
const ReactDOM = require('react-dom');
import '../webpack/css/main.css'
//const client = require('./client');

class Main extends Component {
    render() {
        return (
        <div id = 'Main'>
            <h1>Demo Component</h1>
            <img src="https://upload.wikimedia.org/wikipedia/commons/a/a7/React-icon.svg"/>
        </div>
    );
    }
}

ReactDOM.render(
    <Main />,
    document.getElementById('react-mountpoint')
);
