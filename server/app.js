var express = require('express'),
    app = express(),
    fs = require('fs');

var groups, users;
try{
	groups = JSON.parse(fs.readFileSync("./models/groups.json"));
}catch(e){
	groups = [];
}

app.listen(3000);
console.log('Listening on port 3000');

app.use(express.static(__dirname + '/public'));

var bodyParser = require('body-parser')
app.use( bodyParser.json() );       // to support JSON-encoded bodies
app.use( bodyParser.urlencoded() ); // to support URL-encoded bodies
 
app.get('/allGroups/', function(req, res) {
	res.send(groups);
});

app.get('/allGroupNames/', function(req, res){
	var allNames = [];
	for (var i=0; i<groups.length; i++){
		allNames.push(groups[i].name);
	}
	if (allNames.length==0)
		res.send("null");
	else
		res.send(allNames);
});

app.get('/getGroupsForUser/', function(req, res){
	var user = req.query.user,
		memberOf = [];
	for (var i=0; i<groups.length; i++){
		if (groups[i].members.indexOf(user) != -1){
			memberOf.push(groups[i]);
		}
	}
	if (memberOf.length==0)
		res.send("null");
	else
		res.send(memberOf);
});

app.get('/getMembers/', function(req, res){
	var group = req.query.group;
	res.send(getGroup(group)["members"]);
});

app.get("/search/", function(req, res){
	var query = req.query.q;
	var matches = [];
	for (var i=0; i<groups.length; i++){
		if (groups[i].name.indexOf(query)!=-1) matches.push(groups[i].name);
	}
	if (matches.length == 0)
		res.send("null");
	else
		res.send(matches);
});	

app.get('/joinGroup/', function(req, res){
	var user = req.query.user,
		group = req.query.group;
	for (var i=0; i<groups.length; i++){
		if (groups[i].name == group){
			groups[i].members.push(user);
			fs.writeFile("./models/groups.json");
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

function getGroup(group){
	for (var i=0; i<groups.length; i++){
		if (groups[i].name==group) 
			return groups[i];
	}
	return null;
}
