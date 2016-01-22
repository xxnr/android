package com.ksfc.newfarmer.protocol.beans;

import com.ksfc.newfarmer.protocol.ResponseResult;

import java.util.List;

/**
 * Created by hepeng on 2015/12/27.
 */
public class ConsumerOrderResult extends ResponseResult{

    public Datas datas;

    public static class Datas {
        public String account;
        public String nickname;
        public String name;
        public String total;
        public List<Rows> rows;
    }

    public static class Rows{
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

    public static class Product{

        public String deposit;
        public String price;
        public String name;
        public String category;
        public String id;
        public String thumbnail;
        public int count;
        public String deliverStatus;
    }

    public static class SKUS{
        public String productName;
        public int count;
    }
}
