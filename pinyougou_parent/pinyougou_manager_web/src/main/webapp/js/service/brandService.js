/*自定义服务*/
app.service("brandService",function ($http) {
    this.findByPage=function(pageNum,pageSize) {
        return $http.get('../brand/findByPage.do?pageNum='+pageNum+'&pageSize='+pageSize);
    }

    this.save=function () {
        return $http.post('../brand/add.do',$scope.entity);
    }
});