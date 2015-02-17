//Define an angular module for our app
var amazeApp = angular.module('amazeApp', []);

//Define Routing for app
//Uri /AddNewOrder -> template AddOrder.html and Controller AddOrderController
//Uri /ShowOrders -> template ShowOrders.html and Controller AddOrderController
amazeApp.config(['$routeProvider',
    function($routeProvider) {
        $routeProvider.
            when('/message', {
                templateUrl: 'pages/amazePages/message.html',
                controller: 'MessageController'
            }).
            when('/notif', {
                templateUrl: 'pages/amazePages/notif.html',
                controller: 'NotifController'
            }).
            when('/task', {
                templateUrl: 'pages/amazePages/task.html',
                controller: 'TaskController'
            }).
            when('/profile', {
                templateUrl: 'pages/amazePages/profile.html',
                controller: 'ProfileController'
            }).
            when('/signout', {
                templateUrl: '/signout',
                controller: 'SignOutController'
            }).
            when('/search', {
                templateUrl: 'pages/amazePages/search.html',
                controller: 'SearchController'
            }).
            when('/dashboard', {
                templateUrl: 'pages/amazePages/dashboard.html',
                controller: 'DashboardController'
            }).
            when('/mailbox', {
                templateUrl: 'pages/amazePages/mailbox.html',
                controller: 'MailboxController'
            }).
            when('/calender', {
                templateUrl: 'pages/amazePages/calender.html',
                controller: 'CalenderController'
            }).
            when('/peoplefinder', {
                templateUrl: 'pages/amazePages/peoplefinder.html',
                controller: 'PeopleFinderController'
            }).
            when('/notes', {
                templateUrl: 'pages/amazePages/notes.html',
                controller: 'NotesController'
            }).
            when('/apps', {
                templateUrl: 'pages/amazePages/apps.html',
                controller: 'AppsController'
            }).
            when('/config', {
                templateUrl: 'pages/amazePages/config.html',
                controller: 'ConfigController'
            }).
            otherwise({
	           redirectTo: '/apps'
            });
}]);

amazeApp.controller('AmazeController', function($scope){
    $scope.messages = {};
    $scope.messages.count = 4;  //Put No if message count is 0
    $scope.messages.list = [
              { from : "Support", message: "Hello Good Morning", on: "15 Mints" }
             ,{ from : "Praneeth", message: "Nice Day", on: "15 Mints"  }
             ,{ from : "Tester"  , message: "Thanks in advance", on: "100 Mints"  }
             ,{ from : "All Ok"  , message: "Thanks in advance", on: "400 Mints"  }
             ];
    $scope.notif = {};
    $scope.notif.count = 7;
    $scope.notif.list = [
        { notif: "5 New Posts in Amaze Group", class: "ion ion-ios7-people info" },
        { notif: "Start Blogging today", class: "fa fa-warning danger" },
        { notif: "No yet confirmed", class: "fa fa-users warning" },
        { notif: "Start your tax planning today", class: "ion ion-ios7-cart succession" },
                        ];
    $scope.task = {};
    $scope.task.count = 7;
    $scope.task.list = [
        { name: "Pending task execution", complete: "12" },
        { name: "Complete the LLD", complete: "14" },
        { name: "Task Activity Rescheduling", complete: "99" },
        { name: "Personal Work", complete: "34" },
                        ];
    $scope.user = {};
    $scope.user.name = "Praneeth Ramesh";
    $scope.user.designation = "Lead At CTS";
    $scope.user.location = "PIC Bangalore";
    $scope.user.status = "Online";
    $scope.user.statusChange = function() {
        alert( $scope.user.status );
    }
    $scope.mail = {};
    $scope.mail.count = 121;
    $scope.calendar = {};
    $scope.calendar.count = 21;
});


amazeApp.controller('MessageController', function($scope) {
	
});

amazeApp.controller('NotifController', function($scope) {
	
});

amazeApp.controller('TaskController', function($scope) {
	
});

amazeApp.controller('ProfileController', function($scope) {
	alert("Profile");
});

amazeApp.controller('SignOutController', function($scope) {
	alert("Signout");
});

amazeApp.controller('SearchController', function($scope) {
	alert("Signout");
});

amazeApp.controller('DashboardController', function($scope) {
	alert("Dashboard");
});

amazeApp.controller('MailboxController', function($scope) {
	alert("Mailbox");
});

amazeApp.controller('CalenderController', function($scope) {
	alert("Calender");
});

amazeApp.controller('AppsController', function($scope) {
	$scope.apps = {};
    $scope.apps.list = [
        { class: "small-box bg-aqua", innerClass: "ion ion-bag", module: "Config", url: "/config", status: "V1.0.0" },
        { class: "small-box bg-green", innerClass: "ion ion-stats-bars", module: "Reports", url: "/reporting", status: "V1.0.0" },
        { class: "small-box bg-yellow", innerClass: "ion ion-person-add", module: "Admin", url: "/admin", status: "V1.0.0" },
        { class: "small-box bg-red", innerClass: "ion ion-pie-graph", module: "Plugin", url: "/plugin", status: "V1.0.0" }
    ];
        
    
    alert("Apps");
});

amazeApp.controller('PeopleFinderController', function($scope) {
	alert("PeopleFinder");
});

amazeApp.controller('NotesController', function($scope) {
	alert("Notes");
});

amazeApp.controller('ConfigController', function($scope) {
    alert("Config");
    
});


/*
function post(url, pmts, success, error){
    var httpPost = $http.post(url, pmts);
    httpPost.error = error;
    httpPost.success = success;
}


var httpGet = $http.get('/someUrl');
httpGet.success(function(data, status, headers, config) {
    // this callback will be called asynchronously
    // when the response is available
});
httpGet.error(function(data, status, headers, config) {
    // called asynchronously if an error occurs
    // or server returns response with an error status.
});


// Simple POST request example (passing data) :
var httpPost = $http.post('/someUrl', {msg:'hello word!'});
httpPost.success(function(data, status, headers, config) {
    // this callback will be called asynchronously
    // when the response is available
});
httpPost.error(function(data, status, headers, config) {
    // called asynchronously if an error occurs
    // or server returns response with an error status.
});


*/
