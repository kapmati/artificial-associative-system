/**
 * @author Mateusz Kaproń
 * 25.03.17
 */
angular.module('aas').controller('mainPanelController', [
	'$scope', 'rest',
	function ($scope, rest) {

		$scope.wordsAfterChecking = null;
		$scope.text = 'To jest test';

		$scope.checkInputWords = function (text) {
			rest.checkWords(text, $scope.checkInputWordsSuccess);
		};

		$scope.checkInputWordsSuccess = function (data) {
			$scope.wordsAfterChecking = data;
		};

		$scope.checkInputWords($scope.text);
	}
]);