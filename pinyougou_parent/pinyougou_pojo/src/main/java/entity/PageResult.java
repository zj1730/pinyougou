package entity;

import com.pinyougou.pojo.TbBrand;

import java.io.Serializable;
import java.util.List;

public class PageResult implements Serializable {

    private List rows;
    private long total;

    public PageResult(long total,List rows){
        this.rows=rows;
        this.total=total;
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
