package com.ksfc.newfarmer;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;


/**
 * 三张启动页
 *
 * @author Bruce.wang
 */
public class GuideActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {

    private List<ImageView> imgList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initView();
    }

    public void initView() {
        ViewPager viewPager_guide = (ViewPager) findViewById(R.id.viewPager_guide);
        List<Fragment> fragmentsList = new ArrayList<>();
        imgList = new ArrayList<>();

        ImageView image1 = ((ImageView) findViewById(R.id.image1));
        ImageView image2 = ((ImageView) findViewById(R.id.image2));
        ImageView image3 = ((ImageView) findViewById(R.id.image3));

        imgList.add(image1);
        imgList.add(image2);
        imgList.add(image3);

        for (int i = 0; i < imgList.size(); i++) {
            imgList.get(i).setImageResource(R.drawable.dot_focus_write);
        }
        imgList.get(0).setImageResource(R.drawable.green_leaves);

        for (int i = 0; i < 3; i++) {
            Fragment fragment = new GuideFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("index", i);
            fragment.setArguments(bundle);
            fragmentsList.add(fragment);
        }
        viewPager_guide.setAdapter(new GuideAdapter(
                getSupportFragmentManager(), fragmentsList));

        viewPager_guide.setCurrentItem(0);
        viewPager_guide.addOnPageChangeListener(this);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < imgList.size(); i++) {
            imgList.get(i).setImageResource(R.drawable.dot_focus_write);
        }
        imgList.get(position).setImageResource(R.drawable.green_leaves);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class GuideAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragmentsList = null;


        public GuideAdapter(FragmentManager fm, List<Fragment> fragmentsList) {
            super(fm);
            this.fragmentsList = fragmentsList;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentsList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentsList.size();
        }

    }
}
