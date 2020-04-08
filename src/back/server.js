const http = require('http');
const hostname = '127.0.0.1';
const port = 43594;
var fs = require('fs');
var qs = require('querystring');
var mysql = require('mysql');
var nStatic = require('node-static');
var fileServer = new nStatic.Server('../front/');


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
    var nextID = 0;
    sqlcon.query("SELECT value FROM settings WHERE description='nextMazeID'", function (err, result, fields) {
      if (err) throw err;
      console.log("spam");
      console.log(result);
      console.log(result[0].value);
      console.log("spam");
      nextID = result[0].value;
      sqlcon.query("UPDATE settings SET value="+(nextID+1)+" WHERE description='nextMazeID'", function (err, result, fields) {
        if (err) throw err;
        console.log(result);
        var qsql = "INSERT INTO mazes (maze_id, maze_name, maze_schema, creator_name) VALUES ("+nextID+", 'maze number: "+nextID+"', '"+mdat+"','noname')";
        //console.log('attempting query: '+qsql);
        sqlcon.query(qsql, function (err, result) {
        if (err) throw err;
        console.log("1 record inserted");
        doSelectQuery(console.log)
      });
      });
    });


}

function listMazes(response) {
    var ch = '';
    response.writeHeader(200, {"Content-Type": "text/html"});
    sqlcon.query("SELECT * FROM mazes;", function (err, result, fields) {
      if (err) throw err;
      for (let i=0; i<result.length; i++) {
          ch = ch + '' + result[i].maze_id+ ',' + result[i].maze_name +',' + result[i].creator_name + "<br/>";
      }
      ch = '<p>'+ch+'</p>'
      fs.readFile('../front/mazeList.html', function (err, html) {
          response.write(html);
          response.write(ch);
          response.end();
      });
    });

}


function registerUser(gamertag,email,password,response) {
    sqlcon.query("SELECT Gamer_Tag FROM users WHERE Gamer_Tag='"+gamertag+"';", function (err, result, fields) {
      if (result.length==0) {

         sqlcon.query("SELECT Email FROM users WHERE Email='"+email+"';", function (err, result2, fields) {
            if (result2.length==0) {
               var qsql = "INSERT INTO users (Email, Password, Gamer_Tag) VALUES ('"+email+"', '"+password+"', '"+gamertag+"');";
               sqlcon.query(qsql, function (err, result3) {
                 if (err) throw err;
                 console.log("new user record inserted for "+gamertag);
                 response.writeHeader(200, {"Content-Type": "text/html"});
                 response.write("registerok");
                 response.end();
               });
            } else {
                response.writeHeader(200, {"Content-Type": "text/html"});
                response.write("emailalreadyexists");
                response.end();
            }
         });

      } else {

        response.writeHeader(200, {"Content-Type": "text/html"});
        response.write("alreadyexists");
        response.end();
      }
    });
}

function loginCheck(request,body,response) {
    var post = qs.parse(body);
    var email = post.email;
    var pass = post.password;
    console.log("attempting login check for "+email+":"+pass);
    sqlcon.query("SELECT Password FROM users WHERE Email='"+email+"';", function (err, result, fields) {
          if (result.length>0) {
            var pwshouldbe = result[0].Password;
            console.log("comparing entered password with expected: "+pwshouldbe);
            if (pwshouldbe == pass) {
                response.writeHeader(200, {"Content-Type": "text/html"});
                response.write("loginok");
                response.end();
            } else {
                response.writeHeader(200, {"Content-Type": "text/html"});
                response.write("invalid");
                response.end();
            }
          } else {
            response.writeHeader(200, {"Content-Type": "text/html"});
            response.write("nouser");
            response.end();
          }
    });
}



const requestHandler = (request, response) => {
    if (request.method=='POST') {
        var body = '';
        request.on('data', function (data) {
            body += data;
            console.log('A chunk of data has arrived: '+data);

            if (request.url.startsWith("/register/")) {
                var post = qs.parse(body);
                console.log("register details: "+post.gamertag+","+post.email+","+post.password+","+post.confPassword);
                if (post.password!=post.confPassword) {
                    response.writeHeader(200, {"Content-Type": "text/html"});
                    response.write("nomatch");
                    response.end();
                }
                else if (post.password.length<8) {
                    response.writeHeader(200, {"Content-Type": "text/html"});
                    response.write("small");
                    response.end();
                }
                 else {
                    registerUser(post.gamertag,post.email,post.password,response);
                }
            }
            else if (request.url.startsWith("/login/")) {
                loginCheck(request,body,response);
            }
            else if (request.url.startsWith("/createMaze/")) {
                request.on('end', function () {
                    var post = qs.parse(body);
                    insertMaze(post.id,post.contents);
                });
            }
        });

        console.log("post url: "+request.url);

    } else {
  console.log('received'+request.body)

  if (request.url == '/') {
      response.writeHeader(200, {"Content-Type": "text/html"});
      fs.readFile('../front/index.html', function (err, html) {
      if (err) {
        response.write("404 NOT FOUND: "+request.url);
        response.end();
      } else {
        response.write(html);
        response.end();
      }
      });
  } else if (request.url.endsWith(".js")){

        var x = 0
        fs.readFile('../front'+request.url, function (err, html) {
              if (err) {
                  response.write("404 NOT FOUND: "+request.url);
                  response.end();
                  x = 1;
              }
        });
        if (x==0) {
            const jscr = fs.readFileSync("../front/"+request.url);
            response.setHeader("Content-Type", "text/javascript");
            response.write(jscr);
            response.end();
        }
  } else if (request.url.endsWith(".css")) {
      const csss = fs.readFileSync("../front/style.css");
      response.writeHeader(200, {"Content-Type": "text/css"});
      response.write(csss);
      response.end();
  } else if (request.url.endsWith("mazeList.html")) {
 listMazes(response);
} /*else if (request.url.startsWith("mazeInteraction/")) {
    var x = request.url.substring(16);
    response.writeHeader(200, {"Content-Type": "text/html"});
    fs.readFile('../front/mazeInteraction.html', function (err, html) {
        response.write(html);
        response.write('x:'+x);
        response.end();
    });

}*/else {
      response.writeHeader(200, {"Content-Type": "text/html"});
      console.log("URI:"+request.url);
      fs.readFile('../front'+request.url, function (err, html) {
            if (err) {
                response.write("404 NOT FOUND: "+request.url);
                response.end();
            } else {
                response.write(html);
                response.end();
          }
      });
  }
}
}

const server = http.createServer(requestHandler)

server.listen(port, hostname, () => {
  console.log(`Server running at http://${hostname}:${port}/`);
});

//res.statusCode = 200;
  //res.setHeader('Content-Type', 'text/plain');
  //res.end('Hello, World!\n');
