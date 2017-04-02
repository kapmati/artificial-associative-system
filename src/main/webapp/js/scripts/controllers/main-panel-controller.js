/**
 * @author Mateusz Kaproń
 * 25.03.17
 */
angular.module('aas').controller('mainPanelController', [
	'$scope', 'rest',
	function ($scope, rest) {

		var fileName = 'knowledgeSource.ser';
		var inputText = null;
		$scope.isGraphLoadedSuccessfully = false;
		$scope.wordsAfterChecking = null;
		$scope.outputText = '';
		var inputFileName = false;

		$scope.createGraph = function () {
			inputFileName = document.getElementById('inputFile').files[0].name;
			if (inputFileName !== null && inputFileName !== "") {
				rest.createGraph(inputFileName, $scope.createGraphSuccess);
			}
		};

		$scope.createGraphSuccess = function () {
			//TODO
		};

		$scope.extendGraph = function () {
			inputFileName = document.getElementById('inputFile').files[0].name;
			if (inputFileName !== null && inputFileName !== "") {
				rest.extendGraph(inputFileName, $scope.extendGraphSuccess);
			}
		};

		$scope.extendGraphSuccess = function () {
			//TODO
		};

		$scope.clearInput = function () {
			document.getElementById('inputText').innerHTML = '';
		};

		$scope.checkInputWords = function () {
			inputText = document.getElementById('inputText').innerHTML;
			if (inputText !== null && inputText !== "") {
				rest.checkWords(inputText, $scope.checkInputWordsSuccess);
			}
		};

		$scope.checkInputWordsSuccess = function (data) {
			$scope.wordsAfterChecking = data;
			var text = document.getElementById('inputText').innerHTML;
			//TODO tymczasowo string jest rozszerzany aby korektor uwzględniał wszystie słowa
			text = "A " + text + " Z";
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
			text = text.substring(2, text.length - 2);
			document.getElementById("inputText").innerHTML = text;
		};

		$scope.finishWord = function () {
			inputText = document.getElementById('inputText').innerHTML;
			if (inputText !== null && inputText !== "") {
				rest.finishWord(inputText, $scope.finishWordSuccess);
			}
		};

		$scope.finishWordSuccess = function (data) {
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

		// $scope.isGraphLoaded = function () {
		// 	if (isGraphLoadedSuccessfully) {
		//
		// 	}
		// 	return false;
		// };

		//Checking words
		var waitingTime = 2500;
		var activityTimeout = setTimeout(inActive, waitingTime);

		function inActive() {
			console.log('Send request!');

			//Send request and color wrong words
			// $scope.checkInputWords();
		}

		function resetActive() {
			console.log('Reset');
			clearTimeout(activityTimeout);
			activityTimeout = setTimeout(inActive, waitingTime);
		}

		$scope.init = function (fileName) {
			rest.loadGraph(fileName, $scope.initSuccess);
		};

		$scope.initSuccess = function () {
			//TODO -> wyświetlić info o poprawnym wczytaniu grafu
			$scope.isGraphLoadedSuccessfully = true;
		};

		$scope.init(fileName);

		// $(document).bind('keypress', function () {
		// 	resetActive();
		// });
		$(function () {
			$('.toggle-sidebar').click(function () {
				toggleSideBar();
			});
		});

		function toggleSideBar() {

			if ($('#page-wrapper').hasClass('show-sidebar')) {
				// Do things on Nav Close
				$('#page-wrapper').removeClass('show-sidebar');
			} else {
				// Do things on Nav Open
				$('#page-wrapper').addClass('show-sidebar');
			}
			//$('#site-wrapper').toggleClass('show-nav');
		}
	}
]);
