app.service('cartService',function ($http) {
    this.findCartList=function () {
        return $http.get('cart/findCartList.do');
    }

    this.addNum=function (itemId,num) {
        return $http.get('cart/addToCartList.do?itemId='+itemId+'&num='+num);
    }
})