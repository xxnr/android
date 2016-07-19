package com.ksfc.newfarmer.beans;

import com.ksfc.newfarmer.protocol.ResponseResult;

import java.io.Serializable;

public class LoginResult extends ResponseResult {

    public UserInfo datas;
    public String token;

    public static class UserInfo {
        public String userid;
        public String nickname;
        public String loginName;
        public String cartId;
        public String phone;
        public String userType;
        public String photo;
        public boolean isXXNRAgent;//是否是新农经济人
        public boolean sex;// :性别
        public String address;
        public UserAddress userAddress;// :用户默认送货地址
        public boolean isVerified;//是否认证
        public boolean isUserInfoFullFilled;//信息是否完整
        public boolean isRSC;//是否认证为县级经销商
        public boolean RSCInfoVerifing; //是否正在认证县级经销商

        //需要保存的东西
        public String defaultAddress;// :用户默认送货地址
        public String userTypeInName;//：用户类型
        public String token;
        public String shopCarCount;
        public String name;
        public String townid;
        public String provinceid;
        public String cityid;
        public String countyid;
        public String addressCity;// :用户默认送货地址省市区
        public String addressTown;// :用户默认送货地址镇

    }

    public static class UserAddress {
        public Town town;
        public County county;
        public City city;
        public Province province;

        public static class Town implements Serializable {
            public String id;
            public String name;
            public String countyid;
            public String cityid;
            public String provinceid;
        }

        public static class County implements Serializable {
            public String id;
            public String name;
            public String cityid;
            public String provinceid;
        }

        public static class City implements Serializable {
            public String id;
            public String name;
            public String provinceid;
        }

        public static class Province implements Serializable {
            public String id;
            public String name;
        }
    }


}
