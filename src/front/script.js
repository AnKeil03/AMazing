"use strict"

const WIDTH = 30
const HEIGHT = WIDTH
let data = []
let active
let isMouseDown = false

function initialize() {
	for (let y=0; y<HEIGHT; y++) {
		data[y] = []
		for (let x=0; x<WIDTH; x++) {
			data[y][x] = 0
		}
	}

}

function render() {
	let maze = document.getElementById("maze")
	for (let y=0; y<HEIGHT; y++) {
		let row = document.createElement("div")
		row.classList.add("row")
		for (let x=0; x<WIDTH; x++) {
			let tile = document.createElement("div")
			tile.classList.add("tile")
			tile.classList.add("open")
			tile.setAttribute("data-x", x)
			tile.setAttribute("data-y", y)
			//tile.addEventListener("onclick", toggleOpen)
			tile.onmouseover = toggleOpen

			// append to row
			row.appendChild(tile)

		}
		// append to document
		maze.appendChild(row)
		onmousedown = mouseDown
		onmouseup = mouseUp
	}


}

var button = document.createElement("button");
button.innerHTML = "Send Maze to Server";
var body = document.getElementsByTagName("body")[0];
body.appendChild(button);
var buildmaze = '';
var nextMazeID = 0;
button.addEventListener ("click", function() {

	const URL='http://127.0.0.1:43594'

	const data={
		name:"maze",
		id:nextMazeID++,
		contents:buildmaze
	}
	$.post(URL,data,function(data,status) {
		console.log('sent POST with data=<'+data+'>')
	});
});

function mouseDown() {
	isMouseDown = true
}

function mouseUp() {
	isMouseDown = false
}

function toggleOpen(e) {
	if (isMouseDown) {

		// if tile is open
		if ( e.target.classList.contains("open") ) {
			e.target.classList.remove("open")
			e.target.classList.add("closed")
			var x = e.target.getAttribute("data-x");
			var y = e.target.getAttribute("data-y");
			console.log("closed "+x+","+y);
			toggleMazeData(x,y);

		}
		// if tile is closed
		else if ( e.target.classList.contains("closed") ) {
			e.target.classList.remove("closed")
			e.target.classList.add("open")
			var x = e.target.getAttribute("data-x");
			var y = e.target.getAttribute("data-y");
			console.log("opened "+x+","+y);
			toggleMazeData(x,y);
		}
	}
}

function toggleMazeData(x,y) {

	if (data[y][x]==1) {
		data[y][x]=0;
	} else {
		data[y][x]=1;
	}
	buildmaze=""
	for (let a=0; a<HEIGHT; a++) {
		for (let b=0; b<WIDTH; b++) {
			buildmaze+=data[a][b]
		}
	}
}

// begin

initialize()
render()
