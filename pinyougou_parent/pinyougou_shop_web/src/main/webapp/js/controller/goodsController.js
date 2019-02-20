 //控制层 
app.controller('goodsController' ,function($scope,$controller   ,goodsService ,uploadService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}

	//添加
	$scope.add=function () {
        //获取富文本编辑器内容
		$scope.entity.goodsDesc.introduction=editor.html();
        goodsService.add( $scope.entity).success(
            function(response){
                if(response.success){
                    alert(response.message)
					$scope.entity={};
                    editor.html("");
                }else{
                    alert(response.message);
                }
            }
        );
    };
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
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
		goodsService.dele( $scope.selectIds ).success(
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
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	};

	//文件上传
    $scope.uploadFile=function () {
        alert( $scope.image_entity.url);
        uploadService.uploadFile().success(function (response) {
            //上传成功
            if(response.success){
                $scope.image_entity.url=response.message;
            }else{
                alert(response.message);
            }
        }).error(function () {
            alert("上传失败");
        });
    };
    //添加图片列表
	$scope.entity={goods:{},goodsDesc:{itemImages:[]}};
	$scope.addImage_entity=function () {
		$scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }

    //删除图片
	$scope.deleImage_entity=function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index,1);
    }

});	
