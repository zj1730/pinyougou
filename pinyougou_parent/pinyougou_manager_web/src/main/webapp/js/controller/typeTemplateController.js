 //控制层 
app.controller('typeTemplateController' ,function($scope,$controller   ,typeTemplateService,brandService,specificationService){
	
	$controller('baseController',{$scope:$scope});//继承


    //添加属性列表
    $scope.addTableRow=function () {
      $scope.entity.customAttributeItems.push({});
    };

    //删除属性列表
    $scope.delTableRow=function (index) {
        $scope.entity.customAttributeItems.splice(index,1);
    };

	//定义品牌列表的数据
    $scope.brandList={data:[{id:1,text:'联想'},{id:2,text:'华为'},{id:3,text:'小米'}]};

    //查询品牌列表数据
    $scope.findBrand=function () {
		//调用brandService
		brandService.findSelectList().success(function (response) {
            $scope.brandList={data:response};
        });
    };

    $scope.specificationList={data:[{id:1,text:'联想'},{id:2,text:'华为'},{id:3,text:'小米'}]};
    //查询规格下拉数据
    $scope.findSpecification=function () {
		//调用specificationService
		specificationService.findSelectList().success(function (response) {
            $scope.specificationList={data:response};
        });
    };

    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		typeTemplateService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		typeTemplateService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){
		typeTemplateService.findOne(id).success(
			function(response){
			    //需要将读取到的字符串数据转换为json格式
				$scope.entity= response;
				$scope.entity.brandIds=JSON.parse(response.brandIds);
				$scope.entity.specIds=JSON.parse(response.specIds);
				$scope.entity.customAttributeItems=JSON.parse(response.customAttributeItems);
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=typeTemplateService.update( $scope.entity ); //修改  
		}else{
			serviceObject=typeTemplateService.add( $scope.entity  );//增加 
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
		typeTemplateService.dele( $scope.selectIds ).success(
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
		typeTemplateService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
});	
