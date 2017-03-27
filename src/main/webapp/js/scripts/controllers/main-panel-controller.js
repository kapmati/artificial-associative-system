/**
 * @author Mateusz Kaproń
 * 25.03.17
 */
angular.module('aas').controller('mainPanelController', [
	'$scope', 'rest',
	function ($scope, rest) {

		var inputText = null;
		$scope.wordsAfterChecking = null;
		$scope.outputText = '';

		$scope.checkInputWords = function () {
			inputText = document.getElementById('inputText').innerHTML;
			if (inputText !== null && inputText !== "") {
				rest.checkWords(inputText, $scope.checkInputWordsSuccess);
			}
		};

		$scope.checkInputWordsSuccess = function (data) {
			$scope.wordsAfterChecking = data;
			var text = document.getElementById('inputText').innerHTML;
			var tooltipWords = '';
			for (var key in data) {
				if (data.hasOwnProperty(key)) {
					tooltipWords = '';
					data[key].forEach(function (singleValue) {
						tooltipWords = tooltipWords + '<input type="radio" value="test1">' + singleValue + '<br><br>';
					});
					var reg = new RegExp(' ' + key + ' ', "ig");
					text = text.replace(reg, ' ' +
						'<span class="hover" style="color:#ff6b00;">' + key +
						'<span class="tooltip">' +
						'<form action="">' +
						tooltipWords +
						'</form>' +
						'</span>' +
						'</span> ');
				}
			}
			document.getElementById("inputText").innerHTML = text;
		};

		$scope.nextWord = function () {
			inputText = document.getElementById('inputText').innerHTML;
			if (inputText !== null && inputText !== "") {
				rest.nextWord(inputText, $scope.nextWordSuccess);
			}
		};

		$scope.nextWordSuccess = function (data) {
			var words = '';
			for (var word in data.words) {
				words += word + '(' + data.words[word] + ')\n';
			}
			$scope.outputText = words;
		};

		$scope.breakExtending = function () {
			rest.breakExtending($scope.breakExtendingSuccess);
		};

		$scope.breakExtendingSuccess = function () {
			$scope.wordsAfterChecking = null;
			//TODO
		};

		$scope.textAnalysis = function () {
			inputText = document.getElementById('inputText').innerHTML;
			if (inputText !== null && inputText !== "") {
				rest.textAnalysis(inputText, $scope.textAnalysisSuccess);
			}
		};

		$scope.textAnalysisSuccess = function (data) {
			var output = '';
			var content = '';
			var similarWords = '';
			for (var i = 0; i < data.length; i++) {
				if (output === '') {
					output = data[0].input + '\n';
					content = data[0].notFound + '\n';
					similarWords = data[0].similarWords + '\n';
				} else {
					output += data[i].input + '\n';
					content += data[i].notFound + '\n';
					similarWords += data[i].similarWords + '\n';
				}
			}
			$scope.outputText = 'Input:\n' + output + '\nNot found:\n' + content +
				'\nSimilar words:\n' + similarWords;
		};

		//Checking words
		var waitingTime = 2500;
		var activityTimeout = setTimeout(inActive, waitingTime);
		var checkingNeeded = false;

		function inActive() {
			checkingNeeded = false;
			console.log('Send request!');

			//Send request and color wrong words
			$scope.checkInputWords();
		}

		function resetActive() {
			console.log('Reset');
			checkingNeeded = true;
			clearTimeout(activityTimeout);
			activityTimeout = setTimeout(inActive, waitingTime);
		}

		$(document).bind('keypress', function () {
			resetActive();
		});
	}
]);
