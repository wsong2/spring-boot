//const https = require('https')

//var constants = require("./constants");
//var myUrl = constants.HOST1 + "/item/addnew";

const https = require('http')

const data = JSON.stringify({
    "id":24,
    "miDescr":"ux325 16GB 512GB",
	"miValue2":900.0,
	"miMore":"Intel Optane. No Mic, Bluetooth",
	"miDate":"2020-12-26"
})

const options = {
  hostname: 'localhost',
  port: 8080,
  path: '/item/update',
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Content-Length': data.length
  }
}

const req = https.request(options, (res) => {
  console.log(`statusCode: ${res.statusCode}`)

  res.on('data', (d) => {
    process.stdout.write(d)
  })
})

req.on('error', (error) => {
  console.error(error)
})

req.write(data)
req.end()
