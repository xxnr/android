package com.ksfc.newfarmer.beans;

import com.ksfc.newfarmer.protocol.ResponseResult;

import java.util.List;

/**
 * Created by CAI on 2016/6/22.
 */
public class GiftOrderListResult extends ResponseResult {

    /**
     * giftorders : [{"id":"f154d898e3","buyerName":"呵呵","buyerPhone":"13271788771","buyerId":"f4fe7be34b8440ef9a1f8179b5faaad2","deliverStatus":5,"deliveryCode":"5941640","consigneeName":"1233","consigneePhone":"13271788771","points":1,"gift":{"ref":"5768b5b5d2bc1dcc39597f77","id":"577f5816f5","name":"疯狂大甩卖攻城狮洗车一次","category":"车饰精品","thumbnail":"/images/thumbnail/146648003219623issjor.jpg","points":1,"marketPrice":9999,"online":1,"soldout":0},"dateCreated":"2016-06-21T04:22:11.773Z","RSCInfo":{"RSC":"5649bd6f8eba3c20360b0779","RSCAddress":"河南商丘梁园张阁镇蔬果超市南侧50米（张阁镇公交车站下来脸朝东）","companyName":"佳美农资啥都卖旗舰店","RSCPhone":"15201532358"},"deliveryType":1,"backendUser":"56546caf4ac2dff43638e128","backendUserAccount":"admin","dateDelivered":"2016-06-21T09:05:21.751Z","dateSet":"2016-06-21T09:05:21.751Z","dateCompleted":"2016-06-21T09:05:21.751Z","orderStatus":{"type":4,"value":"已完成"}},{"id":"7567d14b90","buyerName":"呵呵","buyerPhone":"13271788771","buyerId":"f4fe7be34b8440ef9a1f8179b5faaad2","deliverStatus":1,"deliveryCode":"2610907","consigneeName":"1233","consigneePhone":"13271788771","points":1,"gift":{"ref":"5768b5b5d2bc1dcc39597f77","id":"577f5816f5","name":"疯狂大甩卖攻城狮洗车一次","category":"车饰精品","thumbnail":"/images/thumbnail/146648003219623issjor.jpg","points":1,"marketPrice":9999,"online":1,"soldout":0},"dateCreated":"2016-06-21T03:36:39.745Z","RSCInfo":{"RSC":"5649bd6f8eba3c20360b0779","RSCAddress":"河南商丘梁园张阁镇蔬果超市南侧50米（张阁镇公交车站下来脸朝东）","companyName":"佳美农资啥都卖旗舰店","RSCPhone":"15201532358"},"deliveryType":1,"orderStatus":{"type":3,"value":"待自提"}},{"id":"9e267d3b1c","buyerName":"呵呵","buyerPhone":"13271788771","buyerId":"f4fe7be34b8440ef9a1f8179b5faaad2","deliverStatus":1,"deliveryCode":"9830341","consigneeName":"1233","consigneePhone":"13271788771","points":1,"gift":{"ref":"5768b5b5d2bc1dcc39597f77","id":"577f5816f5","name":"疯狂大甩卖攻城狮洗车一次","category":"车饰精品","thumbnail":"/images/thumbnail/146648003219623issjor.jpg","points":1,"marketPrice":9999,"online":1,"soldout":0},"dateCreated":"2016-06-21T03:35:42.666Z","RSCInfo":{"RSC":"5649bd6f8eba3c20360b0765","RSCAddress":"河南开封禹王台三里堡街道水中","companyName":"新农江淮化肥鲁排卖身店","RSCPhone":"15110102070"},"deliveryType":1,"orderStatus":{"type":3,"value":"待自提"}},{"id":"3d8345f4d7","buyerName":"呵呵","buyerPhone":"13271788771","buyerId":"f4fe7be34b8440ef9a1f8179b5faaad2","deliverStatus":1,"deliveryCode":"3746223","consigneeName":"1233","consigneePhone":"13271788771","points":1,"gift":{"ref":"5768b5b5d2bc1dcc39597f77","id":"577f5816f5","name":"疯狂大甩卖攻城狮洗车一次","category":"车饰精品","thumbnail":"/images/thumbnail/146648003219623issjor.jpg","points":1,"marketPrice":9999,"online":1,"soldout":0},"dateCreated":"2016-06-21T03:35:39.146Z","RSCInfo":{"RSC":"5649bd6f8eba3c20360b0765","RSCAddress":"河南开封禹王台三里堡街道水中","companyName":"新农江淮化肥鲁排卖身店","RSCPhone":"15110102070"},"deliveryType":1,"orderStatus":{"type":3,"value":"待自提"}},{"id":"304769e8fe","buyerName":"呵呵","buyerPhone":"13271788771","buyerId":"f4fe7be34b8440ef9a1f8179b5faaad2","deliverStatus":1,"deliveryCode":"0708592","consigneeName":"1233","consigneePhone":"13271788771","points":200,"gift":{"ref":"575e40ae94ba82804f4c188f","id":"66477171c0","name":"熊本熊- 可爱萌萌哒的宠物宠物口吃了","category":"精美礼品","thumbnail":"/images/thumbnail/1466152604813lx2sm7vi.jpg","points":200,"marketPrice":60,"online":1,"soldout":0},"dateCreated":"2016-06-20T11:52:18.599Z","RSCInfo":{"RSC":"5649bd6f8eba3c20360b0765","RSCAddress":"河南开封禹王台三里堡街道水中","companyName":"新农江淮化肥鲁排卖身店","RSCPhone":"15110102070"},"deliveryType":1,"orderStatus":{"type":3,"value":"待自提"}}]
     * total : 5
     * pages : 1
     * page : 0
     */

