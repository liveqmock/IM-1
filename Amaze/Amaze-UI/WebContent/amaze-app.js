var amazeApp = angular.module('amazeApp', []);


amazeApp.controller('LoginController',function($scope, $http){
    $scope.login = {};
    $scope.login.accessClient = "Chhrome";
    $scope.login.loginF = function(){
        var data = {
            name : $scope.login.name,
            password : $scope.login.password,
            accessClient : $scope.login.accessClient,
            remember: $scope.login.remember
        };
        var loginRest = $http.get( 'http://localhost:8080/Amaze-Rest/AmazeRest/login', data );
        loginRest.success(function(){
            alert( "Failure : " + e );
        });
        loginRest.error(function(){
            alert( "Failure : " + e );
        });
    }
});