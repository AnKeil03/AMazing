# AMazing
interactive URL: https://ankeil03.github.io/AMazing/


## Environment Setup
- Install Node JS
    - for Mac OS, recommend installing homebrew and typing "brew install node" in terminal
    - other group mems pls post other OS instructions for this
- Install MySQL Server
    - Mac: "brew install mysql" or similar for other OS
    - run mysql server on your local machine
    - edit src/back/server.js to change username,password,database fields
        * (default values are username="root",password="admin",database="amazeing")
    - enter the following query in MySQL to create a table for storing mazes (will automate this later)
        CREATE TABLE mazes(maze_id INTEGER, maze_data TEXT);

## Running the Server
- Navigate to src/back in terminal
- Type "node server.js" to launch the server
    - server runs on port 43594 by default

## Accessing the Server from Web Browser
- Open a webpage and enter the url "127.0.0.1:43594"
- Maze creation screen should pop up after server sends index.html
- Draw whatever maze you want
- Click the button "send the maze to server" at the bottom left of the webpage


## Verifying Maze Data Retrieval/Storage from Server
- after clicking "send maze to server", look at command line window that is running node server
- some text should display saying that one record has been updated
- server is set to call "SELECT * FROM mazes;" after an insert, so you should also be able to
  see the current list of all mazes stored in database
- SELECT * FROM MAZES; is also called when server starts, so you can restart the server
  to verify that the data is persistently stored
