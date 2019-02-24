 //品牌控制层 
app.controller('baseController' ,function($scope){	
	
    //重新加载列表 数据
    $scope.reloadList=function(){

    	//切换页码  
    	$scope.search( $scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);	   	
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
	
	$scope.selectIds=[];//选中的ID集合 

	//更新复选
	$scope.updateSelection = function($event, id) {		
		if($event.target.checked){//如果是被选中,则增加到数组
			$scope.selectIds.push( id);			
		}else{
			var idx = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(idx, 1);//删除 
		}
	}
	
	//json串转为字符串信息
	/*$scope.jsonToString=function(jsonString,key){

		var json= JSON.parse(jsonString);
		var value="";

		for(var i=0;i<json.length;i++){
			if(i>0){
				value+=",";
			}
			value +=json[i][key];
		}

		return value;
	}*/

	$scope.jsonToString=function (jsonString, key) {
		//将jsonString字符串转换为自己需要的字符串格式
		var json = JSON.parse(jsonString);
		var value="";
		for(var i;i<json.length;i++){
			if(i>0){
				value+=',';
			}
			 value+=json[i][key];
		}
    }

    //查找对象集合中用户属性key的值等于keyValue的对象
    $scope.searchObjectByKey=function (list,key,keyValue) {
		//遍历集合
		for(var i=0;i<list.length;i++){
			if(list[i][key]==keyValue){//错误代码  if(list[i][key]=keyValue）
				return list[i];
			}
		}
		return null;
    }

	
});	