package com.pinyougou.user.controller;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pinyougou.user.service.UserService;
import com.pinyougou.user.utils.PhoneFormatCheckUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbUser;

import entity.PageResult;
import entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Reference
	private UserService userService;
	
	/**
	 * 增加
	 * @param user
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbUser user,String code){
		if(!StringUtils.isNotEmpty(code)){
			return new Result(false, "验证码不能为空");
		}else{
			if(!userService.checkCode(user.getPhone(),code)){
				return new Result(false, "验证码错误");
			}
		}
		try {
			String password = user.getPassword();
			String md5Password = DigestUtils.md5Hex(password);
			user.setPassword(md5Password);
			user.setUpdated(new Date());
			user.setCreated(new Date());
			userService.add(user);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	@RequestMapping("/getCode")
	public Result getCode(String phone){
		try {
			//手机格式校验
			if(phone==null||"".equals(phone)||!PhoneFormatCheckUtils.isPhoneLegal(phone)){
				return new Result(false,"手机格式不正确");
			}
			userService.getCode(phone);
			return new Result(true,"验证码发送成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"验证码发送失败");
		}
	}

	@RequestMapping("/getName")
	private Map getName(){
		Map map = new HashMap();
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		map.put("username",name);
		return map;
	}

	
}
