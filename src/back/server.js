const http = require('http');
const hostname = '127.0.0.1';
const port = 43594;
var fs = require('fs');
var qs = require('querystring');
var mysql = require('mysql');

var sqlcon = mysql.createConnection({
  host: "localhost",
  port: 3306,
  user: "root",
  password: "admin",
  database: "amazeing"
});

sqlcon.connect(function(err) {
  if (err) throw err;
  sqlcon.query("SELECT * FROM mazes", function (err, result, fields) {
    if (err) throw err;
    console.log(result);
  });
});

function doSelectQuery(callback) {
    sqlcon.query("SELECT * FROM mazes", function (err, result, fields) {
      if (err) throw err;
      callback(result);
    });
}

function insertMaze(mid,mdat) {
    var qsql = "INSERT INTO mazes (maze_id, maze_data) VALUES ("+mid+", '"+mdat+"')";
    //console.log('attempting query: '+qsql);
    sqlcon.query(qsql, function (err, result) {
    if (err) throw err;
    console.log("1 record inserted");
    doSelectQuery(console.log)
  });
}



const requestHandler = (request, response) => {
    if (request.method=='POST') {
        //var post = qs.parse(body);
        var body = '';
        request.on('data', function (data) {
            body += data;
            //console.log('A chunk of data has arrived: '+data);
            //doSelectQuery(console.log)
        });


        request.on('end', function () {
            var post = qs.parse(body);
            //console.log("post had "+post.contents);
            insertMaze(post.id,post.contents)
        });
    }
  console.log('received'+request.body)

  if (request.url == '/') {
      response.writeHeader(200, {"Content-Type": "text/html"});
      fs.readFile('../front/index.html', function (err, html) {
          if (err) {
              throw err;
          }
          response.write(html);
          //fs.createReadStream("../front/index.html").pipe(response);
          response.end();
      });
  } else if (request.url.endsWith(".js")){
      const jscr = fs.readFileSync("../front/script.js");
      response.setHeader("Content-Type", "text/javascript");
      response.write(jscr);
      //fs.createReadStream("../front/script.js").pipe(response);
      response.end();
  } else {
      const csss = fs.readFileSync("../front/style.css");
      response.writeHeader(200, {"Content-Type": "text/css"});
      response.write(csss);
      //fs.createReadStream("../front/style.css").pipe(response);
      response.end();
  }
  //response.end('Hello Node.js Server!')
}

const server = http.createServer(requestHandler)

server.listen(port, hostname, () => {
  console.log(`Server running at http://${hostname}:${port}/`);
});

//res.statusCode = 200;
  //res.setHeader('Content-Type', 'text/plain');
  //res.end('Hello, World!\n');
