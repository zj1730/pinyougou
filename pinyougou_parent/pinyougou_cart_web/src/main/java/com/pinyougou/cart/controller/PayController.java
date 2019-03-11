package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeiXinPayService;
import com.pinyougou.pojo.TbPayLog;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utils.IdWorker;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private WeiXinPayService weiXinPayService;
    @Reference
    private OrderService orderService;

    @RequestMapping("/createNative")
    public Map createNative(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //读取支付日志
        TbPayLog payLog = orderService.searchPayLogFromRedis(name);
        if (payLog==null){
            return new HashMap();
        }
        return weiXinPayService.createNative(payLog.getOutTradeNo(),payLog.getTotalFee().intValue()+"");

    }
    /**
     * 查询支付状态
     * @param out_trade_no
     * @return
     */
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        Result result=null;
        //循环判断是否有接收到信息
        int count=0;
        while(true){
            //调用查询接口
            Map<String,String> map = weiXinPayService.queryPayStatus(out_trade_no);
            if(map==null){//出错
                result=new  Result(false, "支付出错");
                break;
            }
            if(map.get("trade_state").equals("SUCCESS")){//如果成功
                result=new  Result(true, "支付成功");
                //改变订单状态
                orderService.updateOrderStatus(out_trade_no,map.get("transaction_id"));
                break;
            }
            try {
                Thread.sleep(3000);//间隔三秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            count++;
            //超过5min失效
            if(count>=6){
                result=new  Result(false, "二维码超时");
                break;
            }
        }
        return result;
    }


}
