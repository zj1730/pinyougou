app.service('loginService',function ($http) {
   //请求登录信息
   this.getLoginName=function () {
        return $http.get("../login/name.do");
   };
});