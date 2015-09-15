package com.lq.albumDemo.image.processer;

import android.content.Context;
import com.lq.albumDemo.image.WebImageView;

/**
 * 回调图片效果处理器工厂类
 * @author changxiang
 *
 */
public class ImageProcesserFactory {

	public enum ProcessType {
		NONE, // 不做处理
		ROUND, // 小圆角
		ROUND_BIG, // 大圆角
		CIRCLE, // 圆形
        ROUND_WIDTH,//支持原图宽高的圆角
        USER_PROFILE, ProgressRenderType, // 个人首页头像
	}
	
	private static ImageProcesserFactory instance = new ImageProcesserFactory();

	private ImageProcesserFactory() {
	};

	public static ImageProcesserFactory getInstance() {
		return instance;
	}

	/**
	 * 获取对应的处理器
	 * @param context
	 * @param processType  回调图片处理类型
	 * @return
	 */
	public WebImageView.ImageProcesser getImageProcesser(Context context, ProcessType processType) {
		WebImageView.ImageProcesser processer = null;

//		if (processType == ProcessType.ROUND) {
//			processer = RoundImageProcesser.getSmallRound(context);
//		} else if (processType == ProcessType.ROUND_BIG) {
//			processer = RoundImageProcesser.getBigRound(context);
//		} else if (processType == ProcessType.CIRCLE) {
//			processer = CircleImageProcesser.getInstance();
//		} else if (processType == ProcessType.USER_PROFILE) {
//            processer = new UserProfileProcesser(context);
//        }else if (processType == ProcessType.ROUND_WIDTH) {
//			processer = RoundCustomWidthProcesser.getSmallRound(context);
//		}

		return processer;
	}

}
