package com.ksfc.newfarmer.beans;

import com.ksfc.newfarmer.protocol.ResponseResult;


/**
 * Created by HePeng on 2016/3/7.
 */
public class RSCInfoResult extends ResponseResult {


    /**
     * companyAddress : {"city":{"_id":"5649bd6c8eba3c20360afabf","provinceid":"58054e5ba551445","uppername":"信陽","name":"信阳","id":"34484b1e894634b","tid":"411500"},"county":{"_id":"5649bd6e8eba3c20360b01be","provinceid":"58054e5ba551445","cityid":"34484b1e894634b","uppername":"息縣","name":"息县","id":"bdc467b26eb1129","tid":"411528"},"details":"出门右拐","town":{"_id":"5666f0a6f6b0560c11733972","id":"9f7171ddf3","tid":"411528101","name":"包信镇","chinesepinyin":"bao xin zhen","countyid":"bdc467b26eb1129","cityid":"34484b1e894634b","provinceid":"58054e5ba551445"},"province":{"_id":"5649bd6c8eba3c20360afa0a","shortname":"H","uppername":"河南","name":"河南","id":"58054e5ba551445","tid":"410000"}}
     * products : []
     * companyName : 旗舰店
     * phone : 13271788771
     * IDNo : 411528199203150095
     * name : 何鹏
     */

    public RSCInfoEntity RSCInfo;


    public static class RSCInfoEntity {
        /**
         * city : {"_id":"5649bd6c8eba3c20360afabf","provinceid":"58054e5ba551445","uppername":"信陽","name":"信阳","id":"34484b1e894634b","tid":"411500"}
         * county : {"_id":"5649bd6e8eba3c20360b01be","provinceid":"58054e5ba551445","cityid":"34484b1e894634b","uppername":"息縣","name":"息县","id":"bdc467b26eb1129","tid":"411528"}
         * details : 出门右拐
         * town : {"_id":"5666f0a6f6b0560c11733972","id":"9f7171ddf3","tid":"411528101","name":"包信镇","chinesepinyin":"bao xin zhen","countyid":"bdc467b26eb1129","cityid":"34484b1e894634b","provinceid":"58054e5ba551445"}
         * province : {"_id":"5649bd6c8eba3c20360afa0a","shortname":"H","uppername":"河南","name":"河南","id":"58054e5ba551445","tid":"410000"}
         */

        public CompanyAddressEntity companyAddress;
        public String companyName;
        public String phone;
        public String IDNo;
        public String name;




        public static class CompanyAddressEntity {
            /**
             * _id : 5649bd6c8eba3c20360afabf
             * provinceid : 58054e5ba551445
             * uppername : 信陽
             * name : 信阳
             * id : 34484b1e894634b
             * tid : 411500
             */

            public CityEntity city;
            /**
             * _id : 5649bd6e8eba3c20360b01be
             * provinceid : 58054e5ba551445
             * cityid : 34484b1e894634b
             * uppername : 息縣
             * name : 息县
             * id : bdc467b26eb1129
             * tid : 411528
             */

            public CountyEntity county;
            public String details;
            /**
             * _id : 5666f0a6f6b0560c11733972
             * id : 9f7171ddf3
             * tid : 411528101
             * name : 包信镇
             * chinesepinyin : bao xin zhen
             * countyid : bdc467b26eb1129
             * cityid : 34484b1e894634b
             * provinceid : 58054e5ba551445
             */

            public TownEntity town;
            /**
             * _id : 5649bd6c8eba3c20360afa0a
             * shortname : H
             * uppername : 河南
             * name : 河南
             * id : 58054e5ba551445
             * tid : 410000
             */

            public ProvinceEntity province;



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

            public static class ProvinceEntity {
                public String _id;
                public String shortname;
                public String uppername;
                public String name;
                public String id;
                public String tid;


            }
        }
    }
}
