/*自定义服务*/
app.service("brandService",function ($http) {
    //分页查询
    this.findByPage=function(pageNum,pageSize) {
        return $http.get('../brand/findByPage.do?pageNum='+pageNum+'&pageSize='+pageSize);
    }
    //分页条按查询
    this.search=function(pageNum,pageSize,entity){
        return $http.post('../brand/search.do?pageNum='+pageNum+'&pageSize='+pageSize,entity);
    };

    //查询所有
    this.findAll=function () {
        return $http.get('../brand/findAll.do');
    };
    this.add=function (entity) {
        return $http.post('../brand/add.do',entity)
    };
    this.update=function (entity) {
        return $http.post('../brand/update.do',entity)
    };
    this.findOne=function (id) {
        return $http.get('../brand/findOne.do?id='+id);
    };
    this.dele=function (ids) {
        return $http.get('../brand/delete.do?ids='+ids);
    }

    this.findSelectList=function () {
        return $http.get('../brand/findSelectList.do');
    }
});