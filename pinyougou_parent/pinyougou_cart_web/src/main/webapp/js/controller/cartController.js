app.controller('cartController' ,function($scope  ,cartService){

    $scope.cartList={};
    $scope.totalValue={totalNum:0,totalMoney:0};

    $scope.findCartList=function () {
        cartService.findCartList().success(function (response) {
            $scope.cartList=response;
            //查询后更新总价
            $scope.totalPrices($scope.cartList);
        });

    }

    $scope.addNum=function (itemId,num) {
        cartService.addNum(itemId,num).success(function (response) {
            if(response.success){
                $scope.findCartList();
            }else{
                alert("添加到购物车失败");
            }
        })
    }

    $scope.totalPrices=function (cartList) {
        //对cartList进行遍历(获取每个商家购物车)
        $scope.totalValue={totalNum:0,totalMoney:0};
        for(var i=0;i<cartList.length;i++){
            var cart=cartList[i].tbOrderItems;
            for(var j=0;j<cart.length;j++){
                $scope.totalValue.totalMoney+=cart[j].totalFee;
                $scope.totalValue.totalNum+=cart[j].num;
            }
        }
    }
});
