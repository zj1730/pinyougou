package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import entity.CartItem;
import entity.Result;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utils.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    private boolean firstLogin = true;

    @RequestMapping("/findCartList")
    public List<CartItem> findCartList(){
        //获取当前登录用户信息
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //判断用户是否登录
        if (name=="anonymousUser"){
            //置为第一次标志位
            firstLogin=true;
            //从cookie中获取数据
            String cartListStr = CookieUtil.getCookieValue(request, "cartList","UTF-8");
            if (!StringUtils.isNotEmpty(cartListStr)){
                cartListStr="[]";
            }
            return JSON.parseArray(cartListStr, CartItem.class);
        }else{
            List<CartItem> cartList = cartService.getCartListFromRedis(name);
            //第一次登录，从cookie中获取数据，保存到reids中，并情况cookie
            if(firstLogin==true){

                String cartListStr = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
                if (!StringUtils.isNotEmpty(cartListStr)){
                    cartListStr="[]";
                }
                List<CartItem> cartList2 = JSON.parseArray(cartListStr, CartItem.class);
                cartList = cartService.MergeCartList(cartList,cartList2);
                //保存到redis中
                cartService.addCartListToRedis(name,cartList);
                //清空cookie数据
                CookieUtil.setCookie(request,response,"cartList","");
                //设置第一次登录标志位为false
                firstLogin=false;
            }

            return cartList;

        }



    }

    @CrossOrigin(origins = "http://localhost:9105")
    @RequestMapping("/addToCartList")
    public Result addToCartList(Long itemId,Integer num){
        try {

            //获取购物车数据
            List<CartItem> cartList = findCartList();
            //将新增商品添加到购物车（只能一个个商品添加）
            cartList = cartService.addGoodsToCart(cartList, itemId, num);

            //判断用户是否登录
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            //用户没有登录
            if(name=="anonymousUser"){

                //将商品添加购物车中
                //将数据转换为Json串
                String jsonString = JSON.toJSONString(cartList);
                //将数据添加到cookie中
                CookieUtil.setCookie(request,response,"cartList",jsonString,3600*24,"UTF-8");
            }else{
                cartService.addCartListToRedis(name,cartList);
            }

            return new Result(true,"添加购物车成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加购物车失败");
        }
    }
}
