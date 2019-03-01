 //控制层 
app.controller('contentController' ,function($scope,$controller ,contentService){
	
	$controller('baseController',{$scope:$scope});//继承
	$scope.contentList=[];
	$scope.keywords='';
	$scope.findByCategoryId=function (id) {
        contentService.findByCategoryId(id).success(function (response) {
			$scope.contentList[id]=response;
        });
    }

    $scope.search=function () {
        location.href="http://localhost:9008/search.html#?keywords="+$scope.keywords;    }
    
});	
