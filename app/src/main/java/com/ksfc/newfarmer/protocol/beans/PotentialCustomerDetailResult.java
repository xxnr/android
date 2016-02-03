package com.ksfc.newfarmer.protocol.beans;

import com.google.gson.Gson;
import com.ksfc.newfarmer.protocol.ResponseResult;

import java.util.List;

/**
 * Created by CAI on 2016/2/2.
 */
public class PotentialCustomerDetailResult extends ResponseResult {


    /**
     * _id : 56b07f9025228d9a3e2c4a3d
     * user : {"name":"你好"}
     * name : 秀秀
     * phone : 13246464645
     * isRegistered : false
     * buyIntentions : [{"_id":"56a72696c1813a7c109138ca","name":"瑞风M5"},{"_id":"56a72696c1813a7c109138c8","name":"瑞风M2"},{"_id":"56a72696c1813a7c109138c9","name":"瑞风M3"}]
     * address : {"province":{"shortname":"H","uppername":"河南","name":"河南","id":"58054e5ba551445","tid":"410000"},"county":{"provinceid":"58054e5ba551445","cityid":"24182401c85c496","uppername":"滑縣","name":"滑县","id":"20d6cd0ef90c839","tid":"410526"},"town":{"id":"a56884fc39","tid":"410526105","name":"牛屯镇","chinesepinyin":"niu tun zhen","countyid":"20d6cd0ef90c839","cityid":"24182401c85c496","provinceid":"58054e5ba551445"},"city":{"provinceid":"58054e5ba551445","uppername":"安陽","name":"安阳","id":"24182401c85c496","tid":"410500"}}
     * sex : true
     */

    public PotentialCustomerEntity potentialCustomer;

    public static class PotentialCustomerEntity {
        public String _id;
        /**
         * name : 你好
         */

        public UserEntity user;
        public String name;
        public String phone;
        public boolean isRegistered;
        /**
         * province : {"shortname":"H","uppername":"河南","name":"河南","id":"58054e5ba551445","tid":"410000"}
         * county : {"provinceid":"58054e5ba551445","cityid":"24182401c85c496","uppername":"滑縣","name":"滑县","id":"20d6cd0ef90c839","tid":"410526"}
         * town : {"id":"a56884fc39","tid":"410526105","name":"牛屯镇","chinesepinyin":"niu tun zhen","countyid":"20d6cd0ef90c839","cityid":"24182401c85c496","provinceid":"58054e5ba551445"}
         * city : {"provinceid":"58054e5ba551445","uppername":"安陽","name":"安阳","id":"24182401c85c496","tid":"410500"}
         */

        public AddressEntity address;
        public boolean sex;
        /**
         * _id : 56a72696c1813a7c109138ca
         * name : 瑞风M5
         */

        public List<BuyIntentionsEntity> buyIntentions;



        public static class UserEntity {
            public String name;

        }

        public static class AddressEntity {
            /**
             * shortname : H
             * uppername : 河南
             * name : 河南
             * id : 58054e5ba551445
             * tid : 410000
             */

            public ProvinceEntity province;
            /**
             * provinceid : 58054e5ba551445
             * cityid : 24182401c85c496
             * uppername : 滑縣
             * name : 滑县
             * id : 20d6cd0ef90c839
             * tid : 410526
             */

            public CountyEntity county;
            /**
             * id : a56884fc39
             * tid : 410526105
             * name : 牛屯镇
             * chinesepinyin : niu tun zhen
             * countyid : 20d6cd0ef90c839
             * cityid : 24182401c85c496
             * provinceid : 58054e5ba551445
             */

            public TownEntity town;
            /**
             * provinceid : 58054e5ba551445
             * uppername : 安陽
             * name : 安阳
             * id : 24182401c85c496
             * tid : 410500
             */

            public CityEntity city;



            public static class ProvinceEntity {
                public String shortname;
                public String uppername;
                public String name;
                public String id;
                public String tid;


            }

            public static class CountyEntity {
                public String provinceid;
                public String cityid;
                public String uppername;
                public String name;
                public String id;
                public String tid;


            }

            public static class TownEntity {
                public String id;
                public String tid;
                public String name;
                public String chinesepinyin;
                public String countyid;
                public String cityid;
                public String provinceid;


            }

            public static class CityEntity {
                public String provinceid;
                public String uppername;
                public String name;
                public String id;
                public String tid;


            }
        }

        public static class BuyIntentionsEntity {
            public String _id;
            public String name;
        }
    }
}
