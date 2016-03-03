package com.ksfc.newfarmer.protocol.beans;

import java.io.Serializable;
import java.util.List;

import com.ksfc.newfarmer.protocol.ResponseResult;

public class MyOrderDetailResult extends ResponseResult {

    public Datas datas;

    public static class Datas {
        public String total;
        public String locationUserId;
        public Rows rows;
    }

    public static class Rows {
        public String id;// 订单ID
        public String orderNo;// 订单编码   用于2.1.4及一下版本
        public String deposit; // 订单下次付款金额或者0（用于2.1.4及一下版本）
        public String orderSubNo;// 子订单编码
        public String totalPrice;// 总金额
        public String dataSubmit;// 订单提交时间（用于2.1.4及一下版本）
        public String recipientName;// 收货人名称
        public String recipientPhone;// 收货人手机号
        public String address;// 地址
        public String remarks;
        public String deliveryTime;// 期望收货日期
        public int orderType;
        public int payStatus;//支付状态(用于2.1.4及一下版本）
        public int deliverStatus;//发货状态(用于2.1.4及一下版本）
        public int payType;//支付类型
        public boolean confirmed;// 是否确认收货
        public boolean isClosed;//是否关闭;
        public String duePrice;  // 待付金额,仅用于展示，不用于付款
        public String paySubOrderType;// 本次付款的子订单类型'deposit':阶段一定金, 'balance':阶段二尾款 'full':全款，（可能没有）用于判断本次付款是什么阶段
        public Order order;
        public List<SubOrders> subOrders;
        public Payment payment;
        public List<OrderGood> orderGoodsList;
        public List<SKUS> SKUList;

        public static class Order implements Serializable {
            public String totalPrice;// 总价
            public String deposit;   // 定金（没有定金时为0）
            public String dateCreated;  // 下单时间
            public String datePaid;  // 支付完成时间
            public String dateDelivered;  // 发货时间
            public String dateCompleted;  // 完成时间
            public OrderStatus orderStatus;


            public class OrderStatus implements Serializable {
                public int type;// 订单目前状态类型 0:交易关闭 1:待付款 2:部分付款 3:待发货 4:部分发货 5:已发货 6:已完成
                public String value;// 订单状态的展示
            }
        }


        public static class SubOrders implements Serializable {
            public String id;// 阶段订单id
            public String price;// 子订单价格
            public String type;//// 子订单类型'deposit':阶段一定金, 'balance':阶段二尾款 'full':全款
            public String _id;// 类别 化肥 汽车
            public String payStatus;// / 子订单付款状态1:待付款, 2:已付款, 3:部分付款
            public int paidCount; // 之前付款次数
            public String payType;// 付款方式（可能没有）只有一次付款完成时才有
            public String paidPrice;//已付款金额
            public List<Payments> payments; // 子订单的每次付款详情（可能没有）只存在多次付款或者一次付款没有付完

            public static class Payments implements Serializable {
                public String dateCreated;// 创建时间
                public String id;// 支付ID
                public int payStatus; // 支付状态
                public int payType; // 支付类型
                public String price;// 支付金额
                public int slice;// 支付的次数
                public String suborderId;// 所属子订单ID
                public String datePaid;// 支付时间
            }
        }

        public static class Payment implements Serializable {
            public String paymentId;// 支付id
            public String price;// 支付金额
            public String suborderId;//// 子阶段id
        }


        public static class SKUS implements Serializable {
            public String name; //SKU名称
            public int count; //商品数量
            public float price;// 商品价格
            public String orderSubType;
            public String orderSubNo;
            public String goodsId; //商品id
            public String imgs; //skuID
            public float deposit;// 商品定金
            public String category; //商品分类
            public String deliverStatus; //SKU简称
            public String productName; //SKU简称
            public List<Additions> additions; //附加选项
            public List<Attributes> attributes; //属性

            public static class Additions implements Serializable {
                public float price;//附加选项价格
                public String name;// 附加选项名称
                public String ref;//附加选项_id
                public String _id;//附加选项_id
            }

            public static class Attributes implements Serializable {
                public String value;
                public String name;
                public String ref;
                public String _id;
            }

        }
    }
    //用于2.1.4及以下适配
    public static class OrderGood implements Serializable {
        public String imgs;
        public String goodsId;
        public String orderSubType;
        public String originalPrice;
        public float unitPrice;
        public int goodsCount;
        public String goodsName;
        public String orderSubNo;
        public String deliverStatus;
        public float deposit;

    }


}
