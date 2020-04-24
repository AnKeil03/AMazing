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


//https://stackoverflow.com/questions/3393854/get-and-set-a-single-cookie-with-node-js-http-server
var get_cookies = function(request) {
  var cookies = {};
  if (request.headers.cookie == null) {
    return null;
  }
  request.headers && request.headers.cookie.split(';').forEach(function(cookie) {
    var parts = cookie.match(/(.*?)=(.*)$/)
    cookies[ parts[1].trim() ] = (parts[2] || '').trim();
  });
  return cookies;
};

function doSelectQuery(callback) {
    sqlcon.query("SELECT * FROM mazes", function (err, result, fields) {
      if (err) throw err;
      callback(result);
    });
}

function insertMaze(mname,mdat,sesh,mid) {
    var nextID = 0;
    sqlcon.query("SELECT value FROM settings WHERE description='nextMazeID'", function (err, result, fields) {
      if (err) throw err;
      console.log("spam");
      console.log(result);
      console.log(result[0].value);
      console.log("spam");
      nextID = result[0].value;
      sqlcon.query("UPDATE settings SET value="+(mid+1)+" WHERE description='nextMazeID'", function (err, result, fields) {
        if (err) throw err;
        console.log(result);
        sqlcon.query("SELECT Gamer_Tag FROM users WHERE Session_Key='"+(sesh)+"';", function (err, result, fields) {
            var qsql = "INSERT INTO mazes (maze_id, maze_name, maze_schema, creator_name) VALUES ("+mid+", '"+mname+"', '"+mdat+"','"+result[0].Gamer_Tag+"')";
            //console.log('attempting query: '+qsql);
            sqlcon.query(qsql, function (err, result) {
            if (err) throw err;
            console.log("1 record inserted");
           // doSelectQuery(console.log)
          });
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
          ch = ch + '<a href="mazeInteraction/' +result[i].maze_id+'">' +result[i].maze_name+'</a> - created by ' +result[i].creator_name+ "<br/>";
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
    var sessionKey = "zk";
    for (var i=0; i<10; i++) {
        var rnum = Math.ceil(Math.random()*100);
        sessionKey = sessionKey+""+rnum;
    }
    sqlcon.query("SELECT Password,Gamer_Tag FROM users WHERE Email='"+email+"';", function (err, result, fields) {
          if (result.length>0) {
            var pwshouldbe = result[0].Password;
            var gt = result[0].Gamer_Tag;
            if (pwshouldbe == pass) {
                //response.cookie("username","420");
                response.writeHeader(200, {'Set-Cookie': 'userSession='+sessionKey+'; Max-Age=3600',"Content-Type": "text/html"});
                response.write("loginok");
                response.end();
                sqlcon.query("UPDATE users SET Session_Key='"+sessionKey+"' WHERE Email='"+email+"';", function (err, result3) {
                     if (err) throw err;
                     console.log("stored session id "+sessionKey+" for user "+gt);
               });
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
            else if (request.url.startsWith("/login.html")) {
                loginCheck(request,body,response);
            }
            else if (request.url.startsWith("/createMaze/")) {
                request.on('end', function () {
                    var post = qs.parse(body);
                    var cookies = get_cookies(request);
                    var sessionID = cookies['userSession'];
                    var mazeid = -1;
                    sqlcon.query("SELECT value FROM settings WHERE description='nextMazeID'", function (err, result, fields) {
                        mazeid = result[0].value;
                        insertMaze(post.name,post.contents,sessionID,mazeid);
                        response.writeHeader(200, {"Content-Type": "text/html"});
                        response.write("mazeok;"+mazeid);
                        response.end();
                    });

                });
            }
            else if (request.url.endsWith("user.html")) {
                   // var cook0 = parseCookies(request);
                   // var cook4 = cook0.userSession;
                    var cook0 = JSON.stringify(request.headers);
                    //console.log("cook: "+cook);
                    //console.log("cookies: "+cookies);
                    var post = qs.parse(body);
                    var sendstuff = post.sendstuff;
                    if (sendstuff!=null) {

                    var cookies = get_cookies(request);
                    if (cookies!=null) {

                    var sessionID = cookies['userSession'];
                    if (sessionID!=null) {
                    sqlcon.query("SELECT Gamer_Tag,Email FROM users WHERE Session_Key='"+sessionID+"';", function (err, result, fields) {
                      if (result.length>0) {
                        var email = result[0].Email;
                        var gt = result[0].Gamer_Tag;

                        response.writeHeader(200, {"Content-Type": "text/html"});
                        response.write("username="+gt+";email="+email);
                        response.end();

                      }
                    });
                    }
                    }
                 }
             }
             else if (request.url.startsWith("/userlogout")) {
                var cookies = get_cookies(request);
                var sessionID = cookies['userSession'];
                if (sessionID!=null) {
                response.writeHeader(200, {'Set-Cookie': 'userSession=0; Max-Age=0',"Content-Type": "text/html"});
                response.write("gtfo");
                response.end();
                }
             }
             else if (request.url.startsWith("/userchangepw")) {
                var cookies = get_cookies(request);
                var sessionID = cookies['userSession'];
                var dat = qs.parse(body);
                var curpw = dat.curpw;
                var newpw = dat.newpw;
                var newpwconf = dat.newpwconf;
                if (sessionID!=null) {

                    if (newpw!=newpwconf) {
                        response.writeHeader(200, {"Content-Type": "text/html"});
                        response.write("pw-nomatch");
                        response.end();
                    } else {

                        sqlcon.query("SELECT Password FROM users WHERE Session_Key='"+sessionID+"';", function (err, result, fields) {
                          if (result.length>0) {
                            var pwshouldbe = result[0].Password;
                            if (pwshouldbe!=curpw) {
                                response.writeHeader(200, {"Content-Type": "text/html"});
                                response.write("pw-invalid");
                                response.end();
                            } else {

                                sqlcon.query("UPDATE users SET Password='"+newpw+"' WHERE Session_Key='"+sessionID+"';", function (err, result, fields) {
                                    response.writeHeader(200, {"Content-Type": "text/html"});
                                    response.write("pw-changed");
                                    response.end();
                                });
                            }
                          }
                        });

                    }

                }
             }
             else if (request.url.startsWith("/usermazes")) {
                var cookies = get_cookies(request);
                var sessionID = cookies['userSession'];
                var resp = "";
                if (sessionID!=null) {
                    sqlcon.query("SELECT Gamer_Tag FROM users WHERE Session_Key='"+sessionID+"';", function (err, result, fields) {
                        var gt = result[0].Gamer_Tag;
                        sqlcon.query("SELECT maze_id,maze_name FROM mazes WHERE creator_name='"+gt+"';", function (err, result, fields) {
                            for (var i=0; i<result.length; i++) {
                                resp = resp+""+result[i].maze_name+","+result[i].maze_id+";";

                            }
                            response.writeHeader(200, {"Content-Type": "text/html"});
                            response.write("mazes="+resp);
                            response.end();
                        });

                    });
                }
             }
             else if (request.url.startsWith("/mazeInteraction")) {
                var cookies = get_cookies(request);
                var sessionID = cookies['userSession'];
                var mazeID = cookies['playingMaze'];
                var resp = "";
                console.log("mazeid to load: "+mazeID);
                if (sessionID!=null) {

                    if (mazeID!=null) {
                    sqlcon.query("SELECT Gamer_Tag FROM users WHERE Session_Key='"+sessionID+"';", function (err, result, fields) {
                        var gt = result[0].Gamer_Tag;
                        sqlcon.query("SELECT maze_name,maze_schema FROM mazes WHERE maze_id='"+mazeID+"';", function (err, resultmaze, fields) {
                            resp = resultmaze[0].maze_name+";"+resultmaze[0].maze_schema;
                            response.writeHeader(200, {"Content-Type": "text/html"});
                            response.write("mazedata="+resp);
                            response.end();
                            console.log("sent maze data");
                        });

                    });
                    } else {
                        console.log("nullmazeid");
                    }
                } else {
                    console.log("nullsession");
                }
             }
        });

        console.log("post url: "+request.url);



    } else { //GET requests



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
        /*fs.readFile('../front'+request.url, function (err, html) {
              if (err) {
                  response.write("404 NOT FOUND: "+request.url);
                  response.end();
                  x = 1;
              }
        });*/
        if (x==0) {
            var filetoread = request.url;
            if (request.url.startsWith("/mazeInteraction")) {
                filetoread = "../front/mazeSolver.js";
            }
            const jscr = fs.readFileSync("../front/"+filetoread);
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
}
    else if (request.url.endsWith("user.html")) {
        //var cook = parseCookies(request.headers.Cookie);
        //var cookies = JSON.stringify(request.headers);
        //console.log("cook: "+cook);
        //console.log("cookies: "+cookies);
        response.writeHeader(200, {"Content-Type": "text/html"});
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
    else if (request.url.startsWith("/mazeInteraction/")) {
        var x = request.url.substring(17);
        console.log("cookie set to "+x);
        response.writeHeader(200, {'Set-Cookie': 'playingMaze='+x+'; Max-Age=3600',"Content-Type": "text/html"});
        fs.readFile('../front/mazeInteraction.html', function (err, html) {
            response.write(html);
            response.end();
        });

    } else {
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
