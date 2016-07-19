package com.ksfc.newfarmer.beans;

import com.ksfc.newfarmer.protocol.ResponseResult;

import java.io.Serializable;
import java.util.List;

public class AddOrderResult extends ResponseResult {
    public String id;
    public String paymentId;
    public String price;
    public String deposit;
    public List<Orders> orders;


    public static class Orders implements Serializable {
        public String id;
        public double deposit;
        public String price;
        public Payment payment;
        public List<SKUS> SKUs;

        public static class Payment implements Serializable {
            public String paymentId;
            public String price;
        }

        public static class SKUS implements Serializable {
            public String productId; //商品id
            public String name; //SKU名称
            public String count; //商品数量
            public String productName;//商品名称

        }

    }

}
