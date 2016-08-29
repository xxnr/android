package com.ksfc.newfarmer.beans;

import com.ksfc.newfarmer.protocol.ResponseResult;

import java.io.Serializable;
import java.util.List;

/**
 * Created by HePeng on 2016/3/24.
 */
public class RscOrderResult extends ResponseResult {


    /**
     * orders : [{"_id":"56f268f4b98a1685726cc13d","price":6000.02,"deliverStatus":4,"id":"411a503bad","consigneeName":"王凯凯","consigneePhone":"13512721873","pendingApprove":false,"deliveryType":1,"subOrders":[{"id":"ef154e0bc5","price":6000.02,"type":"full","_id":"56f268f4b98a1685726cc13f","payStatus":2}],"payments":[{"id":"623219b2d1","slice":1,"price":6000.02,"suborderId":"ef154e0bc5","payType":1,"_id":"56f268f4b98a1685726cc13e","thirdPartyRecorded":false,"isClosed":false,"payStatus":2,"dateCreated":"2016-03-23T09:59:16.322Z","backendUser":"56546caf4ac2dff43638e128","backendUserAccount":"admin","datePaid":"2016-03-23T10:00:46.987Z","dateSet":"2016-03-23T10:00:46.987Z"}],"payStatus":2,"SKUs":[{"ref":"569db7e6f4c59a32277661a4","productId":"18a36cc6e7","price":6000.02,"deposit":0,"productName":"一个很贵的化肥又要很长的名字老要这么长不如写个自动化上架不然PM都要抽筋了嘛","name":"一个很贵的化肥 - 28-11-11 - 40kg","thumbnail":"/images/thumbnail/531680A5/145317671383820cw61or.jpg?category=531680A5&thumb=true","count":1,"category":"化肥","_id":"56f268f4b98a1685726cc140","confirmed":false,"additions":[],"attributes":[{"name":"养分配比","value":"28-11-11","ref":"568d66f809747bd064e1ef13","_id":"56f268f4b98a1685726cc141"}],"deliverStatus":4,"backendUser":"56546caf4ac2dff43638e128","backendUserAccount":"admin","dateDelivered":"2016-03-23T10:00:54.117Z","dateSet":"2016-03-23T10:01:32.441Z","dateRSCReceived":"2016-03-23T10:01:32.440Z"}],"dateCreated":"2016-03-23T09:59:16.322Z","type":{"type":5,"value":"待自提"}}]
     * count : 1
     * pageCount : 1
     */

    public int count;
    public int pageCount;
    /**
     * _id : 56f268f4b98a1685726cc13d
     * price : 6000.02
     * deliverStatus : 4
     * id : 411a503bad
     * consigneeName : 王凯凯
     * consigneePhone : 13512721873
     * pendingApprove : false
     * deliveryType : 1
     * subOrders : [{"id":"ef154e0bc5","price":6000.02,"type":"full","_id":"56f268f4b98a1685726cc13f","payStatus":2}]
     * payments : [{"id":"623219b2d1","slice":1,"price":6000.02,"suborderId":"ef154e0bc5","payType":1,"_id":"56f268f4b98a1685726cc13e","thirdPartyRecorded":false,"isClosed":false,"payStatus":2,"dateCreated":"2016-03-23T09:59:16.322Z","backendUser":"56546caf4ac2dff43638e128","backendUserAccount":"admin","datePaid":"2016-03-23T10:00:46.987Z","dateSet":"2016-03-23T10:00:46.987Z"}]
     * payStatus : 2
     * SKUs : [{"ref":"569db7e6f4c59a32277661a4","productId":"18a36cc6e7","price":6000.02,"deposit":0,"productName":"一个很贵的化肥又要很长的名字老要这么长不如写个自动化上架不然PM都要抽筋了嘛","name":"一个很贵的化肥 - 28-11-11 - 40kg","thumbnail":"/images/thumbnail/531680A5/145317671383820cw61or.jpg?category=531680A5&thumb=true","count":1,"category":"化肥","_id":"56f268f4b98a1685726cc140","confirmed":false,"additions":[],"attributes":[{"name":"养分配比","value":"28-11-11","ref":"568d66f809747bd064e1ef13","_id":"56f268f4b98a1685726cc141"}],"deliverStatus":4,"backendUser":"56546caf4ac2dff43638e128","backendUserAccount":"admin","dateDelivered":"2016-03-23T10:00:54.117Z","dateSet":"2016-03-23T10:01:32.441Z","dateRSCReceived":"2016-03-23T10:01:32.440Z"}]
     * dateCreated : 2016-03-23T09:59:16.322Z
     * type : {"type":5,"value":"待自提"}
     */

    public List<OrdersEntity> orders;

    public static class OrdersEntity {
        public String _id;
        public double price;
        public String id;
        public String consigneeName;
        public String consigneePhone;
        public boolean pendingApprove;
        public String dateCreated;
        public TypeEntity type;
        public List<SKUsEntity> SKUs;

        public DeliveryType deliveryType;


        public static class DeliveryType {
            public int type;
            public String value;
        }



        public static class TypeEntity {
            public int type;
            public String value;

        }


        public static class SKUsEntity {
            public String ref;
            public double price;
            public double deposit;
            public String productName;
            public String name;
            public int count;
            public String category;
            public String _id;
            public String thumbnail;
            public int deliverStatus;
            public List<Additions> additions; //附加选项
            public List<AttributesEntity> attributes;


            public static class AttributesEntity {
                public String name;
                public String value;
                public String ref;
                public String _id;
            }


            public static class Additions implements Serializable {
                public double price;//附加选项价格
                public String name;// 附加选项名称
                public String ref;//附加选项_id
                public String _id;//附加选项_id
            }
        }
    }
}
