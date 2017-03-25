function execute(text) {
	var xhr = new XMLHttpRequest();
	xhr.open('POST', "http://localhost:8080/textAnalysis", true);
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
			var output = null;
			var content = null;
			var similarWords = null;
			console.log(responseJSON);
			for (var i = 0; i < responseJSON.length; i++) {
				if (output == null) {
					output = responseJSON[0].input + '\n';
					content = responseJSON[0].notFound + '\n';
					similarWords = responseJSON[0].similarWords + '\n';
				} else {
					output += responseJSON[i].input + '\n';
					content += responseJSON[i].notFound + '\n';
					similarWords += responseJSON[i].similarWords + '\n';
				}
			}
			document.getElementById("output").innerHTML = 'Input:\n' + output + '\nNot found:\n' + content +
				'\nSimilar words:\n' + similarWords;
		}
	}

}

function nextWord(text) {
	var xhr = new XMLHttpRequest();
	xhr.open('POST', "http://localhost:8080/nextWord", true);
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
			var words = "";
			console.log(responseJSON);
			debugger;
			for (var word in responseJSON.words) {
				words += word + '(' + responseJSON.words[word] + ')\n';
			}
		}
		document.getElementById("output").innerHTML = 'Words:\n' + words;
	}
}

function breakExtending() {
	var xhr = new XMLHttpRequest();
	xhr.open('GET', "http://localhost:8080/breakExtending", true);
	xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
	xhr.send();
}