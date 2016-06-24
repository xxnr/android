package com.ksfc.newfarmer.protocol.beans;

import com.ksfc.newfarmer.protocol.ResponseResult;

import java.io.Serializable;

/**
 * Created by CAI on 2016/6/21.
 */
public class AddGiftOrderResult extends ResponseResult {

    /**
     * __v : 0
     * id : 7567d14b90
     * buyerName : 呵呵
     * buyerPhone : 13271788771
     * buyerId : f4fe7be34b8440ef9a1f8179b5faaad2
     * deliverStatus : 1
     * deliveryCode : 2610907
     * consigneeName : 1233
     * consigneePhone : 13271788771
     * points : 1
     * _id : 5768b647e88ab9213ab345b0
     * gift : {"ref":"5768b5b5d2bc1dcc39597f77","id":"577f5816f5","name":"疯狂大甩卖攻城狮洗车一次","category":"车饰精品","thumbnail":"/images/thumbnail/146648003219623issjor.jpg","points":1,"marketPrice":9999,"online":1,"soldout":0}
     * dateCreated : 2016-06-21T03:36:39.745Z
     * RSCInfo : {"RSC":"5649bd6f8eba3c20360b0779","RSCAddress":"河南商丘梁园张阁镇蔬果超市南侧50米（张阁镇公交车站下来脸朝东）","companyName":"佳美农资啥都卖旗舰店","RSCPhone":"15201532358"}
     * deliveryType : 1
     */

    public GiftOrderBean giftOrder;

    public static class GiftOrderBean implements Serializable {
        public int __v;
        public String id;
        public String buyerName;
        public String buyerPhone;
        public String buyerId;
        public int deliverStatus;
        public String deliveryCode;
        public String consigneeName;
        public String consigneePhone;
        public int points;
        public String _id;
        /**
         * ref : 5768b5b5d2bc1dcc39597f77
         * id : 577f5816f5
         * name : 疯狂大甩卖攻城狮洗车一次
         * category : 车饰精品
         * thumbnail : /images/thumbnail/146648003219623issjor.jpg
         * points : 1
         * marketPrice : 9999
         * online : 1
         * soldout : 0
         */

        public GiftBean gift;
        public String dateCreated;
        /**
         * RSC : 5649bd6f8eba3c20360b0779
         * RSCAddress : 河南商丘梁园张阁镇蔬果超市南侧50米（张阁镇公交车站下来脸朝东）
         * companyName : 佳美农资啥都卖旗舰店
         * RSCPhone : 15201532358
         */

        public RSCInfoBean RSCInfo;
        public int deliveryType;

        public static class GiftBean implements Serializable {
            public String ref;
            public String id;
            public String name;
            public String category;
            public String thumbnail;
            public int points;
            public float marketPrice;
            public int online;
            public int soldout;
        }

        public static class RSCInfoBean implements Serializable {
            public String RSC;
            public String RSCAddress;
            public String companyName;
            public String RSCPhone;
        }
    }
}
