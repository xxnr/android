package net.yangentao.util;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import net.yangentao.util.TaskUtil.BackFore;
import net.yangentao.util.app.App;

//TODO 
public class UIUtil {
	public interface ImageGetter {
		/**
		 * 同步回调, 返回url对应的本地文件, 目录必须已经创建, 可以对File直接写.<br/>
		 * url和返回文件的绝对路径必须是一一对应的, 可以使用MD5值+url长度的形式.
		 * 
		 * @param url
		 *            下载地址 http get
		 * @return 如果返回的File文件已经存在(file.exist()), 则,不会再去网上下载; 否则会去网上下载,
		 *         并写入到File.
		 */
		public File getLocalFile(String url);

		/**
		 * 重要! 下载失败时localFile==null, 不要再进行导致下载的直接或间接操作, 否则会导致死循环<br/>
		 * 异步回调, 从网络下载完成后; <br/>
		 * 如果imageView 在ListView中,此时不应该直接给imageView设置Bitmap,
		 * 应该调用ListView的Adapter的notifyDatasetChange方法<br/>
		 * 这样做是由ListView的Item复用引起的. 因此, 最后一个参数是File, 不是Bitmap<br/>
		 * 如果不是在ListView中, 可以调用Util.loadBitmapFromFile(file, xxx, xxx
		 * );来加载bitmap, 然后给ImageView设置图片<br/>
		 * 
		 * @param url
		 *            图片下载地址
		 * @param localFile
		 *            下载失败时是null
		 */
		public void onDownloadFinish(String url, File localFile);
	}

	// 下载状态, <url, status> , status: {0:下载成功; 1:下载中; -1:下载失败}
	private static final Map<String, Integer> downloadMap = new HashMap<>();

	/**
	 * 根据url填充ImageView, 所有参数非空
	 * 
	 * @param parentView
	 * @param imageViewId
	 * @param url
	 *            非空, 如果此参数为空会抛异常
	 * @param imgGetter
	 */
	public static Bitmap imageDownload(final String url,
			final ImageGetter imgGetter) {
		if (url == null) {
			return null;
		}
		final File file = imgGetter.getLocalFile(url);
		synchronized (downloadMap) {
			// 检查本地文件
			if (file.exists()) {
				downloadMap.put(url, 0);// 标记下载成功
				return Util.loadBitmapFromFile(file, 600, 800);
			}
			// 检查状态, 比如:可能正在下载
			Integer status = downloadMap.get(url);
			if (status == null) {// 没有执行过下载
				// do nothing
			} else if (status.intValue() == 0) {// 下载成功, 实际上不会执行到这里,
				return Util.loadBitmapFromFile(file, 600, 800);
			} else if (status.intValue() > 0) {// 下载中
				return null;
			} else {// <0, 下载失败
					// 继续下载
			}

			downloadMap.put(url, 1);// 标记正在下载
		}
		// url 非空, 并且本地没有文件, 去服务器下载
		TaskUtil.backFore(new BackFore() {
			private boolean success = false;

			@Override
			public void onFore() {
				imgGetter.onDownloadFinish(url, success ? file : null);
			}

			@Override
			public void onBack() {

				InputStream is = HttpUtil.httpGetStream(url);
				if (is != null) {
					File f = new File(file.getAbsolutePath() + ".tmp");
					if (Util.saveStreamToFile(is, f)) {
						if (file.exists()) {// 不应该被执行
							file.delete();
						}
						synchronized (downloadMap) {
							success = f.renameTo(file);
							if (success) {
								downloadMap.put(url, 0);
								return;
							}
						}
					}
				}
				synchronized (downloadMap) {
					downloadMap.put(url, -1);
				}

			}
		});

		return null;
	}

	public static ImageView findImageView(View parentView, int imageViewId) {
		return (ImageView) parentView.findViewById(imageViewId);

	}

	public static ImageView findImageView(Activity activity, int imageViewId) {
		return (ImageView) activity.findViewById(imageViewId);

	}

	public static TextView findTextView(View parentView, int textViewId) {
		return (TextView) parentView.findViewById(textViewId);

	}

	public static TextView findTextView(Activity activity, int textViewId) {
		return (TextView) activity.findViewById(textViewId);

	}

	public static void setTextView(Activity activity, int textViewId,
			String text) {
		findTextView(activity, textViewId).setText(text);
	}

	public static void setTextView(Activity activity, int textViewId,
			int resIdText) {
		findTextView(activity, textViewId).setText(resIdText);
	}

	public static void setTextView(View parent, int textViewId, String text) {
		findTextView(parent, textViewId).setText(text);
	}

	public static void setTextView(View parent, int textViewId, int resIdText) {
		findTextView(parent, textViewId).setText(resIdText);
	}

	public static void setViewVisibility(View parent, int viewid, int visibility) {
		parent.findViewById(viewid).setVisibility(visibility);
	}

	private static float density = -1;

	public static int dp2px(float dp) {
		if (density < 0) {
			density = App.getApp().getResources().getDisplayMetrics().density;
		}
		return (int) (density * dp + 0.5f);
	}
}
