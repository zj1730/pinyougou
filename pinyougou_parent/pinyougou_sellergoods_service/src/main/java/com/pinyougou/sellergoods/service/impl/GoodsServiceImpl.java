package com.pinyougou.sellergoods.service.impl;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.pojogroup.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;

	@Autowired
	private TbGoodsDescMapper tbGoodsDescMapper;

	@Autowired
	private TbItemCatMapper tbItemCatMapper;

	@Autowired
	private TbBrandMapper tbBrandMapper;

	@Autowired
	private TbSellerMapper tbSellerMapper;

	@Autowired
	private TbItemMapper tbItemMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {

		/*
		* 要添加两张表的数据，两张表数据关联，先插入主表，返回id，然后再插入从表
		* */
		TbGoods tbGoods = goods.getGoods();
		tbGoods.setAuditStatus("0");
		goodsMapper.insert(tbGoods);
		TbGoodsDesc goodsDesc = goods.getGoodsDesc();
		goodsDesc.setGoodsId(tbGoods.getId());
		//插入goodsDesc表数据
		tbGoodsDescMapper.insert(goodsDesc);
		//封装itemList数据

		//如果启用规格
		if("1".equals(tbGoods.getIsEnableSpec())){
			List<TbItem> itemList = goods.getItemList();
			for (TbItem tbItem : itemList) {
				//标题 SPU名称+ 规格选项值（需要遍历）
				String title=goods.getGoods().getGoodsName();
				//获取规格选项值  "spec":{"网络":"移动3G","机身内存":"16G"}
				Map<String,Object> map = JSON.parseObject(tbItem.getSpec());
				for (String s : map.keySet()) {
					title+=" "+map.get(s);
				}
				tbItem.setTitle(title);
				//设置其他属性
				setItemValues(tbItem,goods);
				tbItemMapper.insert(tbItem);
			}
		}else{
			//没有启用规格，手动封装数据
			TbItem tbItem = new TbItem();
			//标题
			tbItem.setTitle(tbGoods.getGoodsName());
			//价格
			tbItem.setPrice(tbGoods.getPrice());
			//库存状态
			tbItem.setNum(9999);
			//规格
			tbItem.setSpec("{}");
			//是否启用
			tbItem.setStatus("1");
			//是否默认
			tbItem.setIsDefault("1");
			//设置其他后台需要设置的数据
			setItemValues(tbItem,goods);
			tbItemMapper.insert(tbItem);
		}



	}
	private void setItemValues(TbItem item,Goods goods){
		TbGoods good = goods.getGoods();
		//商品分类信息
		item.setCategoryid(good.getCategory3Id());
		//创建和更新日期
		item.setCreateTime(new Date());
		item.setUpdateTime(new Date());
		//商品id和商家id
		item.setGoodsId(good.getId());
		item.setSeller(good.getSellerId());
		//设置分类名称
		TbItemCat tbItemCat = tbItemCatMapper.selectByPrimaryKey(good.getCategory3Id());
		item.setCategory(tbItemCat.getName());
		//设置品牌名称
		TbBrand tbBrand = tbBrandMapper.selectByPrimaryKey(good.getBrandId());
		item.setBrand(tbBrand.getName());
		//设置商家名称（店铺名称）
		TbSeller seller = tbSellerMapper.selectByPrimaryKey(good.getSellerId());
		item.setSeller(seller.getNickName());
		//设置图片
		List<Map> maps = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
		if (maps.size()>0){
			item.setImage((String)maps.get(0).get("url"));
		}



	}


	
	/**
	 * 修改
	 */
	@Override
	public void update(TbGoods goods){
		goodsMapper.updateByPrimaryKey(goods);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbGoods findOne(Long id){
		return goodsMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			goodsMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		
		if(goods!=null){			
						if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	
}
