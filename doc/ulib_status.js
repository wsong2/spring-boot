if (WScript.FullName.indexOf('cscript.exe') < 0) {
	QuitWithError("Cscript required");
}
var vtOut = WScript.Stdout;
var args = WScript.arguments;
var fso = new ActiveXObject("Scripting.FileSystemObject");
var wshShell = new ActiveXObject("WScript.Shell");

var ivyLib = (args.length > 0) ? args(0) : 'C:\\MyProjects\\user-libraries\\spring_boot_ivy\\lib';
var xmlUserLib = ".\\export.userlibraries";

//
var dom = new ActiveXObject("MSXML2.DOMDocument.6.0");
dom.async = false;
dom.resolveExternals = false;
/*
dom.validateOnParse = false;
dom.setProperty("ProhibitDTD", false);
*/
dom.load(xmlUserLib);
if (dom.parseError.errorCode !== 0) {
	QuitWithError('[DOM] ' + dom.parseError.reason);
}

var mUserJarList = extractUserJarList(dom.documentElement);
var mIvyJarList = ivyJarList(ivyLib);
var mDiff = [];
var mUnmatched = [];
var mUnused = [];

for (var iU=0, iL=0; iU < mUserJarList.length && iL < mIvyJarList.length; ) {
	var cmp = dispatcher(mUserJarList[iU], mIvyJarList[iL]);
	if (cmp <= 0) iU++;
	if (cmp >= 0) iL++;
}
for (; iU < mUserJarList.length; iU++) {
	mUnmatched.push(mUserJarList[iU]);
}
for (; iL < mIvyJarList.length; iL++) {
	mUnused.push(mIvyJarList[iL]);
}

if (mDiff.length > 0) {
	PrintSection('To update');
	for (var i=0; i<mDiff.length; i++)
		vtOut.WriteLine(mDiff[i]);
}
PrintSection('Unmatched User jar');
for (var i=0; i<mUnmatched.length; i++) {
	vtOut.WriteLine(mUnmatched[i]);
}
PrintSection('Unused jar');
for (var i=0; i<mUnused.length; i++) {
	vtOut.WriteLine(mUnused[i]);
}

//
function extractUserJarList(docNode) {
	var arrUL = [];
	var nodeLibrary = docNode.childNodes.item(0);
	var nodeList = nodeLibrary.childNodes;
	for (var i=0, len=nodeList.length; i<len; i++) {
		var jar = nodeList.item(i).getAttribute('path');
		arrUL.push( fso.GetBaseName(jar) );
	}
	arrUL.sort(function(a,b) {
		var a1 = a.toLowerCase();
		var b1 = b.toLowerCase();
		return (a1 === b1) ? 0 : ((a1 < b1) ? -1 : 1);
	});
	return arrUL;
}

function ivyJarList(vLib) {
	var arrLib = [];
	var cmd = wshShell.Exec('cmd /c dir /b ' + vLib);
	var cmdOut = cmd.StdOut;
	while (!cmdOut.AtEndOfStream) {	
		var fn = cmdOut.ReadLine();
		arrLib.push( fso.GetBaseName(fn) );
	}
	return arrLib;
}

function dispatcher(s1, s2)
{
	function twoParts(fn) {
		var ix = fn.lastIndexOf('-');
		return [fn.substr(0,ix), fn.substr(ix+1)];
	}
	var a1 = twoParts(s1);
	var a2 = twoParts(s2);
	
	if (a1[0] === a2[0]) {
		if (a1[1] !== a2[1]) mDiff.push(a1[0] + ': ' + a1[1] + ' ~ ' + a2[1]);
		return 0;
	}
	if (a1[0] < a2[0]) {
		mUnmatched.push(s1);
		return -1;
	}
	mUnused.push(s2);
	return 1;
}

//
function PrintSection(vMsg) {
	var sp = '---------------------------------------------------------------';
	var len = vMsg.length + 6;
	vtOut.WriteLine(sp.substr(0,len));
	vtOut.Write('-- '); vtOut.Write(vMsg); vtOut.WriteLine(' --');
	vtOut.WriteLine(sp.substr(0,len));	
}

function QuitWithError(vMsg)
{
	WScript.Echo(vMsg);
	WScript.Quit(1);
}
