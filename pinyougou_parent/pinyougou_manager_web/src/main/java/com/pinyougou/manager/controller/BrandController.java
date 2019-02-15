package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.BrandService;
import entity.PageResult;
import com.pinyougou.pojo.TbBrand;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;
    @RequestMapping("/findAll")
    public List<TbBrand> findAll(){

        return brandService.findAll();
    }

    @RequestMapping("/findByPage")
    public PageResult findByPage(int pageNum,int pageSize){

        return brandService.findByPage(pageNum,pageSize);
    }


    @RequestMapping("/add")
    public Result add(@RequestBody TbBrand tbBrand){//数据的封装？

        try {
            brandService.add(tbBrand);
            return new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }

    @RequestMapping("/findOne")
    public TbBrand findOne(Long id){
        return brandService.findOne(id);
    }

    @RequestMapping("/update")
    public Result update(@RequestBody TbBrand tbBrand){//使用ajax传递过来的数据需要使用@RequestBody解析

        //调用service方法
        try {
            brandService.update(tbBrand);
            return new Result(true,"修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            brandService.delete(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

    @RequestMapping("/search")
    public PageResult search(int pageNum,int pageSize,@RequestBody TbBrand tbBrand){
        //调用service方法
        return brandService.search(pageNum,pageSize,tbBrand);
    }
}
