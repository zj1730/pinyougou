package com.pinyougou.pojogroup;

import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbTypeTemplate;

import java.io.Serializable;

public class ItemCat implements Serializable {

    private TbItemCat itemCat;
    private TbTypeTemplate tbTypeTemplate;

    public TbItemCat getItemCat() {
        return itemCat;
    }

    public void setItemCat(TbItemCat itemCat) {
        this.itemCat = itemCat;
    }

    public TbTypeTemplate getTbTypeTemplate() {
        return tbTypeTemplate;
    }

    public void setTbTypeTemplate(TbTypeTemplate tbTypeTemplate) {
        this.tbTypeTemplate = tbTypeTemplate;
    }
}
