package com.pinyougou.search.service;

import com.pinyougou.pojo.TbItem;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    /**
     *
     * @param searchMap
     * @return
     */
    public Map<String,Object> search(Map searchMap);

    /**
     * 更新索引库内容
     * @param tbItems
     */
    public void importList(List<TbItem> tbItems);

    /**
     * 根据id删除索引库内容
     * @param ids
     */
    public void deleteByIds(Long[] ids);

}
