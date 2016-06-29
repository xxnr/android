package com.ksfc.newfarmer.http.beans;

import com.ksfc.newfarmer.http.ResponseResult;

import java.util.List;

/**
 * Created by CAI on 2016/4/13.
 */
public class MyInviterResult extends ResponseResult {


    /**
     * inviterId : 8b0acb23dc0d41248595ed68a42d339a
     * inviterPhone : 18211101020
     * inviterPhoto : /images/original/14594882243331butyb9.jpg
     * inviterNickname : 哒哒哒哒
     * inviterName : 哈哈哈
     * inviterSex : true
     * inviterAddress : {"province":{"_id":"5649bd6c8eba3c20360afa0a","shortname":"H","uppername":"河南","name":"河南","id":"58054e5ba551445","tid":"410000"},"city":{"_id":"5649bd6c8eba3c20360afab2","provinceid":"58054e5ba551445","uppername":"洛陽","name":"洛阳","id":"d0168e42659b31b","tid":"410300"},"county":{"_id":"5649bd6e8eba3c20360b014b","provinceid":"58054e5ba551445","cityid":"d0168e42659b31b","uppername":"老城","name":"老城","id":"39bbfbad823742d","tid":"410302"},"town":{"_id":"5666f087f6b0560c1173328e","id":"24781b7c74","tid":"410302001","name":"西关街道","chinesepinyin":"xi guan jie dao","countyid":"39bbfbad823742d","cityid":"d0168e42659b31b","provinceid":"58054e5ba551445"}}
     * inviterUserType : 6
     * inviterUserTypeInName : 新农经纪人
     * inviterVerifiedTypes : ["6"]
     * inviterVerifiedTypesInJson : [{"typeId":"6","typeName":"新农经纪人"}]
     */

    public DatasEntity datas;


    public static class DatasEntity {
        public String inviterId;
        public String inviterPhone;
        public String inviterPhoto;
        public String inviterNickname;
        public String inviterName;
        public boolean inviterSex;
        public boolean inviterIsVerified;

        /**
         * province : {"_id":"5649bd6c8eba3c20360afa0a","shortname":"H","uppername":"河南","name":"河南","id":"58054e5ba551445","tid":"410000"}
         * city : {"_id":"5649bd6c8eba3c20360afab2","provinceid":"58054e5ba551445","uppername":"洛陽","name":"洛阳","id":"d0168e42659b31b","tid":"410300"}
         * county : {"_id":"5649bd6e8eba3c20360b014b","provinceid":"58054e5ba551445","cityid":"d0168e42659b31b","uppername":"老城","name":"老城","id":"39bbfbad823742d","tid":"410302"}
         * town : {"_id":"5666f087f6b0560c1173328e","id":"24781b7c74","tid":"410302001","name":"西关街道","chinesepinyin":"xi guan jie dao","countyid":"39bbfbad823742d","cityid":"d0168e42659b31b","provinceid":"58054e5ba551445"}
         */

        public InviterAddressEntity inviterAddress;
        public String inviterUserType;
        public String inviterUserTypeInName;
        public List<String> inviterVerifiedTypes;
        /**
         * typeId : 6
         * typeName : 新农经纪人
         */

        public List<InviterVerifiedTypesInJsonEntity> inviterVerifiedTypesInJson;


        public static class InviterAddressEntity {
            /**
             * _id : 5649bd6c8eba3c20360afa0a
             * shortname : H
             * uppername : 河南
             * name : 河南
             * id : 58054e5ba551445
             * tid : 410000
             */

            public ProvinceEntity province;
            /**
             * _id : 5649bd6c8eba3c20360afab2
             * provinceid : 58054e5ba551445
             * uppername : 洛陽
             * name : 洛阳
             * id : d0168e42659b31b
             * tid : 410300
             */

            public CityEntity city;
            /**
             * _id : 5649bd6e8eba3c20360b014b
             * provinceid : 58054e5ba551445
             * cityid : d0168e42659b31b
             * uppername : 老城
             * name : 老城
             * id : 39bbfbad823742d
             * tid : 410302
             */

            public CountyEntity county;
            /**
             * _id : 5666f087f6b0560c1173328e
             * id : 24781b7c74
             * tid : 410302001
             * name : 西关街道
             * chinesepinyin : xi guan jie dao
             * countyid : 39bbfbad823742d
             * cityid : d0168e42659b31b
             * provinceid : 58054e5ba551445
             */

            public TownEntity town;


            public static class ProvinceEntity {
                public String _id;
                public String shortname;
                public String uppername;
                public String name;
                public String id;
                public String tid;


            }

            public static class CityEntity {
                public String _id;
                public String provinceid;
                public String uppername;
                public String name;
                public String id;
                public String tid;


            }

            public static class CountyEntity {
                public String _id;
                public String provinceid;
                public String cityid;
                public String uppername;
                public String name;
                public String id;
                public String tid;


            }

            public static class TownEntity {
                public String _id;
                public String id;
                public String tid;
                public String name;
                public String chinesepinyin;
                public String countyid;
                public String cityid;
                public String provinceid;


            }
        }

        public static class InviterVerifiedTypesInJsonEntity {
            public String typeId;
            public String typeName;


        }
    }
}
