package com.zcx.ymyy.entity;

public class PageQuery {

    private Integer current = 1;
    private Integer limit = 10;

    public PageQuery() {
    }

    public PageQuery(Integer current, Integer limit) {
        setCurrent(current);
        setLimit(limit);
    }

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        if (current < 0) {
            this.current = 0;
        } else {
            this.current = current;
        }
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
       if (limit < 0 || limit > 20) {
           this.limit = 10;
       } else {
           this.limit = limit;
       }
    }

    @Override
    public String toString() {
        return "Page{" +
                "current=" + current +
                ", limit=" + limit +
                '}';
    }
}
