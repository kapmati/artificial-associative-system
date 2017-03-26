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

		$scope.breakExtending = function() {
			rest.breakExtending($scope.breakExtendingSuccess);
		};

		$scope.breakExtendingSuccess = function() {
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
		}
	}
]);