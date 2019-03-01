app.controller('searchController',function ($scope,$controller,$location,searchService) {

    $controller('baseController',{$scope:$scope});//继承

    //查询数据集合
    $scope.searchMap={keywords:'',category:'',brand:'',spec:{},price:'',pageNo:1,pageSize:20,sortField:'',sortValue:''};
    //结果数据
    $scope.resultMap={rows:'',categoryList:'',brandList:'',totalPage:'',totalCount:''};
    //页码列表
    $scope.pageList = [];
    //左点显示标记
    $scope.leftDotDis=false;
    //右点显示标志
    $scope.rightDotDis=false;

    $scope.search=function () {

        $scope.searchMap.pageNo=parseInt($scope.searchMap.pageNo);

        if($scope.searchMap.keywords!=null&&$scope.searchMap.keywords!=''){
            searchService.search($scope.searchMap).success(function (response) {
                $scope.resultMap=response;
                //搜索完成更新页码
                $scope.updatePageList();
            });
        }


    }
    //添加过滤添加
    $scope.addSearchMap=function (key, value) {
        //如果是分类数据或则是品牌数据 是单一的属性+值
        if(key=='category'||key=='brand'||key=='price'){
            $scope.searchMap[key]=value;
        }else{
            $scope.searchMap.spec[key]=value;
        }
        //查询
        $scope.search();
    };


    //删除过滤添加
    $scope.deleSearchMap=function (key) {
        if(key=='category'||key=='brand'||key=='price'){
            $scope.searchMap[key]='';
        }else{
            //删除spec对应的属性
            delete $scope.searchMap.spec[key];
        }
        //查询
        $scope.search();
    }

    //更新页码数据
    $scope.updatePageList=function () {
        var totalPage=$scope.resultMap.totalPage;
        var pageNo = $scope.searchMap.pageNo;
        var firstPage=1;
        var lastPage=totalPage;

        //清空页码数组
        $scope.pageList=[];
        //总页数大于5
        if(totalPage>5){
            //当前页不能显示全前2页
            if(pageNo<=3){
                lastPage=5;
            }else if(pageNo>=totalPage-2){//当前页不能显示全后2页
                firstPage=totalPage-4;
            }else{
                firstPage=pageNo-2;
                lastPage=pageNo+2;

            }
        }

        for(var i=firstPage;i<=lastPage;i++){
            $scope.pageList.push(i);
        }
        //判断是否显示省略号
        if(totalPage>5){
            if(firstPage>1){
                $scope.leftDotDis=true;
            }else{
                $scope.leftDotDis=false;
            }

            if(lastPage<totalPage){
                $scope.rightDotDis=true;
            }else{
                $scope.rightDotDis=false;
            }
        }
    }
    //页码跳转
    $scope.queryByPage=function (pageNo) {

        if(pageNo>$scope.resultMap.totalPage||pageNo<1){
            return;
        }
        $scope.searchMap.pageNo=pageNo;
        $scope.search();
    }

    //排序
    $scope.sortSearch=function(sortField,sortValue){
        $scope.searchMap.sortField=sortField;
        $scope.searchMap.sortValue=sortValue;

        $scope.search();
    }

    //判断关键字是否包含品牌
    $scope.keywordsIsBrand=function () {
        var brandList=$scope.resultMap.brandList;
        for(var i=0;i<brandList.length;i++){
            if($scope.searchMap.keywords.indexOf(brandList[i].text)>=0){
                return true;
            }
        }
        return false;
    }

    $scope.loadKeywords=function () {
        $scope.searchMap.keywords=$location.search()['keywords'];
        if($scope.searchMap.keywords==null){
            $scope.searchMap.keywords='';
        }
        $scope.search();
    }


})