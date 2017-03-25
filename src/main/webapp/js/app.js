/**
 * @author Mateusz Kaproń
 * 25.03.17
 */
angular.module('aas', ['ngRoute']).config(['$routeProvider', function ($routeProvider) {
	$routeProvider
		.when('/', {
			templateUrl: 'html/main-panel.html'
		})
		.otherwise({
			redirectTo: 'html/main-panel.html'
		});
}]);