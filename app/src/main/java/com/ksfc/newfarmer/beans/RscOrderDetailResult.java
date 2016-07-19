package com.ksfc.newfarmer.beans;

import com.ksfc.newfarmer.protocol.ResponseResult;

import java.io.Serializable;
import java.util.List;

/**
 * Created by HePeng on 2016/3/24.
 */
public class RscOrderDetailResult extends ResponseResult {


    /**
     * id : c557ae7a8a
     * totalPrice : 78000.00
     * deposit : 3000.00
     * dateCreated : 2016-03-24T04:38:56.142Z
     * deliveryType : {"type":1,"value":"网点自提"}
     * orderStatus : {"type":2,"value":"付款待审核"}
     * consigneeName : 热破额
     * consigneePhone : 13858757676
     * subOrders : [{"id":"801cef5d6b","price":"3000.00","type":"deposit","payStatus":1},{"id":"2afaf97120","price":"75000.00","type":"balance","payStatus":1}]
     * payment : {"id":"7881348d43","slice":1,"price":3000,"suborderId":"801cef5d6b","payType":3,"_id":"56f36f603bc0e0ae7d4a165e","thirdPartyRecorded":false,"isClosed":false,"payStatus":1,"dateCreated":"2016-03-24T04:38:56.141Z"}
     * SKUList : [{"productId":"4597374de4","price":"78000.00","deposit":"3000.00","productName":"江淮汽车 - 第二代瑞风S3 - 2015款","name":"江淮汽车 - 第二代瑞风S3 - 2015款 - 1.8T 自动（GDI 6DCT） - 豪华型 - 拉菲红","thumbnail":"/images/thumbnail/6C7D8F66/1452167039686gcrtfbt9.jpg?category=6C7D8F66&thumb=true","count":1,"category":"汽车","additions":[],"attributes":[{"name":"变速箱","value":"1.8T 自动（GDI 6DCT）","ref":"56a20bd2a20605f152d32d67","_id":"56a20c11a20605f152d32d91"},{"name":"车型配置","value":"豪华型","ref":"568d66f809747bd064e1ef1b","_id":"56a20c11a20605f152d32d90"},{"ref":"568d66f809747bd064e1ef16","name":"颜色","value":"拉菲红","_id":"56a20c11a20605f152d32d8f"}],"deliverStatus":1}]
     */

    public OrderEntity order;


    public static class OrderEntity {
        public String id;
        public String totalPrice;
        public String deposit;
        public String dateCreated;
        /**
         * type : 1
         * value : 网点自提
         */

        public DeliveryTypeEntity deliveryType;
        /**
         * type : 2
         * value : 付款待审核
         */

        public OrderStatusEntity orderStatus;
        public String consigneeName;
        public String consigneePhone;
        public String consigneeAddress;
        /**
         * id : 7881348d43
         * slice : 1
         * price : 3000
         * suborderId : 801cef5d6b
         * payType : 3
         * _id : 56f36f603bc0e0ae7d4a165e
         * thirdPartyRecorded : false
         * isClosed : false
         * payStatus : 1
         * dateCreated : 2016-03-24T04:38:56.141Z
         */

        public PaymentEntity payment;
        /**
         * id : 801cef5d6b
         * price : 3000.00
         * type : deposit
         * payStatus : 1
         */

        public List<SubOrdersEntity> subOrders;
        /**
         * productId : 4597374de4
         * price : 78000.00
         * deposit : 3000.00
         * productName : 江淮汽车 - 第二代瑞风S3 - 2015款
         * name : 江淮汽车 - 第二代瑞风S3 - 2015款 - 1.8T 自动（GDI 6DCT） - 豪华型 - 拉菲红
         * thumbnail : /images/thumbnail/6C7D8F66/1452167039686gcrtfbt9.jpg?category=6C7D8F66&thumb=true
         * count : 1
         * category : 汽车
         * additions : []
         * attributes : [{"name":"变速箱","value":"1.8T 自动（GDI 6DCT）","ref":"56a20bd2a20605f152d32d67","_id":"56a20c11a20605f152d32d91"},{"name":"车型配置","value":"豪华型","ref":"568d66f809747bd064e1ef1b","_id":"56a20c11a20605f152d32d90"},{"ref":"568d66f809747bd064e1ef16","name":"颜色","value":"拉菲红","_id":"56a20c11a20605f152d32d8f"}]
         * deliverStatus : 1
         */

        public List<SKUListEntity> SKUList;



        public static class DeliveryTypeEntity {
            public int type;
            public String value;


        }

        public static class OrderStatusEntity {
            public int type;
            public String value;


        }

        public static class PaymentEntity {
            public String id;
            public int slice;
            public double price;
            public String suborderId;
            public int payType;
            public String _id;
            public boolean thirdPartyRecorded;
            public boolean isClosed;
            public int payStatus;
            public String dateCreated;


        }

        public static class SubOrdersEntity {
            public String id;
            public String price;
            public String type;
            public String payStatus;


        }

        public static class SKUListEntity {
            public String productId;
            public String price;
            public String deposit;
            public String productName;
            public String name;
            public String ref;
            public String thumbnail;
            public int count;
            public String category;
            public String deliverStatus;
            public List<AdditionsEntity> additions;
            /**
             * name : 变速箱
             * value : 1.8T 自动（GDI 6DCT）
             * ref : 56a20bd2a20605f152d32d67
             * _id : 56a20c11a20605f152d32d91
             */

            public List<AttributesEntity> attributes;



            public static class AttributesEntity {
                public String name;
                public String value;
                public String ref;
                public String _id;

            }

            public static class AdditionsEntity implements Serializable {
                public float price;//附加选项价格
                public String name;// 附加选项名称
                public String ref;//附加选项_id
                public String _id;//附加选项_id
            }
        }
    }
}
