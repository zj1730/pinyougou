package com.pinyougou.manager.controller;
import java.util.List;

import com.alibaba.fastjson.JSON;
//import com.pinyougou.page.service.PageService;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import entity.Result;

import javax.jms.*;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;

	@Reference
	private ItemService itemService;

//	@Reference(timeout = 5000)
//	private PageService pageService;

	@Autowired
	private JmsTemplate jmsTemplate;

	@Autowired
	private Destination solrAddDestination;

	@Autowired
	private Destination solrDelDestination;

	@Autowired
	private Destination pageGenDestination;

	@Autowired
	private Destination pageDelDestination;

	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		try {
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(final Long [] ids){
		try {
			goodsService.delete(ids);
			//删除索引库对应内容
			jmsTemplate.send(solrDelDestination, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});
//            itemSearchService.deleteByIds(ids);
			//删除对应静态网页
			jmsTemplate.send(pageDelDestination, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});


			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param goods
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		return goodsService.findPage(goods, page, rows);		
	}

	@RequestMapping("/updateStatus")
    public Result updateStatus(Long[] ids ,String state){
        try {
            goodsService.updateStatus(ids,state);
            if ("1".equals(state)){
                //查询所有的审核成功商品，更新到索引库
                for ( final Long id : ids) {
                	//给队列发送消息
                    List<TbItem> items = itemService.findByGoodsIdAndStatus(id, state);
                    if (items.size()>0){
						final String itemsStr = JSON.toJSONString(items);
						jmsTemplate.send(solrAddDestination, new MessageCreator() {
							@Override
							public Message createMessage(Session session) throws JMSException {
								return session.createTextMessage(itemsStr);
							}
						});
					}


//                    itemSearchService.importList(items);
                    //给静态页面生成服务发送消息
					jmsTemplate.send(pageGenDestination, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(id+"");
						}
					});
//					boolean flag = pageService.getItemHtml(id);
//					System.out.println("生成文件："+flag);
                }
            }

            return new Result(true,"修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

    /*@RequestMapping("/getHtml")
	public Result getHtml(Long goodsId){
		//生成html文件
		boolean flag = pageService.getItemHtml(goodsId);
		System.out.println("生成文件："+flag);
		return new Result(flag,"生成文件");
	}*/
	
}
