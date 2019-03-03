package com.pinyougou.search.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

public class SolrAddListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;//报错应该是使用dubbox扫描创建的包，使用autowired注入

    @Override
    public void onMessage(Message message) {

        try {
            //获取消息
            TextMessage textMessage = (TextMessage) message;
            String jsonString = ((TextMessage) message).getText();
            //json字符串转为对象
            List<TbItem> tbItems = JSON.parseArray(jsonString,TbItem.class);
            itemSearchService.importList(tbItems);
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
