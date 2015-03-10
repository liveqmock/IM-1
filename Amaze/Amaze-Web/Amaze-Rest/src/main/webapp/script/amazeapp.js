//Define an angular module for our app
var amazeApp = angular.module('amazeApp', ['ngRoute']);

amazeApp.directive('a', function() {
    return {
        restrict: 'E',
        link: function(scope, elem, attrs) {
            if(attrs.ngClick || attrs.href === '' || attrs.href === '#'){
                elem.on('click', function(e){
                    e.preventDefault();
                });
            }
        }
   };
});

amazeApp.filter('range', function() {
    return function(input) {
        var lowBound, highBound;
        switch (input.length) {
        case 1:
            lowBound = 0;
            highBound = parseInt(input[0]) - 1;
            break;
        case 2:
            lowBound = parseInt(input[0]);
            highBound = parseInt(input[1]);
            break;
        default:
            return input;
        }
        var result = [];
        for (var i = lowBound; i <= highBound; i++)
            result.push(i);
        return result;
    };
});

//Define Routing for app
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
	        when('/searchScreen', {
	            templateUrl: 'pages/amazePages/searchScreen.html',
	            controller: 'SearchScreenController'
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
        { class: "small-box bg-aqua", innerClass: "ion ion-bag", module: "Config", url: "/searchScreen", status: "V1.0.0" },
        { class: "small-box bg-green", innerClass: "ion ion-stats-bars", module: "Reports", url: "/reporting", status: "V1.0.0" },
        { class: "small-box bg-yellow", innerClass: "ion ion-person-add", module: "Admin", url: "/admin", status: "V1.0.0" },
        { class: "small-box bg-red", innerClass: "ion ion-pie-graph", module: "Plugin", url: "/plugin", status: "V1.0.0" }
    ];
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

amazeApp.controller('SearchScreenController', function($scope) {
	$scope.screenNameBase = 'AmazeRest/searchScreen/';
	$.get("AmazeRest/searchScreen/search", function( data ) {
		$('#screen').hide();
		$scope.modules = data;
	    $scope.$apply();
	});
	$scope.doSearch = function( $event ){
		$.get( $event.target, function( data ) {
			$scope.screen = {};
			$scope.screen.name = data.screenName;
			$scope.screen.searchButtons = data.searchButtons;
			$scope.screen.filterModel = data.filterModel.filters;
			$scope.screen.actionModel = data.actionModel.actionGroupModels;
			$scope.screen.columnModel = data.columnModel.columns;
			$scope.screen.columnModel.length = data.columnModel.columns.length;
			$scope.screen.limit = 50;
			$scope.screen.offset = 0;
			$scope.$apply();
			$('#screen').show();
			var dataUrl = $scope.screenNameBase + $scope.screen.name + '/data';
			var params =  { "limit": $scope.screen.limit , "offset": $scope.screen.offset };
			pageData( dataUrl, params);
			$('input[name=allSlctChck]').on('click', function(){
				var state = $( this ).prop('checked');
				$('input[name=slctChck]').each( function(){
					$(this).prop('checked',state);
				});
			});
			$('a#headSort').on('click',function(){
				var order = $(this).attr("class") == 'caret' ? 'asc' : 'desc' ;
				var params =  { "limit": $scope.screen.limit , "offset": $scope.screen.offset, "sort": $( this ).attr('name'), "order": order };
				pageData( dataUrl, params);
				if( $(this).attr("class") == 'caret' )
					$( this ).attr("class", 'caret caret-upside' );
				else
					$( this ).attr("class", 'caret' );
			});
			$('#rppList li').on('click', function(){
				$scope.screen.limit = $(this).text();
				$scope.$apply();
				var params =  { "limit": $scope.screen.limit , "offset": $scope.screen.offset };
				pageData( dataUrl, params );
				
			});
			$('#refresh').on('click', function(){
				var filterParams = '';
				$('#queryFilters table tr td input').each( function(){
					if($(this).val() != null && $(this).val().length > 0 )
						filterParams += 'upper(' + $(this).attr('id') + ') like ' + ' upper(\'%' +$(this).val() + '%\') and ';
				});
				if( filterParams.length > 0 )
					filterParams = filterParams.substring(0, filterParams.length - 4);

				var params =  { "limit": $scope.screen.limit , "offset": $scope.screen.offset, "filterParams" : filterParams };
				pageData( dataUrl, params);
			});
			$('#colSuppresser li div input[type=checkbox]').on('click', function(){
				$('#dataTable1 tr th[name="' + $(this).attr("name") + '"]').toggle();
				$('#dataTable1 tr td[value=' + $(this).attr("value")  + ']').toggle();
			});
		});
	}
	$scope.doAction = function( $event ){
		var actionUrl = $scope.screenNameBase + $scope.screen.name + '/action/' + $event.target;
		$.get($event.target, function(data){
			$scope.detail = {};
			$scope.detail.detailName = data.screenName;
			$scope.detail.entityName = data.entityName;
			$scope.detail.entity = data.entity;
			$scope.detail.detailButtons = data.detailButtons;
			$scope.detail.widgets = data.widgets;
			$scope.detail.widgetsLen = data.widgets.length;
			$scope.$apply();
			$('#detailModalContent table tr td input[data-change=widgets] ').on('change', function(){
				var value = $(this).val();
				var bindName = $(this).attr('id');
				var jsonObj = $scope.detail.entity;
				jsonObj[bindName] = value;
				$scope.detail.entity = jsonObj;
			});
			for( var i = 0; i < data.widgets.length ; i++ ){
				$(this).renderComponent( data.widgets[i].widgetName, data.widgets[i].widgetType, data.widgets[i].widgetOptions, i );
			}
			$('#detailModal').modal('show');
			
		});
	}
	
	function pageData( dataUrl, params ){
		$.get( dataUrl, params, function( data ) {
			$scope.screen.data = data;
			$scope.screen.data.empty = data.length == 0 ? true : false;
			if( $scope.screen.datalen == null ){
				$scope.screen.datalen = data.length;
			}
			$scope.screen.data.pages = Math.round( $scope.screen.datalen / $scope.screen.limit ) + 1;
			var pageNos = $scope.screen.data.pages;
			var pageList = new Array();
			for( var i = 1 ; i <= pageNos ; i++ ){
				pageList[i - 1] = i;
			}
			$scope.screen.data.pageList = pageList;
			$scope.screen.data.currPage = 1;
			$scope.$apply();
			$('#pagination li').on('click', function(){
				$scope.screen.offset = $(this).text() - 1;
				$scope.$apply();
				var params =  { "limit": $scope.screen.limit , "offset": $scope.screen.offset };
				pageData( dataUrl, params );
			});
			$('#dataTable1 tr td#dataTd').not('input[type=checkbox]').on('click', function(){
				var state = $( this ).parent().find('input[type=checkbox]').prop('checked');
				$( this ).parent().find('td input[type=checkbox]').prop('checked', !state);
				if( state )
					$('input[name=allSlctChck]').prop( 'checked', false );
			});
		});
	}
	
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

// JQuery plugin to render the Widgets

(function ( $ ) {
	$.fn.renderComponent = function( name, type, condition, index ) {
		switch(type) {
		    case "TextBox": {
		    		this.append('<label>'+ name + ' : </label>' + 
		    						'<input type="text" class="form-control" placeholder="' + condition.placeHolder + '" + name="' + condition.property + '" id="textbox" value="" >'
		    					);
		        }
		        break;
		    case "TextArea": {
		    		
		        }
		        break;
		    case "Integer": {
	    		
		        }
		        break;
		    case "Boolean": {
	    		
		        }
		        break;
		    case "DropDown": {
	    		
		        }
			    break;
		    case "EntitySearch": {
	    		
		        }
		        break;
		    default: {
		    	throw new Error("Invalid Component Type. Unknown to the Render Component Plugin. ");
		    }
		}
	};
}( jQuery ));
