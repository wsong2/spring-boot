var Client = require('node-rest-client').Client;
 
var client = new Client();

var args ={headers:{"Content-Type": "application/json"}};

var constants = require("./constants");
var myUrl = constants.HOST1 + "/chargingpoint/Id-0002";

client.delete(myUrl, args,
    function (data, response) {
		if (Buffer.isBuffer(data)) {
    		data = data.toString('utf8');
		}
        console.log(data);
   }
);
