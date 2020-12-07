const cArrGridViewRows = [{
	"itemId":1,
	"itemName":"Asus ZenBook ux325",
	"itemDate":"2020-12-26",
	"descr":"ux325 16GB 512GB",
	"value1":1,"value2":900.0,
	"more":"Intel Optane. No Mic, Bluetooth",
	"dttm":"2021-01-09T22:19:27"
	},{
	"itemId":2,
	"itemName":"Monitor HP",
	"itemDate":"2020-12-29",
	"descr":"24fw",
	"value1":1,"value2":119.0,
	"more":"with Audio",
	"dttm":"2020-12-31T13:50:20"
	}
];

const idRadioGroup = 'rsel';

var mGridViewRows = [];

const refreshGridView = () => { rePopulateTBody('idGridView', mGridViewRows); }

function formToRec(formElement, rec)
{	
	let elements = formElement.elements;
	for (let i=0; i<elements.length; i++) {
		let elt = elements[i];
		rec[elt.name] = elt.value;
	}
}

function formCheckedRec(formElement, rec)
{	
	if (document.getElementById("ck1").checked) {
		rec.itemName = formElement.itemName.value;
	}
	if (document.getElementById("ck2").checked) {
		rec.itemDate = formElement.itemDate.value;
	}
	if (document.getElementById("ck3").checked) {
		rec.descr = formElement.descr.value;
	}
	if (document.getElementById("ck4").checked) {
		rec.value1 = formElement.value1.value;
		rec.value2 = formElement.value2.value;
	}
	if (document.getElementById("ck5").checked) {
		rec.more = formElement.more.value;
	}
}
  
function syncRow(form1, data)
{
	if (data.status !== 'OK')	return;
	let itemId = parseInt(data.itemId);
	let rec = mGridViewRows.find(r => r.itemId === itemId);
	formToRec(form1, rec);
	refreshGridView();
}

function addViewRow(form1, data, rec)
{
	if (data.status !== 'OK')	return;
	let itemId = parseInt(data.itemId);
	if (itemId > 0) {
		rec.itemId = itemId;
		rec.dttm = new Date().toISOString().substr(0,23);	// ATTN
		mGridViewRows.push(rec);
		refreshGridView();
	}
}

//
function createInput(inputType, inputName) {
	let inp = document.createElement('input');
	inp.type = inputType;
	inp.name = inputName;
	return inp;
}

function selectedRowRec() {
	function groupValue() {
		let radios = document.getElementsByName(idRadioGroup);
		for (let i = 0; i < radios.length; i++ ) {
			if( radios[i].checked ) return radios[i].value;
		}
		return '-1';
	}
	let val = groupValue();
	let iRow = parseInt(val);
	return (iRow >= 0) ? mGridViewRows[iRow] : null;
}

function pullSelectID()
{
	let rec = selectedRowRec();
	if (rec != null) {
		document.getElementById("form1").itemId.value = rec.itemId;
	}
}

function pullSelectRow()
{
	let rec = selectedRowRec();
	if (rec == null) return;
	let elements = document.getElementById("form1").elements;
	for (let i=0; i<elements.length; i++) {
		let elt = elements[i];
		elt.value = rec[elt.name];
	}
}

function addTBodyRows(mTBody, arrRows)
{
	function addCell(row, idx, txt) {
		let newCell = row.insertCell(idx);
		let newText = document.createTextNode(txt);
		newCell.appendChild(newText);
	}
	
	mGridViewRows = (arrRows.length === 0) ? cArrGridViewRows : arrRows;
	
	for (let i=0; i<mGridViewRows.length; i++) {
		let rdata = mGridViewRows[i];
		let newRow = mTBody.insertRow(i);
	
		let cell0 = newRow.insertCell(0);
		let radiobox = createInput('radio', idRadioGroup);
		radiobox.value = '' + i;	// for parseInt
		cell0.appendChild(radiobox);
	
		addCell(newRow, 1, rdata.itemId);
		addCell(newRow, 2, rdata.itemName);
		addCell(newRow, 3, rdata.itemDate);
		addCell(newRow, 4, rdata.descr);
		addCell(newRow, 5, rdata.value1);
		addCell(newRow, 6, rdata.value2);
		addCell(newRow, 7, rdata.more);
		addCell(newRow, 8, rdata.dttm);		
	}
}

