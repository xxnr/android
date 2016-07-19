package com.ksfc.newfarmer.beans.dbbeans;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;

@Entity()
public class InviteeEntity implements Serializable {
    @Id
    public String userId;
    public String account;
    public String name;
    public String dateinvited;
    public boolean sex;
    public int newOrdersNumber;
    public String namePinyin;
    public String nameInitial;
    public String getNameInitial() {
        return this.nameInitial;
    }
    public void setNameInitial(String nameInitial) {
        this.nameInitial = nameInitial;
    }
    public String getNamePinyin() {
        return this.namePinyin;
    }
    public void setNamePinyin(String namePinyin) {
        this.namePinyin = namePinyin;
    }
    public int getNewOrdersNumber() {
        return this.newOrdersNumber;
    }
    public void setNewOrdersNumber(int newOrdersNumber) {
        this.newOrdersNumber = newOrdersNumber;
    }
    public boolean getSex() {
        return this.sex;
    }
    public void setSex(boolean sex) {
        this.sex = sex;
    }
    public String getDateinvited() {
        return this.dateinvited;
    }
    public void setDateinvited(String dateinvited) {
        this.dateinvited = dateinvited;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAccount() {
        return this.account;
    }
    public void setAccount(String account) {
        this.account = account;
    }
    public String getUserId() {
        return this.userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    @Generated(hash = 1277884266)
    public InviteeEntity(String userId, String account, String name,
            String dateinvited, boolean sex, int newOrdersNumber,
            String namePinyin, String nameInitial) {
        this.userId = userId;
        this.account = account;
        this.name = name;
        this.dateinvited = dateinvited;
        this.sex = sex;
        this.newOrdersNumber = newOrdersNumber;
        this.namePinyin = namePinyin;
        this.nameInitial = nameInitial;
    }
    @Generated(hash = 1425767472)
    public InviteeEntity() {
    }


}