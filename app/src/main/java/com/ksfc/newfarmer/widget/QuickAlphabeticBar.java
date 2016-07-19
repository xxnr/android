package com.ksfc.newfarmer.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.ksfc.newfarmer.beans.dbbeans.InviteeEntity;
import com.ksfc.newfarmer.beans.dbbeans.PotentialCustomersEntity;
import com.ksfc.newfarmer.utils.Utils;


/**
 * 字母索引条
 *
 * @author Administrator
 */
public class QuickAlphabeticBar extends ImageButton {
    private TextView mDialogText; // 中间显示字母的文本框
    private Handler mHandler; // 处理UI的句柄
    private ListView mList; // 列表
    private float mHight; // 高度
    private Context mContext;
    // 字母列表索引
    private List<String> letters = new ArrayList<>();
    // 字母索引哈希表
    private HashMap<String, Integer> alphaIndexer;
    Paint paint = new Paint();
    Paint paintCircle = new Paint();
    boolean showBkg = false;
    int choose = -1;

    private int oldItemPosition = -1;//上次滚动的下标


    public QuickAlphabeticBar(Context context) {
        super(context);
        this.mContext = context;
    }

    public QuickAlphabeticBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
    }

    public QuickAlphabeticBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }


    // 初始化
    public void init(TextView mDialogText) {
        this.mDialogText = mDialogText;
        mDialogText.setVisibility(View.INVISIBLE);
        mHandler = new Handler();
    }


    // 设置需要索引的列表(潜在客户列表)
    public void setListViewAndPotentailList(final List<PotentialCustomersEntity> potentialCustomers, final ListView mList) {
        this.mList = mList;
        mList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (oldItemPosition != firstVisibleItem) {
                    if (potentialCustomers != null && !potentialCustomers.isEmpty()) {
                        if (firstVisibleItem >= 1) {
                            PotentialCustomersEntity potentialCustomer = potentialCustomers.get(firstVisibleItem - 1);
                            choose = Utils.getNum(potentialCustomer.nameInitial, letters);
                        } else {
                            //初始化选中第一个TAB
                            choose = 0;
                        }
                        invalidate();
                    }
                    oldItemPosition = firstVisibleItem;
                }

            }
        });
    }

    // 设置需要索引的列表(客户列表)
    public void setListViewAndCustomerList(final List<InviteeEntity> inviteeEntities, final ListView mList) {
        this.mList = mList;
        mList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (oldItemPosition != firstVisibleItem) {
                    if (inviteeEntities != null && !inviteeEntities.isEmpty()) {
                        if (firstVisibleItem >= 1) {
                            InviteeEntity inviteeEntity = inviteeEntities.get(firstVisibleItem - 1);
                            choose = Utils.getNum(inviteeEntity.nameInitial, letters);
                        } else {
                            //初始化选中第一个TAB
                            choose = 0;
                        }
                        invalidate();
                    }
                    oldItemPosition = firstVisibleItem;
                }

            }
        });
    }


    // 设置字母索引哈希表
    public void setAlphaIndexer(HashMap<String, Integer> alphaIndexer) {
        this.alphaIndexer = alphaIndexer;
    }


    // 设置字母索引表
    public void setFastScrollLetter(List<String> letters) {
        this.letters.clear();
        this.letters.addAll(letters);
    }

    // 设置字母索引条的高度
    public void setHight(float mHight) {
        this.mHight = mHight;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int act = event.getAction();
        float y = event.getY();
        final int oldChoose = choose;
        // 计算手指位置，找到对应的段，让mList移动段开头的位置上
        int selectIndex = (int) (y / (mHight / letters.size()));

        if (selectIndex > -1 && selectIndex < letters.size()) { // 防止越界
            String key = letters.get(selectIndex);
            if (alphaIndexer.containsKey(key)) {
                int pos = alphaIndexer.get(key);
                if (mList.getHeaderViewsCount() > 0) { // 防止ListView有标题栏,本例中没有
                    this.mList.setSelectionFromTop(
                            pos + mList.getHeaderViewsCount(), 0);
                } else {
                    this.mList.setSelectionFromTop(pos, 0);
                }
                mDialogText.setText(letters.get(selectIndex));
            }
        }

        switch (act) {
            case MotionEvent.ACTION_DOWN:
                showBkg = true;
                if (oldChoose != selectIndex) {
                    if (selectIndex > 0 && selectIndex < letters.size()) {
                        choose = selectIndex;
                        invalidate();
                    }
                }
                if (mHandler != null) {
                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            if (mDialogText != null
                                    && mDialogText.getVisibility() == View.INVISIBLE) {
                                mDialogText.setVisibility(VISIBLE);
                            }
                        }
                    });
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (oldChoose != selectIndex) {
                    if (selectIndex > 0 && selectIndex < letters.size()) {
                        choose = selectIndex;
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                showBkg = false;
                choose = -1;
                if (mHandler != null) {
                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            if (mDialogText != null
                                    && mDialogText.getVisibility() == View.VISIBLE) {
                                mDialogText.setVisibility(INVISIBLE);
                            }
                        }
                    });
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height;
        if (getHeight() - 10 > 0) {
            //预留10个像素防止最后一个圆超出范围
            height = getHeight() - 10;
        } else {
            height = getHeight();
        }
        int width = getWidth();
        if (letters.size() > 0) {
            int sigleHeight = height / letters.size(); // 单个字母占的高度
            for (int i = 0; i < letters.size(); i++) {
                paint.setColor(Color.parseColor("#B0B0B0"));
                paint.setTextSize(Utils.dip2px(mContext, 14));
                paint.setAntiAlias(true);
                paintCircle.setColor(Color.parseColor("#00000000"));
                if (i == choose) {
                    paint.setColor(Color.parseColor("#FFFFFF")); // 滑动时按下字母颜色
                    paint.setFakeBoldText(true);
                    paintCircle.setColor(Color.parseColor("#00b38a"));
                }
                // 绘画的位置
                float xPos = width / 2 - paint.measureText(letters.get(i)) / 2;
                float yPos = sigleHeight * i + sigleHeight;
                // 绘画图片的位置
                canvas.drawCircle(width / 2, yPos - Utils.dip2px(mContext, 5), Utils.dip2px(mContext, 8), paintCircle);
                canvas.drawText(letters.get(i), xPos, yPos, paint);

                paint.reset();
                paintCircle.reset();
            }
        }
    }

}
