package com.ksfc.newfarmer.adapter;

import java.util.ArrayList;
import java.util.List;

import com.ksfc.newfarmer.MsgID;
import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.protocol.beans.HomeImageResult.Rows;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * 广告位的适配器
 * 
 * @author Bruce.Wang
 * 
 */
public class PosterAdapter extends FragmentPagerAdapter {

	private List<Rows> posters;

	private List<Fragment> views;

	@Override
	public int getCount() {
		return posters.size();
	}

	public PosterAdapter(FragmentManager manager, List<Rows> posters) {
		super(manager);
		this.posters = posters;
		views = new ArrayList<Fragment>();
		for (int i = 0; i < posters.size(); i++) {
			PosterFragment frg = new PosterFragment();
			frg.setPoster(posters.get(i));
			views.add(frg);
		}
	}

	public void reset(List<Rows> posters) {
		this.posters = posters;
		views = new ArrayList<Fragment>();
		for (int i = 0; i < posters.size(); i++) {
			PosterFragment frg = new PosterFragment();
			frg.setPoster(posters.get(i));
			views.add(frg);
		}
		notifyDataSetChanged();
	}

	@Override
	public Fragment getItem(int arg0) {
		return views.get(arg0);
	}

	public static class PosterFragment extends Fragment {

		private Rows poster;

		public PosterFragment() {
		}

		public void setPoster(Rows rows) {
			this.poster = rows;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.item_poster, null);
			return view;
		}

		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);
			if (poster == null) {
				return;
			}
			final ImageView iv = (ImageView) view.findViewById(R.id.iv);
			iv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					// Intent intent = new
					// Intent(getActivity(),LunboActivity.class);
					// intent.putExtra("url", poster.url);
					// startActivity(intent);

				}
			});
			ImageLoader.getInstance()
					.displayImage(MsgID.IP + poster.imgUrl, iv);
			// iv.setBackgroundResource(poster.imgUrl);
		}

	}

}
