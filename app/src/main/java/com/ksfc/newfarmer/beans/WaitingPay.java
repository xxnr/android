/**
 *
 */
package com.ksfc.newfarmer.beans;

import com.ksfc.newfarmer.protocol.ResponseResult;

import java.io.Serializable;
import java.util.List;

/**
 * 项目名称：newFarmer63 类名称：WaitingPay 类描述： 创建人：王蕾 创建时间：2015-7-3 下午11:39:47 修改备注：
 */
@SuppressWarnings("serial")
public class WaitingPay extends ResponseResult {

    public Datas datas;

    public static class Datas {
        public String total;
        public List<Orders> rows;
    }

    public static class Orders implements Serializable {

        public String orderNo;  //待删除项 用于2.1.4及以下
        public String deposit;  //待删除项 于2.1.4及以下
        public int typeValue;// 状态值
        public String orderId;// 订单ID
        public String totalPrice;// 订单金额
        public String goodsCount;// 商品数量
        public String address;//订单收货地址
        public String recipientName;// 订单收货人
        public String recipientPhone;//订单收货人电话
        public String typeLable;// 订单状态说明
        public int payType; // 最后一次支付时的支付类型
        public String duePrice;// 待付金额,仅用于列表页展示，不用于付款
        public Order order;  //订单信息
        public RSCInfoEntity RSCInfo;
        public List<Product> products;//商品列表
        public List<SKUS> SKUs;//Sku列表
        public List<SubOrders> subOrders;

    }

    public static class RSCInfoEntity implements Serializable {
        public String RSC;
        public String RSCAddress;
        public String companyName;
        public String RSCPhone;

    }


    public static class Order implements Serializable {
        public String totalPrice;// 总价
        public String deposit;   // 定金（没有定金时为0）
        public String dateCreated;  // 下单时间
        public String datePaid;  // 支付完成时间
        public String dateDelivered;  // 发货时间
        public String dateCompleted;  // 完成时间
        public OrderStatus orderStatus;
        public DeliveryType deliveryType;



        public class OrderStatus implements Serializable {
            public int type;// 订单目前状态类型 0:交易关闭 1:待付款 2:部分付款 3:待发货 4:部分发货 5:已发货 6:已完成
            public String value;// 订单状态的展示
        }

        public class DeliveryType implements Serializable {
            public int type;// 订单目前状态类型 1:网点自提 2：用户自取
            public String value;// 订单状态的展示
        }
    }


    public static class Product implements Serializable {

        public String category;// 类别 化肥 汽车
        public int count;// 数量
        public String thumbnail;// 商品图片
        public String name;//商品名称
        public double deposit;// 商品定金
        public double price;// 商品价格
        public String id;// 商品id

    }

    public static class SKUS implements Serializable {
        public String ref; //skuID
        public String productId; //商品id
        public double deposit;// 商品定金
        public double price;// 商品价格
        public String productName; //SKU简称
        public String name; //SKU名称
        public String thumbnail; //商品略缩图
        public int deliverStatus; //商品的发货状态
        public int count; //商品数量
        public String category; //商品分类
        public List<Additions> additions; //附加选项
        public List<Attributes> attributes; //属性

        public static class Additions implements Serializable {
            public double price;//附加选项价格
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


    public static class SubOrders implements Serializable {
        public String id;// 阶段订单id
        public double price;// 子订单价格
        public String type;//// 子订单类型'deposit':阶段一定金, 'balance':阶段二尾款 'full':全款
        public String _id;// 类别 化肥 汽车
        public String payStatus;// / 子订单付款状态1:待付款, 2:已付款, 3:部分付款
    }


}
