package com.ksfc.newfarmer.activitys;

import java.util.HashMap;
import java.util.Map;

import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.MainActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.RndApplication;
import com.ksfc.newfarmer.adapter.GoodsDetailAdapter;
import com.ksfc.newfarmer.db.Store;
import com.ksfc.newfarmer.db.dao.ShoppingDao;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.protocol.RequestParams;
import com.ksfc.newfarmer.protocol.beans.GetGoodsDetail;
import com.ksfc.newfarmer.protocol.beans.addtoCart;
import com.ksfc.newfarmer.widget.CustomDialog;
import com.ksfc.newfarmer.utils.RndLog;
import com.ksfc.newfarmer.widget.VerticalViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShangpinDetailActivity extends BaseActivity implements ViewTreeObserver.OnGlobalLayoutListener {
    private EditText discount_geshu;
    private ShoppingDao dao;
    private String goodId;
    private GetGoodsDetail.GoodsDetail detail;
    private String type;
    private TextView jinqingqidai_bar;
    private LinearLayout shangpin_detail_bottom_bar;
    private int fenshu;
    private VerticalViewPager viewPager;

    private boolean toast_flag = true;//true的时候提示 添加购物车成功  false 为不提示
    private LinearLayout toSoft_ll;


    @Override
    public int getLayout() {
        return R.layout.shangpin_detail_layout;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        RndApplication.tempDestroyActivityList.add(ShangpinDetailActivity.this);
        goodId = getIntent().getStringExtra("goodId");
        type = getIntent().getStringExtra("type");
        setTitle("商品详情");
        initView();
        dao = new ShoppingDao(ShangpinDetailActivity.this);
        getData();


    }

    private void getData() {
        showProgressDialog();
        RequestParams params = new RequestParams();
        if (isLogin()) {
            params.put("locationUserId", Store.User.queryMe().userid);
        } else {
            params.put("locationUserId", "");
        }
        params.put("productId", goodId);
        execApi(ApiType.GET_GOOD_DETAIL, params);
    }

    private void initView() {
        discount_geshu = (EditText) findViewById(R.id.discount_geshu);
        viewPager = (VerticalViewPager) findViewById(R.id.viewPager_vertical);
        jinqingqidai_bar = ((TextView) findViewById(R.id.jingqingqidai_bar));
        shangpin_detail_bottom_bar = ((LinearLayout) findViewById(R.id.shangpin_detail_bottom_bar));

        setRightImage(R.drawable.goods_shopping_cart_icon);
        setRightViewListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShangpinDetailActivity.this, MainActivity.class);
                intent.putExtra("id", 3);
                startActivity(intent);
            }
        });

        showRightImage();
        discount_geshu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("0")) {
                    // 光标移到最后
                    discount_geshu.setText("1");
                    Editable eText = discount_geshu.getText();
                    Selection.setSelection(eText, eText.length());
                }
            }
        });


        //判断键盘的收起
        toSoft_ll = ((LinearLayout) findViewById(R.id.shangpin_detail_ll));
        toSoft_ll.getViewTreeObserver().addOnGlobalLayoutListener(this);

    }

    @Override
    public void OnViewClick(View v) {

        switch (v.getId()) {
            //状态分别进行判断
            case R.id.discount_jian:
                if (!TextUtils.isEmpty(discount_geshu.getText().toString().trim())) {
                    fenshu = Integer
                            .valueOf(discount_geshu.getText().toString().trim());
                } else {
                    //空的时候初始化为1
                    discount_geshu.setText("1");
                    fenshu = 1;
                }
                if (fenshu <= 1) {
                    showToast("商品数量不能再减少了");
                    return;
                } else {
                    fenshu--;
                    discount_geshu.setText(fenshu + "");
                }
                break;
            case R.id.discount_jia:
                if (!TextUtils.isEmpty(discount_geshu.getText().toString().trim())) {
                    fenshu = Integer
                            .valueOf(discount_geshu.getText().toString().trim());
                } else {
                    //空的时候初始化为1
                    discount_geshu.setText("1");
                    fenshu = 1;
                }
                fenshu++;
                discount_geshu.setText(fenshu + "");
                break;
            case R.id.add_to_shopcart:
                if (!TextUtils.isEmpty(discount_geshu.getText().toString().trim())) {
                    fenshu = Integer
                            .valueOf(discount_geshu.getText().toString().trim());
                } else {
                    showToast("请输入正确的商品数量");
                    return;
                }
                toast_flag = true;
                addToCar();
                break;
            case R.id.buy_now:
                if (!TextUtils.isEmpty(discount_geshu.getText().toString().trim())) {
                    fenshu = Integer
                            .valueOf(discount_geshu.getText().toString().trim());
                } else {
                    showToast("请输入正确的商品数量");
                    return;
                }

                if (isLogin()) {
                    toast_flag = false;//是否显示toast
                    addToCar();
                    Intent intent = new Intent(ShangpinDetailActivity.this, OrderDetailActivity.class);
                    intent.putExtra("goodId", goodId);
                    intent.putExtra("count", fenshu + "");
                    intent.addFlags(1);
                    startActivity(intent);
                } else {
                    CustomDialog.Builder builder = new CustomDialog.Builder(
                            ShangpinDetailActivity.this);
                    builder.setMessage("您还没有登录,是否登录？")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(ShangpinDetailActivity.this,
                                            LoginActivity.class);
                                    intent.putExtra("id", 0);
                                    startActivity(intent);
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("否", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    CustomDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }
                break;
            default:
                break;
        }

    }

    private void addToCar() {//加入购物车
        if (!TextUtils.isEmpty(discount_geshu.getText().toString().trim()) && !discount_geshu.getText().toString().trim().equals("0")) {
            fenshu = Integer
                    .valueOf(discount_geshu.getText().toString().trim());
        } else {
            fenshu = 1;
        }
        if (fenshu <= 0) {
            showToast("请先添加商品！");
            return;
        }
        if (isLogin()) {
            // app/shopCart/addToCart
            // locationUserId:操作人ID
            // goodsId:商品ID
            // userId:用户ID
            // count：件数
            RequestParams params = new RequestParams();
            params.put("locationUserId", Store.User.queryMe().userid);
            params.put("userId", Store.User.queryMe().userid);
            params.put("goodsId", goodId);
            params.put("count", fenshu);
            params.put("update_by_add", "true");
            execApi(ApiType.ADDTOCART, params);
        } else {
            // 查询数据库 为空 就插入 不为空 就更新
            Map<String, String> shopping = dao.getShopping(detail.id);
            if (shopping.isEmpty()) {
                // 新插入
                Map<String, String> map = new HashMap<String, String>();
                map.put("pid", detail.id);
                map.put("title", detail.name);
                map.put("imageurl", detail.imgUrl);
                map.put("numbers", discount_geshu.getText().toString()
                        .trim()
                        + "");
                map.put("type", type);
                map.put("stars", "");
                map.put("pricenow", detail.price);
                dao.saveShopping(map);
            } else {
                // 先从数据库获取对应id的个数 然后相加本地的个数
                Map<String, String> shop = dao.getShopping(detail.id);
                String string = shop.get("numbers");
                // 更新数据库商品对应的id
                int sumNum = Integer.valueOf(string)
                        + Integer.valueOf(discount_geshu.getText()
                        .toString().trim());
                dao.updateShopping(detail.id, sumNum + "");
            }
            showToast("添加购物车成功");
        }


    }

    @Override
    public void onResponsed(Request req) {
        disMissDialog();
        if (req.getApi() == ApiType.GET_GOOD_DETAIL) {
            GetGoodsDetail data = (GetGoodsDetail) req.getData();
            detail = data.datas;
            if (!TextUtils.isEmpty(detail.app_body_url)) {
                GoodsDetailAdapter adapter = new GoodsDetailAdapter(2, detail, this);
                viewPager.setAdapter(adapter);
                viewPager.setOffscreenPageLimit(0);
            } else {
                GoodsDetailAdapter adapter = new GoodsDetailAdapter(1, detail, this);
                viewPager.setAdapter(adapter);
            }
            if (detail.presale) {
                jinqingqidai_bar.setVisibility(View.VISIBLE);
                shangpin_detail_bottom_bar.setVisibility(View.GONE);
            } else {
                jinqingqidai_bar.setVisibility(View.GONE);
                shangpin_detail_bottom_bar.setVisibility(View.VISIBLE);
            }
            setViewClick(R.id.add_to_shopcart);
            setViewClick(R.id.discount_jian);
            setViewClick(R.id.discount_jia);
            setViewClick(R.id.buy_now);
        } else if (req.getApi() == ApiType.ADDTOCART) {
            disMissDialog();
            addtoCart data = (addtoCart) req.getData();
            if ("1000".equals(data.getStatus())) {
                if (toast_flag) {
                    showToast("添加购物车成功");
                }
            }
        }
    }
    //监听软键盘收起时，如果输入框为“”，变为1
    @Override
    public void onGlobalLayout() {
        int heightDiff = toSoft_ll.getRootView().getHeight() - toSoft_ll.getHeight();

        if (heightDiff > 100) {
            RndLog.v(TAG, "键盘弹出状态");
        } else {
            if (discount_geshu.getText().toString().trim().equals("")) {
                discount_geshu.setText("1");
                Editable eText = discount_geshu.getText();
                Selection.setSelection(eText, eText.length());
            }
            RndLog.v(TAG, "键盘收起状态");
        }
    }
}
