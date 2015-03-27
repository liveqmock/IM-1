var amazeApp = angular.module('amazeApp', []);

amazeApp.controller('LoginController', function($scope, $http) {
	
	var position;
	if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function(position){
        	position.latitude = position.coords.latitude;
        	position.longitude = position.coords.longitude;
        }, function(error){
	        	switch(error.code) {
		            case error.PERMISSION_DENIED:
		            	position.error = "User denied the request for Geolocation."
		                break;
		            case error.POSITION_UNAVAILABLE:
		            	position.error = "Location information is unavailable."
		                break;
		            case error.TIMEOUT:
		            	position.error = "The request to get user location timed out."
		                break;
		            case error.UNKNOWN_ERROR:
		            	position.error = "An unknown error occurred."
		                break;
	        	}
        });
    }
	
	var accessClient = {};
	accessClient.ClientCode = navigator.appCodeName;
	accessClient.ClientName = navigator.appName;
	accessClient.ClientVersion = navigator.appVersion;
	accessClient.CookiesEnabled = navigator.cookieEnabled;
	accessClient.ClientLang = navigator.language;
	accessClient.ClientPlatform = navigator.platform;
	accessClient.UserAgentHeader = navigator.userAgent;
	accessClient.Position = position;

	if(typeof(Storage) !== "undefined") {
		if(!localStorage.getItem("amaze.loggedUser.isNew")) {
			$scope.newUser = false;
			$scope.newUserName = localStorage.getItem("amaze.loggedUser.userName");
		}
	}
	$scope.newUser = true;
	
	$scope.login = {};
	$scope.login.accessClient = JSON.stringify(accessClient);
	$scope.diffUser = new function(){
		$scope.newUser = true;
	}
	$scope.login.loginF = function() {
		var data = {
			name : $scope.login.name,
			password : $scope.login.password,
			accessClient : $scope.login.accessClient,
			remember : $scope.login.remember
		};
		localStorage.setItem("amaze.loggedUser.userName", name);
		localStorage.setItem("amaze.loggedUser.isNew", false);
		$.post("AmazeRest/login", data, function(data, status) {
			alert(JSON.stringify(data));
			if( data.error !== true ){
				$(location).attr('href','index.html');
			}
			else{
				$scope.errorMsg = data.errorMessage;
			}
		});

	}
});