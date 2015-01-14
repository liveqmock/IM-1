var amazeApp = angular.module('amazeApp', []);

//amazeApp.config(['$routeProvider',
//    function($routeProvider) {
//        $routeProvider
//            .when('/test', {
//                templateUrl: 'pages/examples/500.html',
//                controller: 'AmazeController'
//            })
//            .when('/search/', {
//                templateUrl: 'pages/search.html',
//                controller: 'SearchController'
//            })
//            .when('/detail/', {
//                templateUrl: 'pages/detail.html',
//                controller: 'DetailController'
//            })
//            .otherwise({
//                redirectTo: '/login'
//            });
//    }
//]);

amazeApp.controller('AmazeController', function($scope){
    $scope.messages.count = 4;
});


amazeApp.controller('DetailController', function($scope){
    console.log("test");
    alert("test");
});

amazeApp.controller('SearchController', function($scope){
    console.log("test");
    alert("test");
});