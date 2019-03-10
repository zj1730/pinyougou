package com.pinyougou.cart.service;

import entity.CartItem;

import java.util.List;

public interface CartService {

    /**
     * 添加商品到购物车
     * @param itemId
     * @param num
     * @return
     */
    public List<CartItem> addGoodsToCart(List<CartItem> cartList ,Long itemId,Integer num);


    /**
     * 从redis中获取购物车数据
     * @return
     */
    List<CartItem> getCartListFromRedis(String key);

    /**
     *
     * @param key
     * @param cartList
     */
    void addCartListToRedis(String key, List<CartItem> cartList);

    /**
     * 购物车合并
     * @param cartList
     * @param cartList2
     * @return
     */
    List<CartItem> MergeCartList(List<CartItem> cartList, List<CartItem> cartList2);
}
