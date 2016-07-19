package com.ksfc.newfarmer.beans.dbbeans;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity()
public class PotentialCustomersEntity {

    @Id
    public String _id;
    public String name;
    public String phone;
    public String nameInitial;
    public String namePinyin;
    public boolean isRegistered;
    public boolean sex;
    public int nameInitialType;
    public int getNameInitialType() {
        return this.nameInitialType;
    }
    public void setNameInitialType(int nameInitialType) {
        this.nameInitialType = nameInitialType;
    }
    public boolean getSex() {
        return this.sex;
    }
    public void setSex(boolean sex) {
        this.sex = sex;
    }
    public boolean getIsRegistered() {
        return this.isRegistered;
    }
    public void setIsRegistered(boolean isRegistered) {
        this.isRegistered = isRegistered;
    }
    public String getNamePinyin() {
        return this.namePinyin;
    }
    public void setNamePinyin(String namePinyin) {
        this.namePinyin = namePinyin;
    }
    public String getNameInitial() {
        return this.nameInitial;
    }
    public void setNameInitial(String nameInitial) {
        this.nameInitial = nameInitial;
    }
    public String getPhone() {
        return this.phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String get_id() {
        return this._id;
    }
    public void set_id(String _id) {
        this._id = _id;
    }
    @Generated(hash = 1342279162)
    public PotentialCustomersEntity(String _id, String name, String phone,
            String nameInitial, String namePinyin, boolean isRegistered,
            boolean sex, int nameInitialType) {
        this._id = _id;
        this.name = name;
        this.phone = phone;
        this.nameInitial = nameInitial;
        this.namePinyin = namePinyin;
        this.isRegistered = isRegistered;
        this.sex = sex;
        this.nameInitialType = nameInitialType;
    }
    @Generated(hash = 796894733)
    public PotentialCustomersEntity() {
    }


}