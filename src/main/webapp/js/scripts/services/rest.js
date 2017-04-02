angular.module('aas').service('rest', [
	'$http',
	function ($http) {
		var baseUrl = 'http://localhost:8080';

		var jsonGet = function (path, onSuccess) {
			$http({
				method: 'GET',
				url: baseUrl + path
			}).then(successHandler(onSuccess));
		};

		var jsonPost = function (path, text, onSuccess) {
			$http({
				method: 'POST',
				url: baseUrl + path,
				data: text
			}).then(successHandler(onSuccess));
		};

		this.createGraph = function (fileName, onSuccess) {
			jsonPost('/createGraph', fileName, onSuccess);
		};

		this.extendGraph = function (fileName, onSuccess) {
			jsonPost('/extendGraph', fileName, onSuccess);
		};

		this.textAnalysis = function (text, onSuccess) {
			jsonPost('/textAnalysis', text, onSuccess);
		};

		this.breakExtending = function (onSuccess) {
			jsonGet('/breakExtending', onSuccess);
		};

		this.finishWord = function (text, onSuccess) {
			jsonPost('/finishWord', text, onSuccess);
		};

		this.nextWord = function (text, onSuccess) {
			jsonPost('/nextWord', text, onSuccess);
		};

		this.checkWords = function (text, onSuccess) {
			jsonPost('/wordsChecking', text, onSuccess);
		};

		this.loadGraph = function (fileName, onSuccess) {
			jsonPost('/loadGraph', fileName, onSuccess);
		};

		var successHandler = function (onSuccess) {
			return function (result) {
				var data = result.data;
				if(onSuccess) {
					onSuccess(data);
				}
			}
		}
	}
]);