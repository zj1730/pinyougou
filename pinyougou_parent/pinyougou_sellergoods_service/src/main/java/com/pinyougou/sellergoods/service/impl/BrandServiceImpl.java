package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.BrandService;
import com.pinyougou.pojo.TbBrandExample;
import entity.PageResult;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper tbBrandMapper;
    @Override
    public List<TbBrand> findAll() {
       return tbBrandMapper.selectByExample(null);
    }

    @Override
    public PageResult findByPage(int pageNum, int pageSize) {

        //调用分页插件
        PageHelper.startPage(pageNum,pageSize);
        Page<TbBrand> page = (Page<TbBrand>)tbBrandMapper.selectByExample(null);
        return new PageResult(page.getResult(),page.getTotal());
    }

    @Override
    @Transactional
    public void add(TbBrand tbBrand) {
        //调用dao完成添加
        tbBrandMapper.insert(tbBrand);
    }

    @Override
    public TbBrand findOne(Long id) {

        return tbBrandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(TbBrand tbBrand) {
        tbBrandMapper.updateByPrimaryKey(tbBrand);
    }

    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            tbBrandMapper.deleteByPrimaryKey(id);
        }
    }

    @Override
    public PageResult search(int pageNum, int pageSize, TbBrand tbBrand) {

        //开启分页
        PageHelper.startPage(pageNum,pageSize);
        TbBrandExample example = new TbBrandExample();
        TbBrandExample.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotEmpty(tbBrand.getName())){
            criteria.andNameLike("%"+tbBrand.getName()+"%");
        }
        if (StringUtils.isNotEmpty(tbBrand.getFirstChar())){
            criteria.andFirstCharEqualTo(tbBrand.getFirstChar());
        }
        /*if (tbBrand!=null){
            if( tbBrand.getName()!=null&&tbBrand.getName().length()>0){
                criteria.andNameLike("%"+tbBrand.getName()+"%");
            }
            if (tbBrand.getFirstChar()!=null&&tbBrand.getFirstChar().length()>0){
                criteria.andFirstCharEqualTo(tbBrand.getFirstChar());
            }
        }*/

        Page<TbBrand> pageInfo = (Page<TbBrand>) tbBrandMapper.selectByExample(example);
        return new PageResult(pageInfo.getResult(), pageInfo.getTotal());

    }

    /*PageHelper.startPage(pageNum, pageSize);
		Page<TbBrand> page=   (Page<TbBrand>) brandMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
*/


}
