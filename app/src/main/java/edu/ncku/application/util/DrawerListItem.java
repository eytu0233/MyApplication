package edu.ncku.application.util;

/**
 * 此抽象類別是給DrawerListSelector使用，用來表示點擊的物件
 * ，並且將點擊物件的名稱與點擊事件綁在一起。
 * 只要繼承並實作此類別就能夠加進DrawerList之中，主要是降低
 * DrawerListSelector與點擊事件間的耦合性，提高內聚性。
 */
public abstract class DrawerListItem {

    protected String itemString;

    public DrawerListItem(String itemString) {
        this.itemString = itemString;
    }

    public String getItemString() {
        return itemString;
    }

    abstract public void onDrawerItemClick();

}
