package com.ksfc.newfarmer.beans.dbbeans;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

/**
 * Created by CAI on 2016/7/19.
 */
@Entity
public class OfflineShoppingCart implements Serializable{
    @Id
    public String SKUId;
    public String numbers;
    public String additions;

    public String getNumbers() {
        return this.numbers;
    }

    public void setNumbers(String numbers) {
        this.numbers = numbers;
    }

    public String getSKUId() {
        return this.SKUId;
    }

    public void setSKUId(String SKUId) {
        this.SKUId = SKUId;
    }

    public String getAdditions() {
        return this.additions;
    }

    public void setAdditions(String additions) {
        this.additions = additions;
    }

    @Generated(hash = 100674811)
    public OfflineShoppingCart(String SKUId, String numbers, String additions) {
        this.SKUId = SKUId;
        this.numbers = numbers;
        this.additions = additions;
    }

    @Generated(hash = 1644390105)
    public OfflineShoppingCart() {
    }

}
