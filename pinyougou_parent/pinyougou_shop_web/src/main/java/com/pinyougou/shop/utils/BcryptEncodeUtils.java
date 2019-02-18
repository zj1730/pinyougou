package com.pinyougou.shop.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptEncodeUtils {

    public static void main(String[] args) {
        String password="123456";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode = encoder.encode(password);
        System.out.println(encode);
    }
}
