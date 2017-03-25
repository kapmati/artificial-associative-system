angular.module('aas').service('rest', [
	'$http',
	function ($http) {
		var baseUrl = 'http://localhost:8080';

		var jsonPost = function (path, text, onSuccess) {
			$http({
				method: 'POST',
				url: baseUrl + path,
				data: text

			}).then(successHandler(onSuccess));
		};

		this.checkWords = function (text, onSuccess) {
			jsonPost('/wordsChecking', text, onSuccess);
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