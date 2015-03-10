var amazeApp = angular.module('amazeApp', []);

amazeApp.controller('LoginController', function($scope, $http) {
	$scope.login = {};
	$scope.login.accessClient = "Chhrome";
	$scope.login.loginF = function() {
		var data = {
			name : $scope.login.name,
			password : $scope.login.password,
			accessClient : $scope.login.accessClient,
			remember : $scope.login.remember
		};

		$.post("AmazeRest/login", data, function(data, status) {
			if( data.message == "Login Validated" ){
				$(location).attr('href','index.html');
			}
		});

	}
});