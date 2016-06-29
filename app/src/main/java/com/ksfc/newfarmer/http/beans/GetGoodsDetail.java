package com.ksfc.newfarmer.http.beans;

import com.ksfc.newfarmer.http.ResponseResult;

import java.io.Serializable;
import java.util.List;

public class GetGoodsDetail extends ResponseResult implements Serializable {
    public GoodsDetail datas;

    public static class GoodsDetail implements Serializable {

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
        public ReferencePrice referencePrice;


        public static class Pictures implements Serializable {

            public String imgUrl;
            public String originalUrl;
            public String thumbnail;

        }

        public static class ReferencePrice implements Serializable {
            public String min;
            public String max;
        }

        public SKUPrice SKUPrice;

        public SKUMarketPrice SKUMarketPrice;

        public static class SKUPrice implements Serializable {

            public String min;
            public String max;


        }

        public static class SKUMarketPrice implements Serializable {
            public String min;
            public String max;
        }

        public List<SKUAdditions> SKUAdditions;//附加选项

        public static class SKUAdditions implements Serializable {

            public String ref;
            public String price;
            public String _id;
            public String name;

        }

        public List<SKUAttributes> SKUAttributes;//商品下的sku属性

        public static class SKUAttributes implements Serializable {

            public String _id;
            public String name;
            public List<String> values;
        }

        public Brand brand;

        public static class Brand implements Serializable {

            public String _id;
            public String name;
            public String __v;
        }

        public List<Attributes> attributes;//商品分类属性

        public static class Attributes implements Serializable {
            public String ref;
            public String _id;
            public String name;
            public String value;
        }


    }


}