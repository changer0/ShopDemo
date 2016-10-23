package com.lulu.shopdemo.model;

/**
 * Created by Lulu on 2016/10/22.
 */

public class CartItem {

    private long productId;//商品ID
    private String productName;
    private String productIcon;
    private float productPrice;
    private int count;

    // TODO: 2016/10/22 CheckBox的选择是否要做在这儿
    private boolean isChecked;//购物车中是否选中某一个商品

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductIcon() {
        return productIcon;
    }

    public void setProductIcon(String productIcon) {
        this.productIcon = productIcon;
    }

    public float getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(float productPrice) {
        this.productPrice = productPrice;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