    public DatasBean datas;

    public static class DatasBean {
        public int total;
        public int pages;
        public int page;
        /**
         * id : f154d898e3
         * buyerName : 呵呵
         * buyerPhone : 13271788771
         * buyerId : f4fe7be34b8440ef9a1f8179b5faaad2
         * deliverStatus : 5
         * deliveryCode : 5941640
         * consigneeName : 1233
         * consigneePhone : 13271788771
         * points : 1
         * gift : {"ref":"5768b5b5d2bc1dcc39597f77","id":"577f5816f5","name":"疯狂大甩卖攻城狮洗车一次","category":"车饰精品","thumbnail":"/images/thumbnail/146648003219623issjor.jpg","points":1,"marketPrice":9999,"online":1,"soldout":0}
         * dateCreated : 2016-06-21T04:22:11.773Z
         * RSCInfo : {"RSC":"5649bd6f8eba3c20360b0779","RSCAddress":"河南商丘梁园张阁镇蔬果超市南侧50米（张阁镇公交车站下来脸朝东）","companyName":"佳美农资啥都卖旗舰店","RSCPhone":"15201532358"}
         * deliveryType : 1
         * backendUser : 56546caf4ac2dff43638e128
         * backendUserAccount : admin
         * dateDelivered : 2016-06-21T09:05:21.751Z
         * dateSet : 2016-06-21T09:05:21.751Z
         * dateCompleted : 2016-06-21T09:05:21.751Z
         * orderStatus : {"type":4,"value":"已完成"}
         */

        public List<GiftordersBean> giftorders;

        public static class GiftordersBean {
            public String id;
            public String buyerName;
            public String buyerPhone;
            public String buyerId;
            public int deliverStatus;
            public String deliveryCode;
            public String consigneeName;
            public String consigneePhone;
            public int points;
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
            public String backendUser;
            public String backendUserAccount;
            public String dateDelivered;
            public String dateSet;
            public String dateCompleted;
            /**
             * type : 4
             * value : 已完成
             */

            public OrderStatusBean orderStatus;

            public static class GiftBean {
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

            public static class RSCInfoBean {
                public String RSC;
                public String RSCAddress;
                public String companyName;
                public String RSCPhone;
            }

            public static class OrderStatusBean {
                public int type;
                public String value;
            }
        }
    }
}
