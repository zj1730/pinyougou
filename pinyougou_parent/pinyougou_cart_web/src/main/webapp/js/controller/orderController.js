app.controller('orderController',function ($scope,addressService,orderService,cartService) {

    $scope.cartList={};
    $scope.totalValue={totalNum:0,totalMoney:0};

    $scope.addressList={};
    //当前选中地址
    $scope.address={};
    $scope.order={paymentType:'1'};

    $scope.findAddress=function (userId) {
        addressService.findAll().success(function (response) {

            $scope.addressList=response;
            //设置默认地址
            for(var i=0;i< $scope.addressList.length;i++){
                if($scope.addressList[i].isDefault=='1'){
                    $scope.address=$scope.addressList[i];
                    break;
                }
            }

        })
    }

    $scope.selectAddress=function(address){
        $scope.address=address;
    }



    $scope.isSelectedAddress=function(address){
        if(address==$scope.address){
            return true;
        }else{
            return false;
        }
    }


    //选择支付方式
    $scope.selectPayType=function(type){
        $scope.order.paymentType= type;
    }

    $scope.findCartList=function () {
        cartService.findCartList().success(function (response) {
            $scope.cartList=response;
            //查询后更新总价
            $scope.totalPrices($scope.cartList);
        });

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

    $scope.submitOrder=function () {

        //将联系人信息封装到订单实体中
        $scope.order.receiverAreaName=$scope.address.address;//地址
        $scope.order.receiverMobile=$scope.address.mobile;//手机
        $scope.order.receiver=$scope.address.contact;//联系人

        orderService.add($scope.order).success(function (response) {
            if(response.success){
                if($scope.order.paymentType=='1'){//如果是微信支付，跳转到支付页面
                    location.href="pay.html";
                }else{//如果货到付款，跳转到提示页面
                    location.href="paysuccess.html";
                }
            }else{
                alert(response.message);
            }
        });
    }




});