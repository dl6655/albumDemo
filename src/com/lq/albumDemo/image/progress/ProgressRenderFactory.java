package com.lq.albumDemo.image.progress;

import android.content.Context;
import com.lq.albumDemo.image.WebImageView;

public class ProgressRenderFactory {

	/**
	 * 进度条类型， 目前提供默认的，其他的效果需要自定义
	 */
	public enum ProgressRenderType {
		NONE, //没有效果
		DEFAULT, // 默认的
	}

	private static ProgressRenderFactory instance;

	private ProgressRenderFactory() {

	}

	public synchronized static ProgressRenderFactory getInstance() {
		if (instance == null) {
			instance = new ProgressRenderFactory();
		}
		return instance;
	}

	/**
	 * 获取进度条显示
	 * 
	 * @param context
	 * @param type
	 *            进度条类型
	 * @return
	 */
	public WebImageView.ImageProgressListener getProgressRender(Context context, ProgressRenderType type) {
		WebImageView.ImageProgressListener listener = null;

		if (type == ProgressRenderType.DEFAULT) {
			listener = new DefaultProgressRender(context);
		}

		return listener;
	}

}
