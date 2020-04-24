"use strict"

class Tuple {
	constructor(x, y) {
		this.x = x
		this.y = y
	}
}

let data = [
	[0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1],
	[3, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 2, 1],
	[1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1],
	[1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1],
	[1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1],
	[1, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1],
	[1, 0, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1],
	[1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1],
	[1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1],
	[1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1],
	[1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1],
	[1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1],
	[1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1],
	[1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1],
	[1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1]
]


let startPoint = new Tuple(13, 1)
let endPoint = new Tuple(0, 1)
let playerPoint = new Tuple(startPoint.x, startPoint.y)
let moves = 0
const HEIGHT = data.length
const WIDTH = data[0].length

function loadData(load) {
    var i=0;
    for (var j=0; j<HEIGHT; j++) {
        for (var k=0; k<WIDTH; k++) {
            if (i<load.length) {
                var d = load.charAt(i);
                data[j][k] = d;
                if (d==2) {
                    startPoint.y = j;
                    startPoint.x = k;
                    playerPoint.y = j;
                    playerPoint.x = k;
                }
                else if (d==3) {
                    endPoint.y = j;
                    endPoint.x = k;
                }
                i++;
            }
        }
    }
    console.log("data changed?")
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
			// append to row
			row.appendChild(tile)
		}
		// append to document
		maze.appendChild(row)
	}
	// put player on the map
	let player = document.createElement("div")
	player.id = "player"
	let startTile = getTileByCoordinates(startPoint.x, startPoint.y)
	startTile.appendChild(player)
	document.getElementById("moves").textContent = moves
}

function getTileByCoordinates(x, y) {
	return document.getElementById("tile" + x + "," + y)
}

function move(x, y) {
	
	// out-of-bounds detection
	let moveTo = data[playerPoint.y+y]
	if (moveTo != undefined) {
		moveTo = moveTo[playerPoint.x+x]
	}
	
	let playerDiv = getTileByCoordinates(playerPoint.x, playerPoint.y)
	if ( moveTo != 1 && moveTo != undefined) {
		moves++
		document.getElementById("moves").textContent = moves
		// legal move, move the player
		playerPoint = new Tuple(playerPoint.x+x, playerPoint.y+y)
		// remove old point from map
		playerDiv.removeChild( document.getElementById("player") )
		// add new point to map
		let newTile = getTileByCoordinates(playerPoint.x, playerPoint.y)
		let player = document.createElement("div")
		player.id = "player"
		newTile.appendChild(player)
		// check if the player has won
		if ( moveTo == 3 ) {
			// player has won
			alert("A winner is you!")
		}
	}
	else {
		// illegal move, do nothing
	}
}

onkeydown = function(e) {
	
	switch (e.key) {
		case "ArrowUp":
			move(-1, 0)
			break
		case "ArrowDown":
			move(1, 0)
			break
		case "ArrowLeft":
			move(0, -1)
			break
		case "ArrowRight":
			move(0, 1)
			break
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

function requestMaze() {
const URL='http://127.0.0.1:43594/mazeInteraction'
var mazeid = readCookie("playingMaze");

	const dat={
		requestmaze:mazeid
	}

	$.post(URL,dat,function(reply,status) {
		console.log('sent POST and got reply=<'+reply+'>')

		if (reply.startsWith('mazedata=') ){
		    console.log("got data");
		    var dat = reply.split('=')[1];
		    var mazen = dat.split(';')[0];
		    var mazed = dat.split(';')[1];
		    var mazetitle = document.getElementById("mazetitle");
		    mazetitle.innerHTML = "Maze Solver: "+mazen;
		    loadData(mazed);
		    render();
        }
	});
}

// begin
//render()

requestMaze()

