/*自定义控制器*/
app.controller("brandController",function ($scope,$controller,brandService) {

	$controller('baseController',{$scope:$scope});

    $scope.dele=function(){
        if(confirm("确认删除吗？")){
            brandService.dele($scope.selectIds).success(function(response){
                //接收数据成功，进行处理
                if(response.success){

                    $scope.reloadList();
                }else{
                    alert(response.message);
                }
            });
        }
    }


    /*实现功能，从后端获取数据，并更新到scope域中对应的数据*/
    $scope.findOne=function (id) {
        brandService.findOne(id).success(function (response) {
            //后端成功响应后，更新数据到域中
            $scope.entity=response;
        });
    }

    /*添加*/
    $scope.save=function(){

        //判断当前点击的保存是添加还是修改
        var Object=brandService.add($scope.entity);
        //判断当前域中的entity数据是否存在id（add不存在，findone存在）
        if($scope.entity.id!=null){
            Object=brandService.update( $scope.entity);
        }

        Object.success(function (response) {
            //请求完成，前端传递过来数据result数据
            if(response.success){
                // 添加成功，刷新页面
                $scope.reloadList();
            }else{
                //传输不成功，弹出错误信息
                alert(response.message);
            }
        });
        $scope.entity={};
    }
    /*TK
    * 编写这个方法作用是什么？是在跳出的编辑添加页面，需要将数据提交给后台，所以肯定是要进行ajax请求
    * 需要域中数据传给ajax，域中的数据就是在scope中
    * */

    /*编写findAll方法   控制器中编写方法*/
    $scope.findAll=function () {
        brandService.findAll().success(function (response) {
            $scope.list=response;
        });
    }


    //根据条件查询

    $scope.searchEntity={};
    $scope.search=function (pageNum,pageSize) {
        brandService.search(pageNum,pageSize,$scope.searchEntity).success(function (response) {
            $scope.list=response.rows;
            $scope.paginationConf.totalItems=response.total;//更新总记录数
        });
    };


    $scope.findByPage=function (pageNum,pageSize) {
        brandService.findByPage().success(function (response) {
            $scope.list=response.rows;
            $scope.paginationConf.totalItems=response.total;//更新总记录数
        });
    };

});