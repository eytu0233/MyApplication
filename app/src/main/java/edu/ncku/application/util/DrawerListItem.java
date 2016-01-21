package edu.ncku.application.util;

/**
 * Created by NCKU on 2016/1/15.
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
