var Client = require('node-rest-client').Client;
var client = new Client();
 
var constants = require("./constants");
var myUrl = constants.HOST1 + "/chargingpoint/addnew";

//process.env["NODE_TLS_REJECT_UNAUTHORIZED"] = 0;
 
var args = {
    data: { 
		"charge-device-id":"Id-0003",
    	"reference":"SRC_LDN60399Z",
    	"latitude":49.531454,
		"longitude":0.19386 
	},
    headers: { "Content-Type": "application/json" }
};
 
client.post(myUrl, args, function (data, response) {
    if (Buffer.isBuffer(data)) {
    	data = data.toString('utf8');
    }
    console.log(data);
});
