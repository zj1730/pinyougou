package com.pinyougou.page.listener;

import com.pinyougou.page.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.Serializable;

public class pageDelListener implements MessageListener {

    @Autowired
    private PageService pageService;

    @Override
    public void onMessage(Message message) {

        try {
            ObjectMessage objectMessage = (ObjectMessage)message;
            Long[] ids = (Long[])objectMessage.getObject();
            pageService.delHtml(ids);
            System.out.println("删除页面成功");

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
