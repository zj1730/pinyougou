package com.pinyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.PageService;
import com.pinyougou.pojo.*;
import com.sun.org.apache.xpath.internal.operations.Bool;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;

@Service
public class PageServiceImpl implements PageService {

    @Value("${pageDir}")
    private String pageDir;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Autowired
    private TbGoodsMapper tbGoodsMapper;

    @Autowired
    private TbGoodsDescMapper tbGoodsDescMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbItemMapper tbItemMapper;

    @Override
    public boolean getItemHtml(Long goodsId) {

        try {
            //获取配置对象
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            //获取模板对象
            Template template = configuration.getTemplate("item.rtf");
            //创建数据模型
            HashMap<String, Object> dataModel = new HashMap<>();
            //获取商品信息
            TbGoods tbGoods = tbGoodsMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goods",tbGoods);
            //获取商品描述信息
            TbGoodsDesc tbGoodsDesc = tbGoodsDescMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goodsDesc",tbGoodsDesc);
            //获取分类信息
            String itemCat1 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName();
            String itemCat2 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName();
            String itemCat3 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName();
            dataModel.put("itemCat1",itemCat1);
            dataModel.put("itemCat2",itemCat2);
            dataModel.put("itemCat3",itemCat3);

            //获取SKU数据
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(goodsId);
            criteria.andStatusEqualTo("1");
            example.setOrderByClause("is_default desc");//填写数据库字段
            List<TbItem> tbItems = tbItemMapper.selectByExample(example);
            dataModel.put("skuList",tbItems);
//            System.out.println(tbItems.get(0));
//            System.out.println(tbItems.get(0).getPrice());
            //创建字符转换流对象
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(pageDir + goodsId + ".html"), "utf-8");
            //生成模板
            template.process(dataModel,writer);
            //关闭流
            writer.close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
}
