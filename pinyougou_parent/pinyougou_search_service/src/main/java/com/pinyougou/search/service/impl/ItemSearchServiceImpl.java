package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import javax.management.Query;
import java.util.*;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public Map<String, Object> search(Map searchMap) {
        HashMap map = new HashMap();
        /* //从索引库中查询数据  分页查询
        SimpleQuery query = new SimpleQuery("*:*");
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        ScoredPage<TbItem> result = solrTemplate.queryForPage(query, TbItem.class);
        map.put("rows",result.getContent());*/
        //关键字查找，高亮显示
        map.putAll(highlightSearch(searchMap));
        //商品分类查找
        List<String> categoryList = searchCategoryList(searchMap);
        map.put("categoryList",categoryList);
        //获取商品品牌和规格信息
        if (categoryList!=null&&categoryList.size()>0){
            Map brandListAndSpecList;
            String categoryName= (String) searchMap.get("category");
            if("".equals(categoryName)){
                brandListAndSpecList = searchBrandListAndSpecList(categoryList.get(0));
            }else{
                brandListAndSpecList= searchBrandListAndSpecList(categoryName);
            }
            map.putAll(brandListAndSpecList);
        }

        return map;
    }

    private Map searchBrandListAndSpecList(String category) {
        Map map = new HashMap();
        //根据商品分类从redis中获取数据
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        if(typeId!=null){
            //根据模板id查询商品品牌数据
            map.put("brandList",redisTemplate.boundHashOps("brandList").get(typeId));
            //根据模板id查询规格数据
            map.put("specList",redisTemplate.boundHashOps("specList").get(typeId));

        }

        return map;
    }

    /**
     * 关键词分组查询，获取分类集合
     * @param searchMap
     * @return
     */
    private List<String> searchCategoryList(Map searchMap) {

        ArrayList<String> list = new ArrayList<>();
        SimpleQuery query = new SimpleQuery("*:*");
        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        GroupOptions groupOptions=new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);

        //得到分组页
        GroupPage<TbItem> tbItems = solrTemplate.queryForGroupPage(query, TbItem.class);
        //得到分组结果集
        GroupResult<TbItem> groupResult = tbItems.getGroupResult("item_category");
        //得到分组结果入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //得到分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();

        for (GroupEntry<TbItem> tbItemGroupEntry : content) {
            list.add(tbItemGroupEntry.getGroupValue());
        }
        return list;
    }

    /**
     * 高亮显示，查询
     * @param searchMap
     * @return
     */
    private Map highlightSearch(Map searchMap){

        HashMap<Object, Object> map = new HashMap<>();
        //高亮查询
        HighlightQuery query=new SimpleHighlightQuery();
        //关键词参数配置       选择要查询的域为关键词域，这个域的值为keywords  criteria貌似是进行模糊查询
        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //分类过滤
        if(!"".equals(searchMap.get("category"))){
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery filterQuery= new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //品牌过滤
        if(!"".equals(searchMap.get("brand"))){
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery= new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }

        //规格选项过滤
        System.out.println("spec "+searchMap.get("spec"));//测试没有传入规格数据 spec:{}时候是什么值？
        if(searchMap.get("spec")!=null){
            Map<String,String> spec = (Map<String,String>)searchMap.get("spec");
            System.out.println("specMap");
            System.out.println(spec);
            for (String s : spec.keySet()) {
                Criteria filterCriteria = new Criteria("item_spec_"+s).is(spec.get(s));
                FilterQuery filterQuery= new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }


        //高亮显示参数配置
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");//选择要高亮显示的域-标题才会高亮，其他地方有关键字也不高亮
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        highlightOptions.setSimplePostfix("</em>");
        query.setHighlightOptions(highlightOptions);//给query设置高亮台条件
        //获取高亮结果
        HighlightPage<TbItem> tbItems = solrTemplate.queryForHighlightPage(query, TbItem.class);
        //对获取的结果进行遍历，手动设置需要进行高亮的字段
        //得到高亮显示入口
        List<HighlightEntry<TbItem>> highlighted = tbItems.getHighlighted();
        for (HighlightEntry<TbItem> tbItemHighlightEntry : highlighted) {
            //获取实体类   HighlightEntry：高亮实体    tbItemHighlightEntry.getHighlights()：高亮域集合
            TbItem item = tbItemHighlightEntry.getEntity();
            //获取高亮域集合
            List<HighlightEntry.Highlight> highlights = tbItemHighlightEntry.getHighlights();
            //实体中存在高亮域
            if(highlights.size()>0&&highlights.get(0).getSnipplets().size()>0){
                //设置高亮结果
                item.setTitle(highlights.get(0).getSnipplets().get(0));
            }
        }
        map.put("rows",tbItems.getContent());
        return map;
    }
}


