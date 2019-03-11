package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WeiXinPayService;
import com.pinyougou.pay.utils.HttpClient;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service(timeout = 5000)
public class WeiXinPayServiceImpl implements WeiXinPayService {

    @Value("${appid}")
    private String appid;
    @Value("${partner}")
    private String partner;
    @Value("${partnerkey}")
    private String partnerkey;
    @Value("${notifyurl}")
    private String notifyurl;


    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        try {
            Map<String,String> map  = new HashMap<>();
            //微信公众号
            map.put("appid",appid);
            //商户号
            map.put("mch_id",partner);
            //随机字符串
            map.put("nonce_str", WXPayUtil.generateNonceStr());
            //商品描述
            map.put("body","品优购");
            //商家订单号
            map.put("out_trade_no",out_trade_no);
            //总金额
            map.put("total_fee",total_fee);
            // 终端ip
            map.put("spbill_create_ip","127.0.0.1");
            //通知地址
            map.put("notify_url",notifyurl);
            //交易类型
            map.put("trade_type","NATIVE");
            //map转xml
            String paramXml = WXPayUtil.generateSignedXml(map,partnerkey);

            //发送消息
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(paramXml);
            httpClient.post();
            //获取内容
            String content = httpClient.getContent();
            Map<String, String> paramMap = WXPayUtil.xmlToMap(content);
            //封装返回值参数
            HashMap<String, Object> returnMap = new HashMap<>();
            returnMap.put("code_url",paramMap.get("code_url"));
            BigDecimal totalFee = new BigDecimal(total_fee);
            totalFee = totalFee.divide(new BigDecimal(100));
            returnMap.put("total_fee",totalFee);
            returnMap.put("out_trade_no",out_trade_no);

            return returnMap;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap();
        }
    }

    @Override
    public Map queryPayStatus(String out_trade_no) {

        try {
            Map<String,String> map  = new HashMap<>();
            //微信公众号
            map.put("appid",appid);
            //商户号
            map.put("mch_id",partner);
            //商家支付单号
            map.put("out_trade_no",out_trade_no);
            //随机字符串
            map.put("nonce_str", WXPayUtil.generateNonceStr());
            //map转xml
            String paramXml = WXPayUtil.generateSignedXml(map,partnerkey);



            //发送消息
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true);
            httpClient.setXmlParam(paramXml);
            httpClient.post();
            //获取内容
            String content = httpClient.getContent();
            Map<String, String> returnMap = WXPayUtil.xmlToMap(content);

            return returnMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
