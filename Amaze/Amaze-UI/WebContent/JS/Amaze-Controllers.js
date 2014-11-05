/**
 * 
 */

angular.module("myapp.hello", []).controller("HelloController",function($scope) {
	$scope.helloTo = {};
	$scope.helloTo.title = "Test";
});

angular.module("myapp.hello").controller("MyController", function($scope) {
	$scope.myData = {};
	$scope.myData.showIt = true;
});

angular.module("myapp.hello").controller("My1Controller", function($scope) {
  $scope.myData = {};
  $scope.myData.swit = 3;
});

angular.module("myapp", [ 'myapp.hello', 'myapp.my','myapp.my1' ]);