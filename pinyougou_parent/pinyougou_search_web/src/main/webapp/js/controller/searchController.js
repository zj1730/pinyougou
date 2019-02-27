app.controller('searchController',function ($scope,$controller,searchService) {

    $controller('baseController',{$scope:$scope});//继承

    $scope.searchMap={keywords:'',category:'',brand:'',spec:{}};
    $scope.resultMap={rows:'',categoryList:''};
    $scope.search=function () {
        searchService.search($scope.searchMap).success(function (response) {
            $scope.resultMap=response;
        });
    }
    //添加过滤添加
    $scope.addSearchMap=function (key, value) {
        //如果是分类数据或则是品牌数据 是单一的属性+值
        if(key=='category'||key=='brand'){
            $scope.searchMap[key]=value;
        }else{
            $scope.searchMap.spec[key]=value;
        }
        //查询
        $scope.search();
    };


    //删除过滤添加
    $scope.deleSearchMap=function (key) {
        if(key=='category'||key=='brand'){
            $scope.searchMap[key]='';
        }else{
            //删除spec对应的属性
            delete $scope.searchMap.spec[key];
        }
        //查询
        $scope.search();
    }

})