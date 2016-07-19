package com.ksfc.newfarmer.beans;

import com.ksfc.newfarmer.protocol.ResponseResult;

import java.util.List;

/**
 * Created by CAI on 2016/7/6.
 */
public class RscGiftOrderListResult extends ResponseResult {

    /**
     * giftorders : [{"id":"e68b77d9ac","buyerName":"凯凯王","buyerPhone":"13512721874","buyerId":"d68322a9c3f5400a8448a5a94a7db3f9","deliverStatus":5,"consigneeName":"高方方","consigneePhone":"13512721874","points":1,"gift":{"ref":"5768b5b5d2bc1dcc39597f77","id":"577f5816f5","name":"疯狂大甩卖攻城狮洗车一次","category":"车饰精品","thumbnail":"/images/thumbnail/146648003219623issjor.jpg","points":1,"marketPrice":9999.99,"online":1,"soldout":0},"dateCreated":"2016-07-01T07:15:03.452Z","RSCInfo":{"RSC":"5649bd6f8eba3c20360b079c","RSCAddress":"河南商丘虞城大杨集镇人民路解放路交界处路南","companyName":"新农人虞城直营店","RSCPhone":"13512721874"},"deliveryType":1,"backendUser":"56546caf4ac2dff43638e128","backendUserAccount":"admin","dateCompleted":"2016-07-01T07:19:14.235Z","dateDelivered":"2016-07-01T07:19:14.235Z","dateSet":"2016-07-01T07:19:14.235Z","orderStatus":{"type":4,"value":"已完成"}},{"id":"b26ae148ae","buyerName":"凯凯王","buyerPhone":"13512721874","buyerId":"d68322a9c3f5400a8448a5a94a7db3f9","deliverStatus":1,"consigneeName":"高方方","consigneePhone":"13512721874","points":16,"gift":{"ref":"576f90e00846ae503917c210","id":"10e3641e3d","name":"看一下上商品的顺序","category":"车饰精品","thumbnail":"/images/thumbnail/1466929366611fuk57b9.jpg","points":16,"marketPrice":0.01,"online":1,"soldout":0},"dateCreated":"2016-07-01T07:13:29.122Z","RSCInfo":{"RSC":"5649bd6f8eba3c20360b079c","RSCAddress":"河南商丘虞城大杨集镇人民路解放路交界处路南","companyName":"新农人虞城直营店","RSCPhone":"13512721874"},"deliveryType":1,"orderStatus":{"type":3,"value":"待自提"}},{"id":"c5684b456b","buyerName":"靳美佳","buyerPhone":"15201532357","buyerId":"a05c6d01efd54beba716b1acdb606524","deliverStatus":1,"consigneeName":"靳美佳还在测","consigneePhone":"15201532357","points":880,"gift":{"ref":"5763ccfc6c6503c264444127","id":"85bbd34a2a","name":"香百年招财猫","category":"车饰精品","thumbnail":"/images/thumbnail/1466158287608df1xajor.jpg","points":880,"marketPrice":0,"online":1,"soldout":0},"dateCreated":"2016-06-27T06:40:55.071Z","RSCInfo":{"RSC":"5649bd6f8eba3c20360b079c","RSCAddress":"河南商丘虞城大杨集镇人民路解放路交界处路南","companyName":"新农人虞城直营店","RSCPhone":"13512721874"},"deliveryType":1,"orderStatus":{"type":3,"value":"待自提"}},{"id":"30e1958464","buyerName":"呵呵","buyerPhone":"13271788771","buyerId":"f4fe7be34b8440ef9a1f8179b5faaad2","deliverStatus":1,"consigneeName":"你家","consigneePhone":"13656432454","points":1,"gift":{"ref":"5768b5b5d2bc1dcc39597f77","id":"577f5816f5","name":"疯狂大甩卖攻城狮洗车一次","category":"车饰精品","thumbnail":"/images/thumbnail/146648003219623issjor.jpg","points":1,"marketPrice":9999.99,"online":1,"soldout":0},"dateCreated":"2016-06-27T03:37:45.729Z","RSCInfo":{"RSC":"5649bd6f8eba3c20360b079c","RSCAddress":"河南商丘虞城大杨集镇人民路解放路交界处路南","companyName":"新农人虞城直营店","RSCPhone":"13512721874"},"deliveryType":1,"orderStatus":{"type":3,"value":"待自提"}},{"id":"6375e1dac3","buyerName":"呵呵","buyerPhone":"13271788771","buyerId":"f4fe7be34b8440ef9a1f8179b5faaad2","deliverStatus":1,"consigneeName":"你家","consigneePhone":"13656432454","points":1,"gift":{"ref":"5768b5b5d2bc1dcc39597f77","id":"577f5816f5","name":"疯狂大甩卖攻城狮洗车一次","category":"车饰精品","thumbnail":"/images/thumbnail/146648003219623issjor.jpg","points":1,"marketPrice":9999.99,"online":1,"soldout":0},"dateCreated":"2016-06-27T03:36:46.126Z","RSCInfo":{"RSC":"5649bd6f8eba3c20360b079c","RSCAddress":"河南商丘虞城大杨集镇人民路解放路交界处路南","companyName":"新农人虞城直营店","RSCPhone":"13512721874"},"deliveryType":1,"orderStatus":{"type":3,"value":"待自提"}},{"id":"c1df70b394","buyerName":"呵呵","buyerPhone":"13271788771","buyerId":"f4fe7be34b8440ef9a1f8179b5faaad2","deliverStatus":1,"consigneeName":"你家","consigneePhone":"13656432454","points":1,"gift":{"ref":"5768b5b5d2bc1dcc39597f77","id":"577f5816f5","name":"疯狂大甩卖攻城狮洗车一次","category":"车饰精品","thumbnail":"/images/thumbnail/146648003219623issjor.jpg","points":1,"marketPrice":9999.99,"online":1,"soldout":0},"dateCreated":"2016-06-27T03:36:25.529Z","RSCInfo":{"RSC":"5649bd6f8eba3c20360b079c","RSCAddress":"河南商丘虞城大杨集镇人民路解放路交界处路南","companyName":"新农人虞城直营店","RSCPhone":"13512721874"},"deliveryType":1,"orderStatus":{"type":3,"value":"待自提"}},{"id":"d8a296079c","buyerName":"凯凯王","buyerPhone":"13512721874","buyerId":"d68322a9c3f5400a8448a5a94a7db3f9","deliverStatus":5,"consigneeName":"高方方","consigneePhone":"13512721874","points":1,"gift":{"ref":"5768b5b5d2bc1dcc39597f77","id":"577f5816f5","name":"疯狂大甩卖攻城狮洗车一次","category":"车饰精品","thumbnail":"/images/thumbnail/146648003219623issjor.jpg","points":1,"marketPrice":9999.99,"online":1,"soldout":0},"dateCreated":"2016-06-25T14:09:58.808Z","RSCInfo":{"RSC":"5649bd6f8eba3c20360b079c","RSCAddress":"河南商丘虞城大杨集镇人民路解放路交界处路南","companyName":"新农人虞城直营店","RSCPhone":"13512721874"},"deliveryType":1,"backendUser":"56546caf4ac2dff43638e128","backendUserAccount":"admin","dateCompleted":"2016-06-25T14:12:16.935Z","dateDelivered":"2016-06-25T14:12:16.935Z","dateSet":"2016-06-25T14:12:16.935Z","orderStatus":{"type":4,"value":"已完成"}},{"id":"cfc0cc6e24","buyerName":"凯凯王","buyerPhone":"13512721874","buyerId":"d68322a9c3f5400a8448a5a94a7db3f9","deliverStatus":5,"consigneeName":"高方方","consigneePhone":"13512721874","points":2,"gift":{"ref":"575e40ae94ba82804f4c188f","id":"66477171c0","name":"熊本熊- 可爱萌萌哒的宠物宠物口吃了","category":"精美礼品","thumbnail":"/images/thumbnail/1466152604813lx2sm7vi.jpg","points":2,"marketPrice":60,"online":1,"soldout":0},"dateCreated":"2016-06-23T10:48:35.217Z","RSCInfo":{"RSC":"5649bd6f8eba3c20360b079c","RSCAddress":"河南商丘虞城大杨集镇人民路解放路交界处路南","companyName":"新农人虞城直营店","RSCPhone":"13512721874"},"deliveryType":1,"backendUser":"56546caf4ac2dff43638e128","backendUserAccount":"admin","dateCompleted":"2016-06-23T11:12:17.343Z","dateDelivered":"2016-06-23T11:12:17.343Z","dateSet":"2016-06-23T11:12:17.343Z","orderStatus":{"type":4,"value":"已完成"}},{"id":"526111b676","buyerName":"凯凯王","buyerPhone":"13512721874","buyerId":"d68322a9c3f5400a8448a5a94a7db3f9","deliverStatus":1,"consigneeName":"高方方","consigneePhone":"13512721874","points":1,"gift":{"ref":"5768b5b5d2bc1dcc39597f77","id":"577f5816f5","name":"疯狂大甩卖攻城狮洗车一次","category":"车饰精品","thumbnail":"/images/thumbnail/146648003219623issjor.jpg","points":1,"marketPrice":9999.99,"online":1,"soldout":0},"dateCreated":"2016-06-23T10:41:29.079Z","RSCInfo":{"RSC":"5649bd6f8eba3c20360b079c","RSCAddress":"河南商丘虞城大杨集镇人民路解放路交界处路南","companyName":"新农人虞城直营店","RSCPhone":"13512721874"},"deliveryType":1,"orderStatus":{"type":3,"value":"待自提"}}]
     * total : 9
     * pages : 1
     * page : 0
     */