function rePopulateTBody(idTBody, rows) {
	let old_tbody = document.getElementById(idTBody);
	let new_tbody = document.createElement('tbody');
	addTBodyRows(new_tbody, rows);
	old_tbody.parentNode.replaceChild(new_tbody, old_tbody);
	new_tbody.id = idTBody;
	//setRangeText();
}

function doLoad(idTBody, btn) {
	btn.disabled = true;
	fetch("/item/all").then(
		(response) => response.json()
	).then((data) => {
		//console.log(data);
		rePopulateTBody(idTBody, data);
		btn.disabled = false;
	}).catch((err) => {
		console.log(err);
		btn.disabled = false;
	});
}

function doAdd(formElement, btn)
{
	let rec = {};
	let headerRec = {'Content-Type': 'application/json'};
	if (formElement.itemId.value == '') {
		btn.disabled = true;
		formToRec(formElement, rec);
		let jdata = JSON.stringify(rec);
		
		fetch('/item/addnew', {
			method: "post",
			body: jdata,
			headers: headerRec
		}).then(response => response.json()
		).then(data => {
			console.log(data);
			addViewRow(formElement, data, rec);
			btn.disabled = false;
		}).catch(err => {
			btn.disabled = false;
			console.log(err);
		});
	} else {
		formCheckedRec(formElement, rec);		
		if (Object.values(rec).length < 1)	return;
		rec.itemId = formElement.itemId.value;
		btn.disabled = true;
		let jdata = JSON.stringify(rec);
		
		fetch('/item/update', {
			method: "post",
			body: jdata,
			headers: headerRec
		}).then(response => response.json()
		).then(data => {
			console.log(data);
			syncRow(formElement, data);
			btn.disabled = false;
		}).catch(err => {
			btn.disabled = false;
			console.log(err);
		});
	}	
}

function deleteRow(data)
{
	if (data.status !== 'OK')	return;
	let itemId = parseInt(data.itemId);
	let idx = (isNaN(itemId) || itemId < 10) ? -1 : mGridViewRows.findIndex(r => r.itemId === itemId);
	if (idx >= 0) {
		mGridViewRows.splice(idx, 1);
		refreshGridView();
	}
}

function doDelete(btn) {
	let rec = selectedRowRec();
	if (rec === null) return;
	
	btn.disabled = true;	
	const url = '/item/del/' + rec.itemId;
	fetch(url, {method: 'DELETE'}
	).then(response => response.json()
	).then(data => {
		btn.disabled = false;
		console.log(data);
		deleteRow(data);
	}).catch(err => {
		btn.disabled = false;
		console.log(err);
	});

}

function doFetch(form, btn) {
	btn.disabled = true;	
	fetch('/item/form', {
		method: 'POST',
		body: new FormData(form)
	}).then(response => response.text()
	).then(text => {
		//console.log(text); 
		let json = parseMultiPart(text);
		printJson(json);
		btn.disabled = false;
	}).catch(err => {
		console.log(err);
		btn.disabled = false;
	});
}

function parseMultiPart(bodyText) {
	const RE = /Content-Length: (\d+)/;
	let lines = bodyText.split(/[\r\n]+/);
	let buffer = '';
	for (let i=0, textSize= -1, len=lines.length; i<len; i++) {
		let line = lines[i];
		//console.log('#' + i + ': ' + line);
		if (textSize > 0) {
			buffer += line;
			if (buffer.length >= textSize)
				return buffer.substring(0,textSize);
		} else {
			let arr = RE.exec(line);
			if (arr) 
				textSize = parseInt(arr[1]);
		}
	}
	return buffer;
}

function printJson(json) {
	let obj = JSON.parse(json);
	let txt = '';
	for (let [key, value] of Object.entries(obj)) {
		txt += (key + ': ' + value + '\r\n');
	}
	document.getElementById("ta1").value = txt;
}