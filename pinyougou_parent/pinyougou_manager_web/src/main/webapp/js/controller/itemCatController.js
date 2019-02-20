 //控制层 
app.controller('itemCatController' ,function($scope,$controller   ,itemCatService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	};
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	};
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;
				$scope.typeTemplateSelect=$scope.getOption(response.typeId,$scope.typeTemplate.data);
			}
		);				
	}

	//查询分类下一级数据
	$scope.findByParentId=function (parentId) {
		itemCatService.findByParentId(parentId).success(function (response) {
			$scope.list=response;
        });
    };

	//查询模板下拉选项数据
    //$scope.specificationList={data:[{id:1,text:'联想'},{id:2,text:'华为'},{id:3,text:'小米'}]};

    $scope.typeTemplate={data:[]};
    $scope.selectOptionList=function () {
        typeTemplateService.selectOptionList().success(function (response) {
				$scope.typeTemplate={data:response};
            }
		);
    };

    //根据id值，从下拉选数据集合中获取对应的数据
	$scope.getOption=function (id,selectList) {
		for(var i=0;i<selectList.length;i++){
			if(selectList[i].id==id){
                $scope.typeTemplateSelect=selectList[i];
                break;
			}
		}
    }

	
	//保存
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
            $scope.entity.typeId=$scope.typeTemplateSelect.id;
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
            $scope.entity.parentId=$scope.parentId;
            $scope.entity.typeId=$scope.typeTemplateSelect.id;
			serviceObject=itemCatService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询
                    $scope.grade=1;
                    $scope.parentId=0;
		        	$scope.selectList({id:0});//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemCatService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
                    $scope.grade=1;
                    $scope.parentId=0;
                    $scope.selectList({id:0});
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	//面包屑的数据更新
	$scope.grade=1;
	$scope.parentId=0;
	$scope.setGrade=function (value) {
		$scope.grade=value;
    }
	$scope.selectList=function (entity_p) {
		//刷新面包屑导航栏数据  entity_p表示当前要显示分类数据的父分类
		if($scope.grade==1){
			$scope.entity_1=null;
			$scope.entity_2=null;
		}
		if($scope.grade==2){
			$scope.entity_1=entity_p;
			$scope.entity_2=null;
		}
		if($scope.grade==3){
			$scope.entity_2=entity_p;
		}

		//执行分类数据的查询
		$scope.findByParentId(entity_p.id);
        $scope.parentId=entity_p.id;

    }
});

 /*$scope.selectList=function(p_entity){
     //alert($scope.grade);

     if($scope.grade==1){
         $scope.entity_1=null;
         $scope.entity_2=null;
     }
     if($scope.grade==2){

         $scope.entity_1=p_entity;
         $scope.entity_2=null;
     }
     if($scope.grade==3){
         $scope.entity_2=p_entity;
     }

     $scope.findByParentId(p_entity.id);

 }*/
