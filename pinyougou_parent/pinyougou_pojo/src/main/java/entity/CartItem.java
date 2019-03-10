package entity;

import com.pinyougou.pojo.TbOrderItem;

import java.io.Serializable;
import java.util.List;

public class CartItem implements Serializable {

    private String sellerId;
    private String sellerName;
    private List<TbOrderItem> tbOrderItems;

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public List<TbOrderItem> getTbOrderItems() {
        return tbOrderItems;
    }

    public void setTbOrderItems(List<TbOrderItem> tbOrderItems) {
        this.tbOrderItems = tbOrderItems;
    }
}
