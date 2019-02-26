app.controller('searchController',function ($scope,$controller,searchService) {

    $controller('baseController',{$scope:$scope});//继承

    $scope.searchMap={};

    $scope.search=function () {
        searchService.search($scope.searchMap).success(function (response) {
            $scope.resultMap=response;
        })
    }

})