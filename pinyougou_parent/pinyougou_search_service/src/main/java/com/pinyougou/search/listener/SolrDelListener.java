package com.pinyougou.search.listener;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.*;
import java.io.Serializable;
import java.util.List;

public class SolrDelListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;//报错应该是使用dubbox扫描创建的包，使用autowired注入

    @Override
    public void onMessage(Message message) {

        try {
            //获取对象
            ObjectMessage objectMessage = (ObjectMessage)message;
            Long[] ids = (Long[]) objectMessage.getObject();
            itemSearchService.deleteByIds(ids);
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
