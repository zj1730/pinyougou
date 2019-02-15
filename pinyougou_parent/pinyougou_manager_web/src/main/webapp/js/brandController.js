/*自定义控制器*/
app.controller("brandController",function ($scope,brandService) {
    /*编写findAll方法   控制器中编写方法*/
    $scope.findAll=function () {
        $http.get('../brand/findAll.do').success(function (response) {
            $scope.list=response;
        });
    }

    //重新加载列表 数据
    $scope.reloadList=function(){
        //切换页码
        $scope.findByPage( $scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    }

    //分页控件配置
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function(){
            $scope.reloadList();//重新加载
        }
    };

    /*  $http.get('../brand/findPage.do?page='+page+'&rows='+rows).success(
          function(response){
              $scope.list=response.rows;
              $scope.paginationConf.totalItems=response.total;//更新总记录数
          }
      );*/


    $scope.findByPage=function (pageNum,pageSize) {
        brandService.findByPage(pageNum,pageSize).success(function (response) {
            $scope.list=response.rows;
            $scope.paginationConf.totalItems=response.total;//更新总记录数
        });
    }
});