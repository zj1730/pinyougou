 //控制层 
app.controller('userController' ,function($scope   ,userService){

	$scope.entity={};
	$scope.password;
	//注册
	$scope.reg=function () {
		if($scope.entity.password!=$scope.password){
			alert("两次密码不一致，请重新输入");
			return;
		}

		userService.add($scope.entity,$scope.code).success(function (response) {
			if(response.success){
				alert(response.message);
			}else {
				alert(response.message);
			}
        })
    }

    //获取验证码
	$scope.getCode=function () {
		userService.getCode($scope.entity.phone).success(function (response) {
			alert(response.message);
        })
    }
	
	 

    
});	
