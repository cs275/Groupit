var express = require('express'),
    app = express(),
    fs = require('fs');

var groups, users;
try{
	groups = JSON.parse(fs.readFileSync("./models/groups.json"));
}catch(e){
	groups = [];
}

try{
	messages = JSON.parse(fs.readFileSync("./models/messages.json"))
}catch(e){
	messages = {};
}

app.listen(3000);
console.log('Listening on port 3000');

app.use(express.static(__dirname + '/public'));

var bodyParser = require('body-parser')
app.use( bodyParser.json() );       // to support JSON-encoded bodies
app.use( bodyParser.urlencoded() ); // to support URL-encoded bodies
 
app.post('/allGroups/', function(req, res) {
	res.send(groups);
});

app.post('/allGroupNames/', function(req, res){
	var allNames = [];
	for (var i=0; i<groups.length; i++){
		allNames.push(groups[i].name);
	}
	if (allNames.length==0)
		res.send("null");
	else
		res.send(allNames);
});

app.post('/getGroupsForUser/', function(req, res){
	var user = req.param('user', null),
		memberOf = [];
	for (var i=0; i<groups.length; i++){
		if (groups[i].members.indexOf(user) != -1){
			memberOf.push(groups[i].name);
		}
	}
	if (memberOf.length==0)
		res.send("null");
	else
		res.send(memberOf);
});

app.post('/getGroup/', function(req, res){
	var group = req.param('user', null),
		memberOf = [];
	for (var i=0; i<groups.length; i++){
		if (group[i].name==group){
			memberOf.push(groups[i]);
		}
	}
	if (memberOf.length==0)
		res.send("null");
	else
		res.send(memberOf);
});


app.post('/getMembers/', function(req, res){
	var group = req.param('group', null);
	res.send(getGroup(group)["members"]);
});

app.post("/search/", function(req, res){
	var query = req.param('query', null);
	var matches = [];
	for (var i=0; i<groups.length; i++){
		if (groups[i].name.toLowerCase().indexOf(query.toLowerCase())!=-1) matches.push(groups[i].name);
	}
	if (matches.length == 0)
		res.send("null");
	else
		res.send(matches);
});	

app.post('/joinGroup/', function(req, res){
	var user = req.param('user', null),
		group = req.param('group', null);
	for (var i=0; i<groups.length; i++){
		if (groups[i].name == group){
			groups[i].members.push(user);
			fs.writeFile("./models/groups.json", JSON.stringify(groups));
			res.send("1");
			return;
		}
	}
	res.send("null");
});

app.post('/newGroup/', function(req, res) {
    var name = req.body.name,
       	description = req.body.description,
       	admin = req.body.admin,
       	location = req.body.location,
       	category = req.body.category;

    var object = {
    	"name":name,
    	"description":description,
    	"admin":admin,
    	"location":location,
    	"category":category,
    	"members": [admin]
    }
    groups.push(object);
    fs.writeFile("./models/groups.json", JSON.stringify(groups));

    console.log("Name: ", name);
    res.send("Hello " + name);
});

app.post('/sendMessage/', function(req, res){
	var group = req.query.group,
		user = req.query.user,
		message = req.query.message;
	if (messages[group]){
		messages[group].data.push({"user":user,"message":message});
		res.send("Success")
	}else{
		messages.push({"data":[{"user":user,"message":message}]});
		res.send("Created new thread");
	}
	fs.writeFile("./models/messages.json", JSON.stringify(groups))
});

app.post('/getMessages/', function(req, res){
	var group = req.query.group,
		start = req.query.start||0;
		end = req.query.end||100;
	if (messages[group])
		res.send( messages[group].slice(start,end));
	else
		res.send([]);
});


function getGroup(group){
	for (var i=0; i<groups.length; i++){
		if (groups[i].name==group) 
			return groups[i];
	}
	return null;
}
