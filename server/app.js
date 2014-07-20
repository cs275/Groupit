var express = require('express'),
    app = express(),
    fs = require('fs');

var groups;
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
    	"category":category
    }
    groups.push(object);
    fs.writeFile("./models/groups.json", JSON.stringify(groups));

    console.log("Name: ", name);
    res.send("Hello " + name);
});
