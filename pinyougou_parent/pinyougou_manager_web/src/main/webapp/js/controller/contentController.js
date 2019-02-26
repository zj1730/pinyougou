 //控制层 
app.controller('contentController' ,function($scope,$controller,uploadService ,contentService, contentCategoryService){
	
	$controller('baseController',{$scope:$scope});//继承

    //广告主状态数组
    $scope.status=["无效","有效"];

    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		contentService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		contentService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		contentService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=contentService.update( $scope.entity ); //修改  
		}else{
		    if($scope.entity.status==null){
                $scope.entity.status='0';
            }
			serviceObject=contentService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		contentService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		contentService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    //文件上传
    $scope.uploadFile=function () {
        // alert( $scope.image_entity.url);
        uploadService.uploadFile().success(function (response) {
            //上传成功
            if(response.success){
                $scope.entity.pic=response.message;

            }else{
                alert(response.message);
            }
        }).error(function () {
            alert("上传失败");
        });
    };

	//查询所有
    $scope.findCategory=function () {
        contentCategoryService.findAll().success(function (response) {
            $scope.contentCategoryList=response;
        });
    }
    
});	
