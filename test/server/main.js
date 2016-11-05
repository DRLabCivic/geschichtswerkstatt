/*
* @Author: Lutz Reiter, Design Research Lab, Universität der Künste Berlin
* @Date:   2016-11-04 13:53:08
* @Last Modified by:   lutzer
* @Last Modified time: 2016-11-04 14:52:17
*/

'use strict';

/*Define dependencies.*/

var express = require('express');
var app = express();
var http = require('http').Server(app);

var config = {
	port : 8081,
	hostname: '192.168.1.5',
	uploadDir: '/tmp'
}

/* Setup file upload */
var multipart = require('connect-multiparty');
var fileUploader = multipart({
    uploadDir: config.uploadDir,
    autoFiles: true
    //maxFilesSize: Config.maxUploadFileSize
});


/* Setup Routes */
var router = express.Router();
router.get('/upload', function(req,res) {
	console.log('GET');
    res.send('GET: Hello There');
});
router.post('/upload', fileUploader, function(req,res) {
	console.log('POST');
	console.log(req.body);
	console.log(req.files);
    res.send('POST: Hello There');
});
app.use(router);

/* Error Handling */
app.use(function(err, req, res, next) {
    res.status(err.status || 500);
});

/* Run the server */

http.listen(config.port,config.hostname,function(){
    console.log("info","Test Server listening on "+config.hostname+":"+config.port);
});