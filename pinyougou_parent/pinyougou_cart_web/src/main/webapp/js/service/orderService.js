app.service('orderService',function ($http) {
    this.add=function (order) {
        return $http.post('order/add.do',order);
    }
});