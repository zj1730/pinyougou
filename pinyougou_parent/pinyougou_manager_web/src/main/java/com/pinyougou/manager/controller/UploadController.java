package com.pinyougou.manager.controller;

import entity.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import utils.FastDFSClient;

@RestController
public class UploadController {

    //@Value("${FILE_SERVER_URL}")

    private String FILE_SERVER_URL="http://192.168.25.133/";
    @RequestMapping("/upload")
    public Result uploadFile(MultipartFile file) {
        //获取文件扩展名
        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        System.out.println(ext);

        //创建文件上传对象
        try {
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
            //获取文件路径
            String path = FILE_SERVER_URL + fastDFSClient.uploadFile(file.getBytes(), ext);
            return new Result(true, path);


        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "文件上传失败");
        }
    }
}
