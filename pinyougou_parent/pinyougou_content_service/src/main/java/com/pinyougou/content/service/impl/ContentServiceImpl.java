package com.pinyougou.content.service.impl;
import java.util.List;

import com.pinyougou.content.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbContentExample;
import com.pinyougou.pojo.TbContentExample.Criteria;

import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;

	@Autowired
	private RedisTemplate redisTemplate;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<TbContent> findByCategoryId(Long CategoryId) {

		List<TbContent> tbContents = (List<TbContent>) redisTemplate.boundHashOps("content").get(CategoryId);
		if(tbContents==null){

			TbContentExample example = new TbContentExample();
			Criteria criteria = example.createCriteria();
			//根据分类id查找
			criteria.andCategoryIdEqualTo(CategoryId);
			//广告状态有效
			criteria.andStatusEqualTo("1");
			//排序
			example.setOrderByClause("sort_order");
			tbContents = contentMapper.selectByExample(example);
			redisTemplate.boundHashOps("content").put(CategoryId,tbContents);
		}else{
			System.out.println("从缓存中获取数据");
		}
		return tbContents;
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {


		//获取插入的广告的分类
		Long categoryId = content.getCategoryId();
		//将对应分类的广告缓存删除
		redisTemplate.boundHashOps("content").delete(categoryId);
		//更新广告数据
		contentMapper.insert(content);
		/*增加到缓存中的数据，不知道增加的是什么分类的，可以获取分类，然后清楚对应的缓存*/
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){
		//获取该广告原先的分类id
		Long oldCategoryId = contentMapper.selectByPrimaryKey(content.getId()).getCategoryId();
		redisTemplate.boundHashOps("content").delete(oldCategoryId);
		//当前的广告分类id
		Long newCategoryId = content.getCategoryId();
		if (newCategoryId.longValue()!=oldCategoryId.longValue()){
			redisTemplate.boundHashOps("content").delete(newCategoryId);
		}
		contentMapper.updateByPrimaryKey(content);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			Long categoryId = contentMapper.selectByPrimaryKey(id).getCategoryId();
			redisTemplate.boundHashOps("content").delete(categoryId);
			contentMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	
}
