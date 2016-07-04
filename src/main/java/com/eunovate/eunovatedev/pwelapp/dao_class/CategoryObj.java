package com.eunovate.eunovatedev.pwelapp.dao_class;

/**
 * Created by EunovateDev on 5/18/2016.
 */
public class CategoryObj{
    int category_id;
    int sub_category_id;
    String cat_desc;
    String subcat_desc;

    public String getSubcat_desc() {
        return subcat_desc;
    }

    public void setSubcat_desc(String subcat_desc) {
        this.subcat_desc = subcat_desc;
    }

    public String getCat_desc() {
        return cat_desc;
    }

    public void setCat_desc(String cat_desc) {
        this.cat_desc = cat_desc;
    }

    public int getSub_category_id() {
        return sub_category_id;
    }

    public void setSub_category_id(int sub_category_id) {
        this.sub_category_id = sub_category_id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }
}