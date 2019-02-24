 //控制层 
app.controller('goodsController' ,function($scope,$controller ,$location  ,goodsService ,uploadService,itemCatService,typeTemplateService){
	
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
	$scope.findOne=function(){
	    //获取路径中的参数
        var id = $location.search()['id'];
        if(id==null){
            //添加页面时候，没有id值,不进行查询
            return;
        }
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
				//数据填充到富文本编辑器中
				editor.html(response.goodsDesc.introduction);
				//对模板数据进行转换
				$scope.entity.goodsDesc.customAttributeItems=JSON.parse(response.goodsDesc.customAttributeItems);
				//对规格数据进行转换
                $scope.entity.goodsDesc.specificationItems=JSON.parse(response.goodsDesc.specificationItems);
                //对图片数据进行转换
                $scope.entity.goodsDesc.itemImages=JSON.parse(response.goodsDesc.itemImages);
                //对itemList数据进行转换
				for(var i=0;i<$scope.entity.itemList.length;i++){
                    $scope.entity.itemList[i].spec=JSON.parse($scope.entity.itemList[i].spec);
				}
			}
		);				
	};

	//判断复选框对应的数据是否在goods的规格数据中
	$scope.getCheckedStatus=function(name,value){
		//判断对应的规格和属性是否存在
		//[{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]},{"attributeName":"屏幕尺寸","attributeValue":["6寸","5寸"]}]
		var spec=$scope.entity.goodsDesc.specificationItems;
		for(var i=0;i<spec.length;i++){
			if(spec[i].attributeName==name){
				for(var j=0;j<spec[i].attributeValue.length;j++){
					if(spec[i].attributeValue[j]==value){
						return true;
					}
				}
				return false;
			}
		}
		return false;
	};


	//添加
	/*$scope.add=function () {
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
    };*/

    //保存
    $scope.save=function(){
        //提取文本编辑器的内容
        $scope.entity.goodsDesc.introduction=editor.html();
        var serviceObject;//服务层对象

        if($scope.entity.goods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
            //获取富文本编辑器内容

			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
                    // //重新查询
		        	// $scope.reloadList();//重新加载
                    alert("保存成功");
                    location.href="goods.html";//跳转到商品列表页
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
        // alert( $scope.image_entity.url);
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
    $scope.entity={ goodsDesc:{itemImages:[],specificationItems:[]}  };
	// $scope.entity={goods:{},goodsDesc:{itemImages:[]}};
	$scope.addImage_entity=function () {
		$scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }

    //删除图片
	$scope.deleImage_entity=function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index,1);
    }

    //一级分类下拉选框
    $scope.selectItemCat1List=function(){
		//查询一级分类 parentId=0
		itemCatService.findByParentId(0).success(function (response) {
            $scope.itemCat1List=response;
        });
	};
	//二级分类下拉选框 定义的方法是被angularJs自动调用的
	$scope.$watch('entity.goods.category1Id',function (newValue, oldValue) {
		itemCatService.findByParentId(newValue).success(function (response) {
			$scope.itemCat2List=response;
        });
    });

	//三级分类下拉选框
    $scope.$watch('entity.goods.category2Id',function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(function (response) {
            $scope.itemCat3List=response;
        });
    });

    //三级分类后读取模板id
    $scope.$watch('entity.goods.category3Id',function (newValue, oldValue) {
		itemCatService.findOne(newValue).success(function (response) {
			$scope.entity.goods.typeTemplateId=response.typeId;
        });
	});

    //定义模板格式
    // $scope.typeTemplate={brandIds:{},customAttributeItems:{}};
    //品牌选项下拉框（模板id改变，）
	$scope.$watch('entity.goods.typeTemplateId',function(newValue,oldValue){
		//查询模板，将模板中的品牌列表json数据转换为json格式数据
		typeTemplateService.findOne(newValue).success(function (response) {
            $scope.typeTemplate=response;//查询到模板的数据
            $scope.typeTemplate.brandIds=JSON.parse(response.brandIds);//将品牌列表的数据转为Json格式，应该赋值给goods数据
            if($location.search()['id']==null){
            	//新增
                $scope.entity.goodsDesc.customAttributeItems=JSON.parse(response.customAttributeItems);
            }

        });
		typeTemplateService.findSpecList(newValue).success(function (response) {
			//将数据传递给specList数据
			$scope.specList=response;
        });

	});

	//更新entity.goodsDesc.specificationItems
    // $scope.entity.goodsDesc.specificationItems=[];//使用到必须定义
	$scope.updateSpecAttribute=function ($event,name,value) {
		//查找集合中是否存在对应属性名的对象
		var object=$scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,"attributeName",name);
		//如果对象存在，进行数据的追加
		if(object!=null){
            //如果是选中
			if($event.target.checked){
                object.attributeValue.push(value);//[{"attributeName":"网络","attributeValue":["移动3G","移动4G"],"网络":"移动4G"}]
            }else{
				var index = object.attributeValue.indexOf(value);
				object.attributeValue.splice(index,1);
				//如果移除后元素没有了，就删除整个对象
				if(object.attributeValue.length<=0){
                    var index = $scope.entity.goodsDesc.specificationItems.indexOf(object);
                    $scope.entity.goodsDesc.specificationItems.splice(index,1);
				}
			}

		}else{
			//不存在该属性，就在整个集合中添加对应的属性
            $scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]});

        }
        //创建SKU数据
        $scope.createSKU();
    };

	//生成SKU数据
	$scope.createSKU=function () {
		//新建一个基本的表数据
        $scope.entity.itemList=[{spec:{},price:0,num:99999,status:'0',isDefault:'0'} ];//列表初始化
		//获取SPU中的规格数据
		var specList=$scope.entity.goodsDesc.specificationItems;
		//对规格表数据进行遍历
		for(var i=0;i<specList.length;i++){
			//对规格数据进行遍历，每次传递一个itemList,将这个表增加一个规格
            $scope.entity.itemList=addColumn($scope.entity.itemList,specList[i].attributeName,specList[i].attributeValue);
		}
    };

	//给itemList增加一个规格数据
	var addColumn=function (list,name,values) {
		//定义一个空itemList
		var newList=[];
		//遍历原有集合进行克隆
		for(var i=0;i<list.length;i++){
			var oldRow=list[i];
			//遍历values值
			for(var j=0;j<values.length;j++){
				//克隆新的行
				var newRow = JSON.parse(JSON.stringify(oldRow));
				//给新的行填充数据
				newRow.spec[name]=values[j];
				//将新的行push到newList中
				newList.push(newRow);
			}
		}
		return newList;
    };

	//状态数组
    $scope.status=['未审核','已审核','审核未通过','关闭'];//商品状态

	//查询全部分类信息
    $scope.itemCatList=[];
	$scope.findItemCat=function () {
        itemCatService.findAll().success(function (response) {
			//将分类数据转换为数组
            for(var i=0;i<response.length;i++){
                $scope.itemCatList[response[i].id]=response[i].name;
			}
        });
    }
});	
