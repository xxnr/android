package com.ksfc.newfarmer.beans;

import com.ksfc.newfarmer.protocol.ResponseResult;

import java.util.List;

/**
 * Created by hepeng on 2015/12/27.
 */
public class ConsumerOrderResult extends ResponseResult {

    public Datas datas;

    public static class Datas {
        public String account;
        public String nickname;
        public String name;
        public String total;
        public List<Rows> rows;

        public AddressEntity address;
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


    public static class Rows {
        public int typeValue;
        public String orderId;
        public String totalPrice;
        public String goodsCount;
        public String address;
        public String recipientName;
        public String recipientPhone;
        public String deposit;
        public List<Product> products;
        public List<SKUS> SKUs;
        public String dateCreated;
    }

    public static class Product {

        public String deposit;
        public String price;
        public String name;
        public String category;
        public String id;
        public String thumbnail;
        public int count;
        public String deliverStatus;
    }

    public static class SKUS {
        public String productName;
        public int count;
    }
}
