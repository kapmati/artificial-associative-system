function execute(text) {
			var xhr = new XMLHttpRequest();
			xhr.open('POST', "http://localhost:8080/textAnalysis", true);
			xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
			xhr.send('inputText=' + text);
			var response = null;
			xhr.onreadystatechange = function() {
				if (xhr.readyState == 4)
  					if (xhr.status == 200)
    					response = xhr.responseText;
    					if (response != null) {
    						var responseJSON = JSON.parse(response);
    						var output = null;
    						var content = null;
    						var bestNeighbour = null;
    						console.log(responseJSON);
    						for (var i = 0; i < responseJSON.length; i++) {
    							if (output == null) {
    								output = responseJSON[0].input + '\n';
    								content = responseJSON[0].notFound + '\n';
    								bestNeighbour = responseJSON[0].bestNeighbour + '\n';
    							} else {
    								output += responseJSON[i].input + '\n';
    								content += responseJSON[i].notFound + '\n';
    								bestNeighbour += responseJSON[i].bestNeighbour + '\n';
    							}
							}
							document.getElementById("output").innerHTML = 'Input:\n' + output + '\nNot found:\n' + content +
								'\nBest neighbours:\n' + bestNeighbour;
    					}
			}

		}

//function extend() {
//    var response = null;
//    while(response != 100) {
//        var xhr = new XMLHttpRequest();
//        xhr.open('GET', "http://localhost:8080/extend", true);
//        xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
//        xhr.send();
//    	response = null;
//        xhr.onreadystatechange = function() {
//        	if (xhr.readyState == 4) {
//        	    if (xhr.status == 200) {
//        	        response = xhr.responseText;
//        	        if (response != null) {
//        	            document.getElementById("processBar").innerHTML = response;
//        	        }
//        	    }
//        	}
//        }
//
//    }
//}

function breakExtending() {
        var xhr = new XMLHttpRequest();
        xhr.open('GET', "http://localhost:8080/breakExtending", true);
        xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
        xhr.send();
}

function extend() {
        var xhr = new XMLHttpRequest();
        xhr.open('GET', "http://localhost:8080/extend", true);
        xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
        xhr.send();
}