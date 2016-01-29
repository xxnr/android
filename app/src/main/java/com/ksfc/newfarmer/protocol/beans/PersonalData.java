/**
 *
 */
package com.ksfc.newfarmer.protocol.beans;

import java.io.Serializable;

import com.ksfc.newfarmer.protocol.ResponseResult;

/**
 * 项目名称：newFarmer 类名称：PersonalData 类描述： 创建人：尚前琛 创建时间：2015-7-24 下午6:28:38 修改备注：
 */
public class PersonalData extends ResponseResult implements Serializable {
    public Data datas;

    public static class Data implements Serializable {
        public String userid;
        public String nickname;
        public String loginName;
        public String phone;
        public String photo;
        public String inviterNickname;
        public String userTypeInName;//用户类型
        public String inviter;
        public String inviterName;
        public String userType;
        public boolean isVerified;
        public String pointLaterTrade;
        public String name;
        public boolean sex;
        public Address defaultAddress;
        public float score;
        public UserAddress address;

        public static class UserAddress implements Serializable {

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

        public static class Address implements Serializable {
            public String country;
            public String address;
        }


        public String getImageUrl() {
            return this.photo;
        }
    }
}
