app.controller('payController' ,function($scope ,$location,payService){


    //本地生成二维码
    $scope.createNative=function(){
        payService.createNative().success(
            function(response){
                //金额显示
                $scope.money=response.total_fee ;	//金额
                //订单号显示
                $scope.out_trade_no= response.out_trade_no;//订单号
                //二维码
                var qr = new QRious({
                    element:document.getElementById('qrious'),
                    size:250,
                    level:'H',
                    value:response.code_url
                });

                //调用查询方法
                queryPayStatus($scope.out_trade_no);
            }
        );
    }

    //查询支付状态
    var queryPayStatus=function(out_trade_no){
        payService.queryPayStatus(out_trade_no).success(
            function(response){
                if(response.success){
                    location.href="paysuccess.html#?money="+$scope.money;
                }else{
                    if(response.message=="二维码超时"){
                        alert("支付超时，请重新支付");
                        $scope.createNative();
                    }else{
                        location.href="payfail.html";
                    }
                }
            }
        );
    }

    //页面跳转后金额显示
    $scope.getMoney=function () {
        return $location.search()['money'];
    }

});
