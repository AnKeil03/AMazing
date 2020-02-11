TESTING WEB CONNECTIONS


1. Start Server

cd to BackendProject directory, then type "./run.sh"
* Server runs using sudo so that it can be ran on port 80, so you will be prompted to type your password when executing run.sh




2. Test front end connection (simple)

Once server is running, open websockettest.html in browser, and the message received should display as "testing".




3. Test front end connection with React client

This test uses a basic React application created from "npm create-react-app" command with additional functionality to support web connections. The client application repeatedly sends random integers to the server, and the server replies to each message with the response "testing". To run this example:

cd to BackendProject/testweb, then type "npm start"

A basic webpage should open. Open the JavaScript console, and you should see the message "Received: 'testing'" repeatedly. On the server console, you should see "Received web message: <N>" where N is the random integer that the client sent.
