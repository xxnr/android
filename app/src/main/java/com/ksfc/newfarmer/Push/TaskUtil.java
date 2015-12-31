package com.ksfc.newfarmer.Push;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

public class TaskUtil {
	public interface ForeBack {
		public void onFore();

		public void onBack();
	}

	public interface BackFore {
		public void onFore();

		public void onBack();
	}

	private static Handler main;
	private static Handler back;
	private static HandlerThread thread;
	private static ExecutorService es;

	static {
		main = new Handler(Looper.getMainLooper());
		thread = new HandlerThread("back thread handler");
		thread.setDaemon(true);
		thread.start();
		back = new Handler(thread.getLooper());
		es = Executors.newCachedThreadPool(new ThreadFactory() {

			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setPriority(Thread.NORM_PRIORITY - 1);
				t.setDaemon(true);
				return t;
			}
		});
	}

	public static Handler getMainHandler() {
		return main;
	}

	public static Handler getBackHandler() {
		return back;
	}

	public static ExecutorService getPool() {
		return es;
	}

	/**
	 * main handler������
	 */
	public static void foreDelay(final Runnable r, final long millSeconds) {
		main.postDelayed(new Runnable() {

			@Override
			public void run() {
				try {
					r.run();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}, millSeconds);
	}

	/**
	 * main handler������
	 */
	public static void fore(final Runnable r) {
		main.post(new Runnable() {

			@Override
			public void run() {
				try {
					r.run();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * thread handler������, �Ŷ�,��֤˳��
	 */
	public static void backQueue(final Runnable r) {
		back.post(new Runnable() {

			@Override
			public void run() {
				try {
					r.run();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * thread pool������
	 */
	public static void back(final Runnable r) {
		es.submit(r);
	}

	/**
	 * ��main handler��ִ��task.onFore, �����̳߳���ִ��task.onBack
	 */
	public static void foreBack(final ForeBack task) {
		fore(new Runnable() {

			@Override
			public void run() {
				try {
					task.onFore();
				} catch (Throwable e) {
					e.printStackTrace();
				}
				back(new Runnable() {

					@Override
					public void run() {
						try {
							task.onBack();
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
	}

	/**
	 * �����̳߳���ִ��task.onBack, ��main handler��ִ��task.onFore
	 */
	public static void backFore(final BackFore task) {
		back(new Runnable() {

			@Override
			public void run() {
				try {
					task.onBack();
				} catch (Throwable e) {
					e.printStackTrace();
				}
				fore(new Runnable() {

					@Override
					public void run() {
						try {
							task.onFore();
						} catch (Throwable e) {
							e.printStackTrace();
						}

					}
				});
			}
		});
	}

}
