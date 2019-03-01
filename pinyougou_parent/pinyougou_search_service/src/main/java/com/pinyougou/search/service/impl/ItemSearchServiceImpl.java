package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import javax.management.Query;
import java.util.*;

@Service(timeout = 5000)
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

    @Override
    public void importList(List<TbItem> tbItems) {
        if(tbItems!=null&&tbItems.size()>0){
            //包含动态域，需要进行转换
            for (TbItem tbItem : tbItems) {
                //获取规格数据
                String spec = tbItem.getSpec();
                //转换为json对象
                Map map = JSON.parseObject(spec,Map.class);
                //设置到item的map中
                tbItem.setSpecMap(map);
            }
            solrTemplate.saveBeans(tbItems);
            solrTemplate.commit();
        }
    }

    @Override
    public void deleteByIds(Long[] ids) {
        SimpleQuery query = new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsid").in(Arrays.asList(ids));
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    private Map searchBrandListAndSpecList(String category) {
        Map map = new HashMap();
        //根据商品分类从redis中获取数据
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        if(typeId!=null){
            //根据模板id查询商品品牌数据
            map.put("brandList",redisTemplate.boundHashOps("brandList").get(typeId+""));
            //根据模板id查询规格数据
            map.put("specList",redisTemplate.boundHashOps("specList").get(typeId+""));

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
        //高亮显示参数配置 Highlight
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");//选择要高亮显示的域-标题才会高亮，其他地方有关键字也不高亮
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        highlightOptions.setSimplePostfix("</em>");
        query.setHighlightOptions(highlightOptions);//给query设置高亮台条件

        //关键词参数配置       选择要查询的域为关键词域，这个域的值为keywords  criteria貌似是进行模糊查询
        String keywords= ((String) searchMap.get("keywords")).replace(" ","");
        Criteria criteria=new Criteria("item_keywords").is(keywords);
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

        //价格过滤
        if(!"".equals(searchMap.get("price"))){
            //进行字符串切割获取数据
            String[] prices = ((String)searchMap.get("price")).split("-");
            if(!"0".equals(prices[0])){
                FilterQuery filterQuery = new SimpleFilterQuery();
                Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(prices[0]);//注意，此处传入是Object，可以不用转换为数字
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
            if(!"*".equals(prices[1])){
                FilterQuery filterQuery = new SimpleFilterQuery();
                Criteria filterCriteria = new Criteria("item_price").lessThanEqual(prices[1]);//注意，此处传入是Object，可以不用转换为数字
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

       //获取分页参数
        Integer pageNo = (Integer) searchMap.get("pageNo");
        if (pageNo==null){
            pageNo=1;
        }
        //获取分页条数
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if(pageSize==null){
            pageSize=20;
        }
        query.setOffset((pageNo-1)*pageSize);
        query.setRows(pageSize);

        //排序
        String sortField = (String) searchMap.get("sortField");
        String sortValue = (String) searchMap.get("sortValue");
        if(StringUtils.isNotEmpty(sortValue)&&StringUtils.isNotEmpty(sortField)){
            if("DESC".equals(sortValue)){
                Sort sort = new Sort(Sort.Direction.DESC,"item_"+sortField);
                query.addSort(sort);
            }else if("ASC".equals(sortValue)){
                Sort sort = new Sort(Sort.Direction.ASC,"item_"+sortField);
                query.addSort(sort);
            }
        }


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
        map.put("totalPage",tbItems.getTotalPages());
        map.put("totalCount",tbItems.getTotalElements());
        //获取分页
        return map;

    }
}


