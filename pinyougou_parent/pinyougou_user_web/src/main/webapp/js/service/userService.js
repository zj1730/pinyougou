//服务层
app.service('userService',function($http){
	    	

	//增加 
	this.add=function(entity,code){
		return  $http.post('../user/add.do?code='+code,entity );
	}

	//获取验证码
	this.getCode=function (phone) {
		return $http.get('../user/getCode.do?phone='+phone);
    }

});
