app.service('itemService',function ($http) {
    //调用添加购物车的方法
    this.addToCart=function (itemId, num) {
        return $http.get('http://localhost:9107/cart/addToCartList.do?itemId='+itemId+'&num='+num,{'withCredentials':true});
    };
});