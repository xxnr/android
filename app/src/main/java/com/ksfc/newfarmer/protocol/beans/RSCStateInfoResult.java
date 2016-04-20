package com.ksfc.newfarmer.protocol.beans;

import com.ksfc.newfarmer.protocol.ResponseResult;

import java.io.Serializable;
import java.util.List;

/**
 * Created by HePeng on 2016/3/16.
 */
public class RSCStateInfoResult extends ResponseResult implements Serializable {


    /**
     * RSCs : [{"_id":"5649bd6f8eba3c20360b0765","RSCInfo":{"name":"鲁琲","phone":"15110102070","companyName":"代做毕设","companyAddress":{"province":{"_id":"5649bd6c8eba3c20360afa0a","shortname":"H","uppername":"河南","name":"河南","id":"58054e5ba551445","__v":0,"tid":"410000"},"town":{"_id":"5666f0a6f6b0560c11733972","id":"9f7171ddf3","tid":"411528101","name":"包信镇","chinesepinyin":"bao xin zhen","countyid":"bdc467b26eb1129","cityid":"34484b1e894634b","provinceid":"58054e5ba551445","__v":0},"details":"出门右拐","county":{"_id":"5649bd6e8eba3c20360b01be","provinceid":"58054e5ba551445","cityid":"34484b1e894634b","uppername":"息縣","name":"息县","id":"bdc467b26eb1129","__v":0,"tid":"411528"},"city":{"_id":"5649bd6c8eba3c20360afabf","provinceid":"58054e5ba551445","uppername":"信陽","name":"信阳","id":"34484b1e894634b","__v":0,"tid":"411500"}}}}]
     * count : 1
     * pageCount : 1
     */

    public int count;
    public int pageCount;
    /**
     * _id : 5649bd6f8eba3c20360b0765
     * RSCInfo : {"name":"鲁琲","phone":"15110102070","companyName":"代做毕设","companyAddress":{"province":{"_id":"5649bd6c8eba3c20360afa0a","shortname":"H","uppername":"河南","name":"河南","id":"58054e5ba551445","__v":0,"tid":"410000"},"town":{"_id":"5666f0a6f6b0560c11733972","id":"9f7171ddf3","tid":"411528101","name":"包信镇","chinesepinyin":"bao xin zhen","countyid":"bdc467b26eb1129","cityid":"34484b1e894634b","provinceid":"58054e5ba551445","__v":0},"details":"出门右拐","county":{"_id":"5649bd6e8eba3c20360b01be","provinceid":"58054e5ba551445","cityid":"34484b1e894634b","uppername":"息縣","name":"息县","id":"bdc467b26eb1129","__v":0,"tid":"411528"},"city":{"_id":"5649bd6c8eba3c20360afabf","provinceid":"58054e5ba551445","uppername":"信陽","name":"信阳","id":"34484b1e894634b","__v":0,"tid":"411500"}}}
     */

    public List<RSCsEntity> RSCs;



    public static class RSCsEntity implements Serializable {
        public String _id;
        /**
         * name : 鲁琲
         * phone : 15110102070
         * companyName : 代做毕设
         * companyAddress : {"province":{"_id":"5649bd6c8eba3c20360afa0a","shortname":"H","uppername":"河南","name":"河南","id":"58054e5ba551445","__v":0,"tid":"410000"},"town":{"_id":"5666f0a6f6b0560c11733972","id":"9f7171ddf3","tid":"411528101","name":"包信镇","chinesepinyin":"bao xin zhen","countyid":"bdc467b26eb1129","cityid":"34484b1e894634b","provinceid":"58054e5ba551445","__v":0},"details":"出门右拐","county":{"_id":"5649bd6e8eba3c20360b01be","provinceid":"58054e5ba551445","cityid":"34484b1e894634b","uppername":"息縣","name":"息县","id":"bdc467b26eb1129","__v":0,"tid":"411528"},"city":{"_id":"5649bd6c8eba3c20360afabf","provinceid":"58054e5ba551445","uppername":"信陽","name":"信阳","id":"34484b1e894634b","__v":0,"tid":"411500"}}
         */

        public RSCInfoEntity RSCInfo;



        public static class RSCInfoEntity implements Serializable {
            public String name;
            public String phone;
            public String companyName;
            /**
             * province : {"_id":"5649bd6c8eba3c20360afa0a","shortname":"H","uppername":"河南","name":"河南","id":"58054e5ba551445","__v":0,"tid":"410000"}
             * town : {"_id":"5666f0a6f6b0560c11733972","id":"9f7171ddf3","tid":"411528101","name":"包信镇","chinesepinyin":"bao xin zhen","countyid":"bdc467b26eb1129","cityid":"34484b1e894634b","provinceid":"58054e5ba551445","__v":0}
             * details : 出门右拐
             * county : {"_id":"5649bd6e8eba3c20360b01be","provinceid":"58054e5ba551445","cityid":"34484b1e894634b","uppername":"息縣","name":"息县","id":"bdc467b26eb1129","__v":0,"tid":"411528"}
             * city : {"_id":"5649bd6c8eba3c20360afabf","provinceid":"58054e5ba551445","uppername":"信陽","name":"信阳","id":"34484b1e894634b","__v":0,"tid":"411500"}
             */

            public CompanyAddressEntity companyAddress;



            public static class CompanyAddressEntity implements Serializable {
                /**
                 * _id : 5649bd6c8eba3c20360afa0a
                 * shortname : H
                 * uppername : 河南
                 * name : 河南
                 * id : 58054e5ba551445
                 * __v : 0
                 * tid : 410000
                 */

                public ProvinceEntity province;
                /**
                 * _id : 5666f0a6f6b0560c11733972
                 * id : 9f7171ddf3
                 * tid : 411528101
                 * name : 包信镇
                 * chinesepinyin : bao xin zhen
                 * countyid : bdc467b26eb1129
                 * cityid : 34484b1e894634b
                 * provinceid : 58054e5ba551445
                 * __v : 0
                 */

                public TownEntity town;
                public String details;
                /**
                 * _id : 5649bd6e8eba3c20360b01be
                 * provinceid : 58054e5ba551445
                 * cityid : 34484b1e894634b
                 * uppername : 息縣
                 * name : 息县
                 * id : bdc467b26eb1129
                 * __v : 0
                 * tid : 411528
                 */

                public CountyEntity county;
                /**
                 * _id : 5649bd6c8eba3c20360afabf
                 * provinceid : 58054e5ba551445
                 * uppername : 信陽
                 * name : 信阳
                 * id : 34484b1e894634b
                 * __v : 0
                 * tid : 411500
                 */

                public CityEntity city;



                public static class ProvinceEntity  implements Serializable{
                    public String _id;
                    public String shortname;
                    public String uppername;
                    public String name;
                    public String id;
                    public int __v;
                    public String tid;

                }

                public static class TownEntity  implements Serializable{
                    public String _id;
                    public String id;
                    public String tid;
                    public String name;
                    public String chinesepinyin;
                    public String countyid;
                    public String cityid;
                    public String provinceid;
                    public int __v;

                }

                public static class CountyEntity  implements Serializable{
                    public String _id;
                    public String provinceid;
                    public String cityid;
                    public String uppername;
                    public String name;
                    public String id;
                    public int __v;
                    public String tid;

                }

                public static class CityEntity implements Serializable {
                    public String _id;
                    public String provinceid;
                    public String uppername;
                    public String name;
                    public String id;
                    public int __v;
                    public String tid;

                }
            }
        }
    }
}
