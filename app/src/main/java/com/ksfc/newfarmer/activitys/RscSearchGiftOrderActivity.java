package com.ksfc.newfarmer.activitys;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;
import com.ksfc.newfarmer.BaseActivity;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.common.CommonAdapter;
import com.ksfc.newfarmer.common.CommonViewHolder;
import com.ksfc.newfarmer.common.PicassoHelper;
import com.ksfc.newfarmer.common.LoadMoreScrollListener;
import com.ksfc.newfarmer.protocol.ApiType;
import com.ksfc.newfarmer.protocol.remoteapi.RemoteApi;
import com.ksfc.newfarmer.protocol.Request;
import com.ksfc.newfarmer.beans.RscGiftOrderListResult;
import com.ksfc.newfarmer.utils.DateFormatUtils;
import com.ksfc.newfarmer.utils.PopWindowUtils;
import com.ksfc.newfarmer.utils.StringUtil;
import com.ksfc.newfarmer.widget.BaseViewUtils;
import com.ksfc.newfarmer.widget.ClearEditText;
import com.ksfc.newfarmer.widget.KeyboardListenRelativeLayout;
import com.ksfc.newfarmer.widget.LoadingFooter;
import com.ksfc.newfarmer.widget.PtrHeaderView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class RscSearchGiftOrderActivity extends BaseActivity implements
        KeyboardListenRelativeLayout.IOnKeyboardStateChangedListener {

    @BindView(R.id.rsc_search_edit)
    ClearEditText rscSearchEdit;
    @BindView(R.id.rsc_search_text)
    TextView rscSearchText;
    @BindView(R.id.gift_order_listView)
    ListView giftOrderListView;
    @BindView(R.id.rotate_header_list_view_frame)
    PtrClassicFrameLayout rotateHeaderListViewFrame;
    @BindView(R.id.pop_bg)
    RelativeLayout popBg;
    @BindView(R.id.myself_userImg_1)
    ImageView myselfUserImg1;
    @BindView(R.id.shop_text1)
    TextView shopText1;
    @BindView(R.id.null_shop_cart_layout)
    RelativeLayout nullShopCartLayout;
    @BindView(R.id.root_view)
    KeyboardListenRelativeLayout rootView;

    private String search="";
    private int page = 1;
    private LoadingFooter loadingFooter;
    private LoadMoreScrollListener moreOnsrcollListener = new LoadMoreScrollListener() {
        @Override
        public void loadMore() {
            //加载更多
            if (loadingFooter.getState() == LoadingFooter.State.Idle) {
                loadingFooter.setState(LoadingFooter.State.Loading);
                page++;
                RemoteApi.getRscGiftOrderList(RscSearchGiftOrderActivity.this, search, page);
            }
        }
    };
    private GiftOrderListAdapter adapter;
    private String deliveryId;
    private PopupWindow popupWindowSelfDelivery;
    private TextView recipient_name;
    private TextView recipient_phone;
    private EditText self_delivery_code_et;
    private TextView pop_save;


    @Override
    public int getLayout() {
        return R.layout.activity_rsc_seach_gift_order;
    }

    @Override
    public void OnActCreate(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        rootView = (KeyboardListenRelativeLayout) findViewById(R.id.root_view);
        rootView.setOnKeyboardStateChangedListener(this);
        rscSearchText.setText("取消");
        //点击取消关闭键盘
        RxView.clicks(rscSearchText).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                BaseViewUtils.hideSoftInput(RscSearchGiftOrderActivity.this,rscSearchText);
                finish();
            }
        });
        //文本改变的值赋给search
        RxTextView.textChangeEvents(rscSearchEdit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<TextViewTextChangeEvent>() {
                    @Override
                    public void call(TextViewTextChangeEvent textViewTextChangeEvent) {
                        search = textViewTextChangeEvent.text().toString().trim();
                    }
                });
        //键盘搜索的action
        rscSearchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*判断是否是“搜索”键*/
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    /*隐藏软键盘*/
                    BaseViewUtils.hideSoftInput(RscSearchGiftOrderActivity.this,rscSearchEdit);
                    if (StringUtil.checkStr(search)) {
                        showProgressDialog();
                        page = 1;
                        RemoteApi.getRscGiftOrderList(RscSearchGiftOrderActivity.this, search, page);
                    }
                    return true;
                }
                return false;
            }
        });
        //软件盘自动弹出
        rscSearchEdit.requestFocus();
        Timer timer = new Timer(); //设置定时器
        timer.schedule(new TimerTask() {
            @Override
            public void run() { //弹出软键盘的代码
                BaseViewUtils.showSoftInput(RscSearchGiftOrderActivity.this,rscSearchEdit);
            }
        }, 300); //设置300毫秒的时长

        giftOrderListView.setOnScrollListener(moreOnsrcollListener);
        loadingFooter = new LoadingFooter(RscSearchGiftOrderActivity.this, giftOrderListView);
        PtrHeaderView header = new PtrHeaderView(RscSearchGiftOrderActivity.this);
           /* 设置刷新头部view */
        rotateHeaderListViewFrame.setHeaderView(header);
        /* 设置回调 */
        rotateHeaderListViewFrame.addPtrUIHandler(header);
        rotateHeaderListViewFrame.setLastUpdateTimeRelateObject(this);
        rotateHeaderListViewFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                rotateHeaderListViewFrame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (rotateHeaderListViewFrame != null) {
                            rotateHeaderListViewFrame.refreshComplete();
                        }
                    }
                }, 2000);
                page = 1;
                RemoteApi.getRscGiftOrderList(RscSearchGiftOrderActivity.this, search, page);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });


    }

    @Override
    public void OnViewClick(View v) {

    }

    @Override
    public void onResponsed(Request req) {
        if (req.getApi() == ApiType.GET_RSC_GIFT_ORDER_LIST) {
            if (rotateHeaderListViewFrame != null) {
                rotateHeaderListViewFrame.refreshComplete();
            }
            if (req.getData().getStatus().equals("1000")) {
                RscGiftOrderListResult reqData = (RscGiftOrderListResult) req.getData();
                if (reqData.datas != null) {
                    List<RscGiftOrderListResult.DatasBean.GiftordersBean> list = reqData.datas.giftorders;
                    if (list != null && !list.isEmpty()) {
                        nullShopCartLayout.setVisibility(View.GONE);
                        loadingFooter.setSize(page, list.size());
                        if (page == 1) {
                            if (adapter == null) {
                                adapter = new GiftOrderListAdapter(RscSearchGiftOrderActivity.this, list);
                                giftOrderListView.setAdapter(adapter);
                            } else {
                                adapter.clear();
                                adapter.addAll(list);
                            }
                            giftOrderListView.setSelection(0);
                        } else {
                            if (adapter != null) {
                                adapter.addAll(list);
                            }
                        }
                    } else {
                        loadingFooter.setSize(page, 0);
                        if (page == 1) {
                            if (adapter != null) {
                                adapter.clear();
                            }
                            nullShopCartLayout.setVisibility(View.VISIBLE);
                        } else {
                            page--;
                        }
                    }
                }
            }
        } else if (ApiType.GET_RSC_GIFT_ORDER_SELF_DELIVERY == req.getApi()) {
            if (req.getData().getStatus().equals("1000")) {
                showCustomToast("自提成功", R.drawable.toast_success_icon);
                if (null != popupWindowSelfDelivery && popupWindowSelfDelivery.isShowing()) {
                    popupWindowSelfDelivery.dismiss();
                }
                showProgressDialog();
                page = 1;
                RemoteApi.getRscGiftOrderList(RscSearchGiftOrderActivity.this, search, page);

            }
        }
    }


    class GiftOrderListAdapter extends CommonAdapter<RscGiftOrderListResult.DatasBean.GiftordersBean> {

        public GiftOrderListAdapter(Context context, List<RscGiftOrderListResult.DatasBean.GiftordersBean> data) {
            super(context, data, R.layout.item_rsc_exchange_order_list);
        }

        @Override
        public void convert(CommonViewHolder holder, final RscGiftOrderListResult.DatasBean.GiftordersBean giftordersBean) {
            if (giftordersBean != null) {

                holder.setText(R.id.gift_order_time, DateFormatUtils.convertTime(giftordersBean.dateCreated));
                holder.setText(R.id.gift_order_person, StringUtil.checkStr(giftordersBean.consigneeName) ? giftordersBean.consigneeName : "");
                View pick_up_rel = holder.getView(R.id.pick_up_rel);
                pick_up_rel.setVisibility(View.GONE);

                if (giftordersBean.orderStatus != null) {
                    holder.setText(R.id.gift_order_delivery_state, StringUtil.checkStr(giftordersBean.orderStatus.value)
                            ? giftordersBean.orderStatus.value : "");

                    if (giftordersBean.orderStatus.type == 3) {//待自提
                        pick_up_rel.setVisibility(View.VISIBLE);
                    }
                }
                if (giftordersBean.gift != null) {
                    PicassoHelper.setImageRes(RscSearchGiftOrderActivity.this,giftordersBean.gift.thumbnail,(ImageView) holder.getView(R.id.gift_order_img_iv));
                    holder.setText(R.id.gift_order_name_iv, StringUtil.checkStr(giftordersBean.gift.name)
                            ? giftordersBean.gift.name : "");
                    holder.setText(R.id.gift_order_price_iv, StringUtil.checkStr(String.valueOf(giftordersBean.gift.points))
                            ? String.valueOf(giftordersBean.gift.points) : "");
                }
                //自提操作
                final View go_to_pick = holder.getView(R.id.go_to_pick);
                RxView.clicks(go_to_pick)
                        .throttleFirst(1, TimeUnit.SECONDS)
                        .subscribe(new Action1<Void>() {
                            @Override
                            public void call(Void aVoid) {
                                showPopDelivery(go_to_pick, giftordersBean);
                                deliveryId = giftordersBean.id;
                            }
                        });

            }
        }
    }

    /***********************************
     * 以下是网点自提相关
     *************************************************************/

    /**
     * 创建PopupWindow
     */
    private void initSelfDeliveryPopUptWindow() {
        @SuppressLint("InflateParams") View popupWindow_view = getLayoutInflater().inflate(
                R.layout.pop_rsc_pick_up_exchange_order, null, false);
        // 创建PopupWindow实例
        popupWindowSelfDelivery = new PopupWindow(popupWindow_view,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindowSelfDelivery.setAnimationStyle(R.style.popWindow_anim_style);
        popupWindowSelfDelivery.setFocusable(true);
        popupWindowSelfDelivery.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindowSelfDelivery.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                PopWindowUtils.setBackgroundBlack(popBg, 1);
            }
        });

        //初始化
        recipient_name = (TextView) popupWindow_view.findViewById(R.id.recipient_name);
        recipient_phone = (TextView) popupWindow_view.findViewById(R.id.recipient_phone);
        self_delivery_code_et = (EditText) popupWindow_view.findViewById(R.id.self_delivery_code_et);
        pop_save = (TextView) popupWindow_view.findViewById(R.id.pop_save);
        //点击X关闭pop
        RxView.clicks(popupWindow_view.findViewById(R.id.pop_close)).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (null != popupWindowSelfDelivery && popupWindowSelfDelivery.isShowing()) {
                    popupWindowSelfDelivery.dismiss();
                }
            }
        });
        //去自提
        RxView.clicks(pop_save).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                String code = self_delivery_code_et.getText().toString().trim();
                if (StringUtil.checkStr(code) && StringUtil.checkStr(deliveryId)) {
                    RemoteApi.rscSelfDelivery(RscSearchGiftOrderActivity.this, deliveryId, code);
                }
            }
        });
        //监听输入文本框文字变化
        RxTextView.textChangeEvents(self_delivery_code_et)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<TextViewTextChangeEvent>() {
                    @Override
                    public void call(TextViewTextChangeEvent textViewTextChangeEvent) {
                        String key = textViewTextChangeEvent.text().toString().trim();
                        if (key.length() >= 7) {
                            pop_save.setEnabled(true);
                        } else {
                            pop_save.setEnabled(false);
                        }
                    }
                });

    }


    //显示popWindow
    private void showPopDelivery(View parent, RscGiftOrderListResult.DatasBean.GiftordersBean giftordersBean) {
        if (null != popupWindowSelfDelivery) {
            popupWindowSelfDelivery.dismiss();
        } else {
            initSelfDeliveryPopUptWindow();
        }
        //初始化按钮
        self_delivery_code_et.setText("");
        pop_save.setEnabled(false);
        pop_save.setText("确定");

        recipient_name.setText(StringUtil.checkStr(giftordersBean.consigneeName) ? giftordersBean.consigneeName : "");
        recipient_phone.setText(StringUtil.checkStr(giftordersBean.consigneePhone) ? giftordersBean.consigneePhone : "");

        //设置背景及展示
        PopWindowUtils.setBackgroundBlack(popBg, 0);
        popupWindowSelfDelivery.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
    }


    @Override
    public void onKeyboardStateChanged(int state) {
        if (state == KeyboardListenRelativeLayout.KEYBOARD_STATE_HIDE) {
            if (self_delivery_code_et != null) {
                self_delivery_code_et.clearFocus();
            }
        }
    }



}