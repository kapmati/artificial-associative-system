function wordsChecking(text) {
	var xhr = new XMLHttpRequest();
	xhr.open('POST', "http://localhost:8080/wordsChecking", true);
	xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
	xhr.send('inputText=' + text);
	var response = null;
	xhr.onreadystatechange = function () {
		if (xhr.readyState == 4) {
			if (xhr.status == 200) {
				response = xhr.responseText;
			}
		}
		if (response != null) {
			var responseJSON = JSON.parse(response);
			return responseJSON;
		}
	}
}