package com.pinyougou.solrutil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
public class SolrUtils {

    @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired
    private SolrTemplate solrTemplate;

    public void add(){
        //添加到索引库中
        TbItem item=new TbItem();
        item.setId(1L);
        item.setBrand("华为");
        item.setCategory("手机");
        item.setGoodsId(1L);
        item.setSeller("华为2号专卖店");
        item.setTitle("华为Mate9");
        item.setPrice(new BigDecimal(2000));
        solrTemplate.saveBean(item);
        solrTemplate.commit();

        solrTemplate.saveBean(item);
    }
    public void delteById(){
        solrTemplate.deleteById("1");
        solrTemplate.commit();
    }
    //添加全部方法
    public void importItemData(){

        //调用itemMapper
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");
        System.out.println(tbItemMapper);
        List<TbItem> tbItems = tbItemMapper.selectByExample(example);
        for (TbItem tbItem : tbItems) {
            //获取规格数据
            String spec = tbItem.getSpec();
            //转换为json对象
            Map map = JSON.parseObject(spec,Map.class);
            //设置到item的map中
            tbItem.setSpecMap(map);
        }
//        System.out.println(tbItemMapper);
//        System.out.println(tbItems);

        solrTemplate.saveBeans(tbItems);
        solrTemplate.commit();


    }
    public void deleteAll(){
        Query query=new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();

    }


    public static void main(String[] args) {

        //创建spring对象
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtils solrUtils = (SolrUtils) applicationContext.getBean("solrUtils");

        //调用
        solrUtils.importItemData();
//        solrUtils.delteById();
//          solrUtils.deleteAll();
    }
}
