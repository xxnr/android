/**
 *
 */
package com.ksfc.newfarmer.beans;

import java.io.Serializable;
import java.util.List;

import com.ksfc.newfarmer.protocol.ResponseResult;

/**
 * 项目名称：newFarmer 类名称：GetshopCart 类描述： 创建人：尚前琛 创建时间：2015-7-3 下午9:38:48 修改备注：
 */
public class GetshopCart extends ResponseResult implements Serializable{
    public ShopcartData datas;

    public static class ShopcartData implements Serializable {
        public int total;
        public String shopCartId;
        public int DiscountPrice;
        public String locationUserId;
        public long totalPrice;
        public List<shopCart> rows;
        public int totalCount;
        public int offlineEntryCount;
    }

    public static class shopCart implements Serializable{
        public String brandName;
        public int offlineEntryCount;
        public List<SKU> SKUList;
    }

    public static class SKU implements Serializable{
        public String goodsId;
        public String _id;
        public String price;
        public String imgUrl;
        public String productDesc;
        public String point;
        public String name;
        public String productName;
        public String deposit;
        public String count;
        public List<Attributes> attributes;
        public List<Additions> additions;
        public boolean online;
        public String product_id;

        public static class Attributes implements Serializable{
            public String order;
            public String value;
            public String name;
            public String ref;
            public String _id;

        }

        public static class Additions implements Serializable {
            public String _id;
            public String category;
            public String brand;
            public String name;
            public double price;
            public String __v;

        }
    }
}
