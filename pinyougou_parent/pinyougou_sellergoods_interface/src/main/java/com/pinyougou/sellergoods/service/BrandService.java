package com.pinyougou.sellergoods.service;

import entity.PageResult;
import com.pinyougou.pojo.TbBrand;

import java.util.List;
import java.util.Map;

public interface BrandService {

    List<TbBrand> findAll();
    PageResult findByPage(int pageNum, int pageSize);

    public void add(TbBrand tbBrand);

    /**
     * 根据id查询单个数据
     * @param id
     * @return
     */
    TbBrand findOne(Long id);

    /**
     * 更新品牌数据
     * @param tbBrand
     */
    void update(TbBrand tbBrand);

    /**
     * 删除选中数据
     * @param ids
     */
    void delete(Long[] ids);

    /**
     * 根据条件分页查询
     * @param pageNum
     * @param pageSize
     * @param tbBrand
     * @return
     */
    PageResult search(int pageNum, int pageSize, TbBrand tbBrand);

    /**
     * 查询所有商品信息，以下列选项要求的格式
     * @return
     */
    List<Map> findSelectList();


}
