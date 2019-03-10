package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import entity.CartItem;
import entity.Result;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Arg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import utils.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {



    @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<CartItem> addGoodsToCart(List<CartItem> cartList,Long itemId ,Integer num) {

            //从数据库中查询数据
            TbItem item = tbItemMapper.selectByPrimaryKey(itemId);
            if(item!=null){
                //将数据添加到购物车数据中
                return addItemToCartList(item,num,cartList);
            }else{
                throw new RuntimeException("该商品不存在");
            }

            //将数据转换为Json串
//            String jsonString = JSON.toJSONString(cartList);
            //将数据添加到cookie中
//            CookieUtil.setCookie(request,response,"cartList",jsonString,3600*24,"UTF-8");

    }

    @Override
    public List<CartItem> getCartListFromRedis(String key) {

        List<CartItem> cartItems = (List<CartItem>) redisTemplate.boundHashOps("cartList").get(key);
        if(cartItems==null){
            cartItems = new ArrayList<>();
        }
        System.out.println("从缓存中读取数据");
        return cartItems;
    }

    @Override
    public void addCartListToRedis(String key, List<CartItem> cartList) {

        redisTemplate.boundHashOps("cartList").put(key,cartList);
        System.out.println("添加数据到缓存");
    }

    @Override
    public List<CartItem> MergeCartList(List<CartItem> cartList, List<CartItem> cartList2) {

        //遍历购物车
        for (CartItem cartItem : cartList2) {
            List<TbOrderItem> tbOrderItems = cartItem.getTbOrderItems();
            for (TbOrderItem tbOrderItem : tbOrderItems) {
                addGoodsToCart(cartList,tbOrderItem.getItemId(),tbOrderItem.getNum());
            }
        }
        return cartList;
    }

    /**
     * 将SKU添加到购物车中
     * @param item
     * @param cartList
     * @return
     */
    private List<CartItem> addItemToCartList(TbItem item ,Integer num, List<CartItem> cartList ){


        //购物车列表中获取商家购物车
        CartItem cart = getCartByItem(item,cartList);
        //在购物车中不存在对应商品的商家购物车
        if(cart==null){
            //创建一个新的商家购物车
            cart = new CartItem();
            cart.setSellerId(item.getSellerId());//商家ID
            cart.setSellerName(item.getSeller());//商家名称
            List<TbOrderItem> orderItemList=new ArrayList();//创建购物车明细列表
            TbOrderItem orderItem = createOrderItem(item,num);//创建orderItem
            orderItemList.add(orderItem);
            cart.setTbOrderItems(orderItemList);
            cartList.add(cart);
        }else{
            //根据商品从商家购物车中获取对应的商品
            TbOrderItem orderItem = getOrderItemByItem(item,cart);
            //商家购物车中不存在该商品
            if(orderItem==null){
                //创建一个新的orderItem放入商品列表中
                cart.getTbOrderItems().add(createOrderItem(item,num));
            }else{
                //对应的orderItem中的数量改变
                orderItem.setNum(orderItem.getNum()+num);
                //总价改变
                orderItem.setTotalFee(orderItem.getPrice().multiply(new BigDecimal(orderItem.getNum())));
//                item.getPrice().multiply(new BigDecimal(num))
                //如果数量<=0，就清除对应的商品
                if(orderItem.getNum()<=0){
                    cart.getTbOrderItems().remove(orderItem);
                }
            }

        }
        return cartList;
    }

    private TbOrderItem getOrderItemByItem(TbItem item, CartItem cart) {
        List<TbOrderItem> tbOrderItems = cart.getTbOrderItems();
        for(int j=0;j<tbOrderItems.size();j++){
            TbOrderItem tbOrderItem = tbOrderItems.get(j);
            //存在同样商品
            if (item.getId().longValue()==tbOrderItem.getItemId().longValue()){
               return tbOrderItem;
            }
        }
        return null;
    }

    /**
     * 创建OrderItem对象
     * @param item
     * @param num
     * @return
     */
    private TbOrderItem createOrderItem(TbItem item,Integer num){
        //创建新的购物车明细对象
        TbOrderItem orderItem=new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        orderItem.setTotalFee(item.getPrice().multiply(new BigDecimal(orderItem.getNum())));//item.getPrice().doubleValue()*num)
        return orderItem;
    }

    /**
     * 根据item获取对应的CartItem
     * @param item
     * @return
     */
    private CartItem getCartByItem(TbItem item,List<CartItem> cartList) {
        for(int i=0;i<cartList.size();i++){
            //出现重复的商家id
            if(item.getSellerId().equals(cartList.get(i).getSellerId())){
                return cartList.get(i);
            }
        }
        return null;

    }

   /* private TbOrderItem addItemToOrderItem(TbItem item) {

        TbOrderItem tbOrderItem = new TbOrderItem();
        tbOrderItem.setNum(1);
        tbOrderItem.setGoodsId(item.getGoodsId());
        tbOrderItem.setItemId(item.getId());
        tbOrderItem.setPicPath(item.getImage());
        tbOrderItem.setPrice(item.getPrice());
        tbOrderItem.setSellerId(item.getSellerId());
        tbOrderItem.setTitle(item.getTitle());
        return tbOrderItem;
    }*/


}
