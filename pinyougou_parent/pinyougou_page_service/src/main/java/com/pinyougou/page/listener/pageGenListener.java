package com.pinyougou.page.listener;

import com.pinyougou.page.service.PageService;
import com.pinyougou.page.service.impl.PageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class pageGenListener implements MessageListener {

    @Autowired
    private PageService pageService;

    @Override
    public void onMessage(Message message) {

        try {
            //获取消息
            TextMessage textMessage = (TextMessage) message;
            String idStr = textMessage.getText();
            boolean flag = pageService.getItemHtml(Long.parseLong(idStr));
            System.out.println("生成静态页面："+flag);

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
