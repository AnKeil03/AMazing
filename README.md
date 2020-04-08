# AMazing
interactive URL: https://ankeil03.github.io/AMazing/


## Environment Setup
- Install Node JS
    - for Mac OS, recommend installing homebrew and typing "brew install node" in terminal
    - for windows OS, download nodejs windows installer through https://nodejs.org/en/download/
- Install MySQL Server
    - Mac: "brew install mysql" or similar for other OS
    - run mysql server on your local machine
    - edit src/back/server.js to change username,password,database fields
        * (default values are username="root",password="admin",database="amazeing")
    - enter the following queries in MySQL to create the tables to store data (will automate this later):
        CREATE TABLE mazes(
        Maze_ID INT PRIMARY KEY,
        Maze_Name TEXT,
        Maze_Schema TEXT,
        Creator_Name TEXT
        );

       CREATE TABLE users(
       User_ID INT PRIMARY KEY,
       Email TEXT,
       Password TEXT,
       Gamer_Tag TEXT
       );

       CREATE TABLE maze_attempts(
       Attempt_ID INT PRIMARY KEY,
       Maze_ID INT,
       User_ID INT,
       Number_of_Moves INT,
       Start_Time DATETIME,
       End_Time DATETIME,
       Completed BOOLEAN
       );
       
       CREATE TABLE settings(
       Setting_ID INT PRIMARY KEY,
       Next_Maze_ID INT
       );

       Maze Difficulty Query:
SELECT m.Maze_ID, Maze_Name, Creator_Name, COUNT(Completed = TRUE) AS Times_Completed, Number_of_Moves, End_Time-Start_Time AS Completion_Time FROM mazes AS m JOIN maze_attempts AS ma ON m.Maze_ID = ma.Maze_ID GROUP BY m.Maze_ID ORDER BY Number_of_Moves DESC, Times_Completed ASC, Completion_Time DESC;

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
