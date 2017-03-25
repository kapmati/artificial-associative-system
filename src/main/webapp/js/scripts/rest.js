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
			// var output = null;
			// var content = null;
			// var similarWords = null;
			// console.log(responseJSON);
			// for (var i = 0; i < responseJSON.length; i++) {
			// 	if (output == null) {
			// 		output = responseJSON[0].input + '\n';
			// 		content = responseJSON[0].notFound + '\n';
			// 		similarWords = responseJSON[0].similarWords + '\n';
			// 	} else {
			// 		output += responseJSON[i].input + '\n';
			// 		content += responseJSON[i].notFound + '\n';
			// 		similarWords += responseJSON[i].similarWords + '\n';
			// 	}
			// }
			// document.getElementById("output").innerHTML = 'Input:\n' + output + '\nNot found:\n' + content +
			// 	'\nSimilar words:\n' + similarWords;
		}
	}
}