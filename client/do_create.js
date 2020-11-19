var Client = require('node-rest-client').Client;
 
var client = new Client();
 
var constants = require("./constants");
var myUrl = constants.HOST1 + "/chargingpoint/addnew";

var args = {
    data: { 
		"charge-device-id":"Id-0002",
    	"reference":"SRC_LDN30188Z",
    	"latitude":50.531454,
		"longitude":-0.17386 
	},
    headers: { "Content-Type": "application/json" }
};
 
client.post(myUrl, args, function (data, response) {
    if (Buffer.isBuffer(data)) {
    	data = data.toString('utf8');
    }
    console.log(data);
});
