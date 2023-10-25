var tabLinks = [];
var contentDivs = [];

// Assign onclick events to the tab links, and highlight the tab iStartIdx
var iStartIdx = 0;

const setStartIdx = (vIdx) => { iStartIdx = vIdx; }

function init()
{
	const getHash = (url) => url.substring(url.lastIndexOf('#') + 1);

	// Grab the tab links and content divs from the page
	let tabListItems = document.getElementById('tabs').childNodes;
	for (let i = 0; i < tabListItems.length; i++ ) {
        if (tabListItems[i].nodeName == "LI") {
			let tabLink = getFirstChildWithTagName( tabListItems[i], 'A' );
			let id = getHash( tabLink.getAttribute('href') );
			tabLinks[id] = tabLink;
			contentDivs[id] = document.getElementById( id );
        }
	}
	
	let k = 0;
	for (let id in tabLinks ) {
        tabLinks[id].onclick = showTab;
        tabLinks[id].onfocus = () => this.blur();
        if ( k == iStartIdx ) tabLinks[id].className = 'selected';
        k++;
	}

	// Hide all content divs except the iStartIdx
	let n = 0;
	for (let id in contentDivs ) {
		if ( n != iStartIdx ) contentDivs[id].className = 'tabContent hide';
		n++;
    }
	
	//
	function showTab() {
		let selectedId = getHash( this.getAttribute('href') );

		for (let id in contentDivs ) {
			tabLinks[id].className = (id == selectedId) ? 'selected' : '';
			contentDivs[id].className = (id == selectedId) ? 'tabContent' : 'tabContent hide';
		}

		// Stop the browser following the link !!!
		return false;
	}

	function getFirstChildWithTagName( element, tagName ) {
		for (let i = 0; i < element.childNodes.length; i++ ) {
			if ( element.childNodes[i].nodeName == tagName )
				return element.childNodes[i];
		}
	}
}
