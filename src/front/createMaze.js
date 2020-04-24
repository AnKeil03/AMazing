"use strict"
var mazemsg = document.getElementById("mazewarn");

class Tuple {
	constructor(x, y) {
		this.x = x
		this.y = y
	}
}

const WIDTH = 15
const HEIGHT = WIDTH
let data = []
let active
let isMouseDown = false
let draw = true
let tool = "drawErase"
let startPoint = new Tuple(0, 0)
let endPoint = new Tuple(0, 0)

document.getElementById("drawErase").onclick = function() {
	tool = "drawErase"
}

document.getElementById("setStart").onclick = function() {
	tool = "setStart"
}

document.getElementById("setEnd").onclick = function() {
	tool = "setEnd"
}

// create blank maze
function initialize() {
	for (let y=0; y<HEIGHT; y++) {
		data[y] = []
		for (let x=0; x<WIDTH; x++) {
			data[y][x] = 0
		}
	}
}

// draw initial maze
function render() {
	let maze = document.getElementById("maze")
	for (let y=0; y<HEIGHT; y++) {
		let row = document.createElement("div")
		row.classList.add("row")
		for (let x=0; x<WIDTH; x++) {
			let tile = document.createElement("div")
			tile.classList.add("tile")
			tile.setAttribute("data-value", data[y][x])
			tile.setAttribute("data-x", x)
			tile.setAttribute("data-y", y)
			tile.id = "tile" + x + "," + y
			tile.onmouseover = mouseOver
			tile.onmousedown = mouseDown
			tile.onmouseup = mouseUp
			// append to row
			row.appendChild(tile)
		}
		// append to document
		maze.appendChild(row)
	}
}

function getTileByCoordinates(x, y) {
	return document.getElementById("tile" + x + "," + y)
}

function mouseDown(e) {
	isMouseDown = true
	switch (tool) {
		case "drawErase":
			// if tile is open
			if ( e.target.getAttribute("data-value") == "0" ) {
				draw = true
			}
			else {
				draw = false
			}
			// draw or erase tile we clicked
			drawErase(e)
			break
		case "setStart":
			setStart(e)
			break
		case "setEnd":
			setEnd(e)
			break
	}
}

function mouseUp() {
	isMouseDown = false
}

function mouseOver(e) {
	if (isMouseDown) {
		switch (tool) {
			case "drawErase":
				drawErase(e)
				break
			case "setStart":
				setStart(e)
				break
			case "setEnd":
				setEnd(e)
				break
		}
	}
}

function drawErase(e) {
	let x = Number( e.target.getAttribute("data-x") )
	let y = Number( e.target.getAttribute("data-y") )
	// if we are drawing
	if (draw) {
		//console.log("Closing...")
		data[y][x] = 1
		e.target.setAttribute("data-value", "1")
	}
	// if we are erasing
	else {
		//console.log("Opening...")
		data[y][x] = 0
		e.target.setAttribute("data-value", "0")
	}
}

function setStart(e) {
	if (isMouseDown && tool == "setStart") {
		let x = Number( e.target.getAttribute("data-x") )
		let y = Number( e.target.getAttribute("data-y") )
		// remove old start
		data[startPoint.y][startPoint.x] = 0
		let oldTile = getTileByCoordinates(startPoint.x, startPoint.y)
		if (oldTile) {
			oldTile.setAttribute("data-value", 0)
		}
		// set new start
		// 2 represents start
		data[y][x] = 2
		startPoint.x = x
		startPoint.y = y
		e.target.setAttribute("data-value", "2")
	}
}

function setEnd(e) {
	if (isMouseDown && tool == "setEnd") {
		let x = Number( e.target.getAttribute("data-x") )
		let y = Number( e.target.getAttribute("data-y") )
		// remove old end
		data[endPoint.y][endPoint.x] = 0
		let oldTile = getTileByCoordinates(endPoint.x, endPoint.y)
		if (oldTile) {
			oldTile.setAttribute("data-value", 0)
		}
		// set new end
		// 3 represents end
		data[y][x] = 3
		endPoint.x = x
		endPoint.y = y
		e.target.setAttribute("data-value", "3")
	}
}

//https://stackoverflow.com/questions/5639346/what-is-the-shortest-function-for-reading-a-cookie-by-name-in-javascript
function readCookie(name) {
  var nameEQ = name + "=";
  var ca = document.cookie.split(';');
  for(var i=0;i < ca.length;i++) {
    var c = ca[i];
    while (c.charAt(0)==' ') c = c.substring(1,c.length);
    if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
  }
  return null;
}
var button = document.createElement("button");
button.innerHTML = "Send Maze to Server";
var body = document.getElementsByTagName("body")[0];
body.appendChild(button);
var nextMazeID = 0;
button.addEventListener ("click", function() {
    var sessionID = readCookie("userSession");
    var mazeName = document.getElementById("mazename");

    if (sessionID==null) {
        mazemsg.innerHTML = "Login session expired."
    }
    else if (mazeName==null || mazeName.value.length<1) {
        mazemsg.innerHTML = "Please enter a name for your maze."
    }
    else {
        const URL='http://127.0.0.1:43594/createMaze/'
        var sendmaze = '';
        for (let i=0; i<WIDTH;i++) {
            for (let j=0;j<HEIGHT;j++) {
                sendmaze += data[i][j]
            }
        }
        const dat={
            name:mazename.value,
            contents:sendmaze
        }
        $.post(URL,dat,function(response,status) {
            //console.log('sent POST with response=<'+response+'>')
            if (response.startsWith('mazeok')) {
                var id = response.split(';')[1];
                mazemsg.innerHTML = "Maze submitted successfully! <a href='mazeInteraction/"+id+"'>Click here to play.</a>";
            }
        });
	}
});

function checkForUser() {
     var sessionID = readCookie("userSession");
     if (sessionID==null) {
        mazemsg.innerHTML = "WARNING: You are not logged in, so your maze will not be saved. Please <a href='login.html'>log in</a> to be able to submit mazes to the server.";
     }
}

// begin

initialize()
render()
checkForUser()

