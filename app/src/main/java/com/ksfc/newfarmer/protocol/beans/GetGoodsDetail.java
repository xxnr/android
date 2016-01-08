package com.ksfc.newfarmer.protocol.beans;

import com.ksfc.newfarmer.protocol.ResponseResult;

import java.io.Serializable;
import java.util.List;

public class GetGoodsDetail extends ResponseResult implements Serializable {
    public GoodsDetail datas;

    public class GoodsDetail implements Serializable {

        public Boolean istop;
        public String deposit;
        public String datecreated;
        public String app_standard_url;
        public String brandName;
        public Boolean online;
        public String id;
        public String app_support_url;
        public String originalUrl;
        public String _id;
        public String description;
        public String name;
        public String linker;
        public String categoryId;
        public String __v;
        public String discountPrice;
        public String discount;
        public String imgUrl;
        public String category;
        public String thumbnail;
        public String price;
        public boolean presale;
        public String payWithScoresLimit;
        public String app_body_url;
        public List<Pictures> pictures;

        public class Pictures implements Serializable {

            public String imgUrl;
            public String originalUrl;
            public String thumbnail;

        }

        public SKUPrice SKUPrice;

        public class SKUPrice implements Serializable {

            public String min;
            public int max;


        }

        public List<SKUAdditions> SKUAdditions;

        public class SKUAdditions implements Serializable {

            public String ref;
            public String price;
            public String _id;
            public String name;

        }

        public List<SKUAttributes> SKUAttributes;

        public class SKUAttributes implements Serializable {

            public String _id;
            public String name;
            public List<String> values;
        }

        public Brand brand;

        public class Brand implements Serializable {

            public String _id;
            public String name;
            public String __v;
        }

        public List<Attributes> attributes;

        public class Attributes implements Serializable {

            public String ref;
            public String _id;
            public String name;
            public String value;


        }


    }


}