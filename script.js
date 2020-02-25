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
		}
		// if tile is closed
		else if ( e.target.classList.contains("closed") ) {
			e.target.classList.remove("closed")
			e.target.classList.add("open")
		}
	}
}

// begin

initialize()
render()