    public DatasBean datas;

    public static class DatasBean {
        public int total;
        public int pages;
        public int page;
        /**
         * id : e68b77d9ac
         * buyerName : 凯凯王
         * buyerPhone : 13512721874
         * buyerId : d68322a9c3f5400a8448a5a94a7db3f9
         * deliverStatus : 5
         * consigneeName : 高方方
         * consigneePhone : 13512721874
         * points : 1
         * gift : {"ref":"5768b5b5d2bc1dcc39597f77","id":"577f5816f5","name":"疯狂大甩卖攻城狮洗车一次","category":"车饰精品","thumbnail":"/images/thumbnail/146648003219623issjor.jpg","points":1,"marketPrice":9999.99,"online":1,"soldout":0}
         * dateCreated : 2016-07-01T07:15:03.452Z
         * RSCInfo : {"RSC":"5649bd6f8eba3c20360b079c","RSCAddress":"河南商丘虞城大杨集镇人民路解放路交界处路南","companyName":"新农人虞城直营店","RSCPhone":"13512721874"}
         * deliveryType : 1
         * backendUser : 56546caf4ac2dff43638e128
         * backendUserAccount : admin
         * dateCompleted : 2016-07-01T07:19:14.235Z
         * dateDelivered : 2016-07-01T07:19:14.235Z
         * dateSet : 2016-07-01T07:19:14.235Z
         * orderStatus : {"type":4,"value":"已完成"}
         */

        public List<GiftordersBean> giftorders;

        public static class GiftordersBean {
            public String id;
            public String buyerName;
            public String buyerPhone;
            public String buyerId;
            public int deliverStatus;
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
             * marketPrice : 9999.99
             * online : 1
             * soldout : 0
             */

            public GiftBean gift;
            public String dateCreated;
            /**
             * RSC : 5649bd6f8eba3c20360b079c
             * RSCAddress : 河南商丘虞城大杨集镇人民路解放路交界处路南
             * companyName : 新农人虞城直营店
             * RSCPhone : 13512721874
             */

            public RSCInfoBean RSCInfo;
            public int deliveryType;
            public String backendUser;
            public String backendUserAccount;
            public String dateCompleted;
            public String dateDelivered;
            public String dateSet;
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
                public double marketPrice;

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
