package com.lq.albumDemo.image;

import android.view.View;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 进度通知管理
 * 
 * @author changxiang
 * 
 */
public class ProgressManager {
	private static final String TAG = "ProgressManager";

	private ConcurrentHashMap<String, ArrayList<WeakReference<WebImageView>>> mProgressListenerList = new ConcurrentHashMap<String, ArrayList<WeakReference<WebImageView>>>();

	private static ProgressManager instance;

	private ProgressManager() {
	};

	public static ProgressManager getInstance() {
		if (instance == null) {
			instance = new ProgressManager();
		}
		return instance;
	}

	/**
	 * 添加观察者
	 * 
	 * @param url
	 * @param listener
	 */
	public void addProgressListener(String url, WebImageView listener) {
		// 先删除旧的对应关系
		removeProgressListener(listener);
		
		ArrayList<WeakReference<WebImageView>> list = mProgressListenerList.get(url);
		if (list == null) {
			list = new ArrayList<WeakReference<WebImageView>>();
			mProgressListenerList.put(url, list);
		}
		
		list.add(new WeakReference<WebImageView>(listener));
	}

	/**
	 * 删除观察者，保证同一个view在表里最多只有一个
	 * 
	 * @param listener
	 */
	public void removeProgressListener(WebImageView listener) {
		Map.Entry<String, ArrayList<WeakReference<WebImageView>>> entry;
		ArrayList<WeakReference<WebImageView>> list;
		Iterator<Map.Entry<String, ArrayList<WeakReference<WebImageView>>>> iterator = mProgressListenerList.entrySet()
				.iterator();
		while (iterator.hasNext()) {
			entry = iterator.next();
			list = entry.getValue();

			if (list != null) {
				int len = list.size();
				WeakReference<WebImageView> item;
				for (int i = len - 1; i >= 0; i--) {
					item = list.get(i);
					if (item.get() == null || item.get() == listener) {
						list.remove(i);
					}
				}
			}

		}
	}

	/**
	 * 通知状态变化
	 * 
	 * @param imageUri
	 * @param view
	 * @param newState
	 */
	public void notifyStateChanged(String imageUri, View view, int newState) {
		ArrayList<WeakReference<WebImageView>> list = mProgressListenerList.get(imageUri);
		if (list == null || list.size() == 0) {
			return;
		}

		int len = list.size();
		WeakReference<WebImageView> item;
		for (int i = len - 1; i >= 0; i--) {
			item = list.get(i);
			if (item.get() == null) {
				list.remove(i);
			} else if(item.get().getImageProgressListener() != null){
				item.get().getImageProgressListener().onStateChange(imageUri, item.get(), newState);

				if (newState == WebImageView.ImageProgressListener.STATE_COMPLETE || newState == WebImageView.ImageProgressListener.STATE_FAILED
						|| newState == WebImageView.ImageProgressListener.STATE_CANCELLED) {
					list.remove(i);
				}
			}
		}
	}

	/**
	 * 通知进度
	 * 
	 * @param imageUri
	 * @param view
	 * @param current
	 * @param total
	 */
	public void notifyProgressChanged(String imageUri, View view, int current, int total) {
		
		ArrayList<WeakReference<WebImageView>> list = mProgressListenerList.get(imageUri);
		if (list == null || list.size() == 0) {
			return;
		}

		int len = list.size();
		WeakReference<WebImageView> item;
		for (int i = len - 1; i >= 0; i--) {
			item = list.get(i);
			if (item.get() == null) {
				list.remove(i);
			} else if(item.get().getImageProgressListener() != null){
				item.get().getImageProgressListener().onProgressUpdate(imageUri, item.get(), current, total);
			}
		}
	}

}
