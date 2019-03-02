//控制
app.controller('itemController' ,function($scope){	

	//定义规格数据全局变量
	$scope.spec={};
    $scope.sku={};

	$scope.num=1;
	//数据加减操作
	$scope.addNum=function(num){
		$scope.num+=num;
		if($scope.num<1){
			$scope.num=1;
		}
	}

	//规格属性的获取操作
	$scope.selectSpec=function (key, value) {
		if(value!=null&&value!=""){
            $scope.spec[key]=value;
            $scope.updateSkuBySpec();
        }

    }

    //判断规格属性是否被选中
	$scope.isSpecSelected=function (key, value) {
		if($scope.spec[key]==value){
			return true;
		}
		return false;

    }

    //加载sku数据
	$scope.loadSku=function () {
        $scope.sku=skuList[0];
        $scope.spec=JSON.parse(JSON.stringify($scope.sku.spec));
    }

    $scope.updateSkuBySpec=function () {
		for(var i=0;i<skuList.length;i++){
			var skuSpec=skuList[i].spec;
			//判断两个对象是否一样
			if($scope.matchObject(skuSpec,$scope.spec)){
				$scope.sku=skuList[i];
				return;
			}
		}

    }
    //判断两个对象(json)是否一样
	$scope.matchObject=function (obj1, obj2) {
		for(var key in obj1){
			if(obj1[key]!=obj2[key]){
				return false;
			}
		}
        for(var key in obj2){
            if(obj2[key]!=obj1[key]){
                return false;
            }
        }
        return true;
    }

    //添加到购物车  -- 预留接口
    $scope.addToCart=function(){
        alert('skuid:'+$scope.sku.id);
    }




});	
