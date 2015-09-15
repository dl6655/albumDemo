package com.lq.albumDemo.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import com.lq.albumDemo.image.processer.ImageProcesserFactory;
import com.lq.albumDemo.image.progress.ProgressRenderFactory;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

/**
 * 网络图片控件
 * @author changxiang
 *
 * 在使用该控件之前，需要在application里调用{@link ImageHelper#initConfig(Context)}
 *
 * 支持的imageUri有以下5种格式：
 *    String imageUri = "http://site.com/image.png"; // from Web
      String imageUri = "file:///mnt/sdcard/image.png"; // from SD card
      String imageUri = "content://media/external/audio/albumart/13"; // from content provider
      String imageUri = "assets://image.png"; // from assets
      String imageUri = "drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
 */
public class WebImageView extends ImageView implements ImageLoadingListener, ImageLoadingProgressListener {
	
	private ImageProgressListener mImageProgressListener;

	public WebImageView(Context context) {
		super(context);
	}

	public WebImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WebImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

    /**
* 设置图片
* @param url 图片地址
*/
    public void setImageUrl(String url) {
        setImageUrl(url, false);
    }

    /**
     * 设置图片,使用Bitmap.Config.RGB_565解析图片
     * 可以节省内存的使用
     * @param url 图片地址
     * @param use565
     */
	public void setImageUrl(String url, boolean use565){
		setImageUrl(url, 0, null, null, use565);
	}
	
	/**
	 * 
	 * @param url
	 * @param imageRes
	 * @param use565
	 * @param flag 这个参数没有用 是和另一个方法区分用的
	 */
	public void setImageUrl(String url, int imageRes,boolean use565,boolean flag){
	    setImageUrl(url, imageRes, null, null, use565);
	}
	
	/**
     * 设置图片
     * @param url 地址
     * @param imageRes  默认图片，URL为空，图片加载中，加载失败时显示的
     */
	public void setImageUrl(String url, int imageRes){
		setImageUrl(url, imageRes, null, null);
	}
	
	/**
     * 设置图片
     * @param url 地址
     * @param imageRes  默认图片，URL为空，图片加载中，加载失败时显示的
     * @param useDefaultProgressRender 是否使用默认的进度条显示
     */
    public void setImageUrl(String url, int imageRes, boolean useDefaultProgressRender){
    	setImageUrl(url, imageRes, ProgressRenderFactory.ProgressRenderType.DEFAULT, null);
	}
    
	/**
     * 设置图片
     * @param url 地址
     * @param imageRes  默认图片，URL为空，图片加载中，加载失败时显示的
     * @param progressType 进度监听器，调用者在此做进度的渲染 {@link ProgressRenderFactory.ProgressRenderType}
     */
    public void setImageUrl(String url, int imageRes, ProgressRenderFactory.ProgressRenderType progressType){
		setImageUrl(url, imageRes, progressType, null);
	}
    
    /**
     * 设置图片
     * @param url 地址
     * @param imageRes  默认图片，URL为空，图片加载中，加载失败时显示的
     * @param type 回调图片的处理类型，通过{@link ImageProcesserFactory.ProcessType}获取对应的处理器
     */
    public void setImageUrl(String url, int imageRes, ImageProcesserFactory.ProcessType type){
		setImageUrl(url, imageRes, null, type);
	}
  
    /**
     * 设置图片
     * @param url 地址
     * @param imageRes  默认图片，URL为空，图片加载中，加载失败时显示的
     * @param useDefaultProgressRender 是否使用默认的进度条显示
     * @param processType 回调图片的处理类型，通过{@link ImageProcesserFactory.ProcessType}获取对应的处理器  
     */
	public void setImageUrl(String url, int imageRes, boolean useDefaultProgressRender, ImageProcesserFactory.ProcessType processType) {
		setImageUrl(url, imageRes, ProgressRenderFactory.ProgressRenderType.DEFAULT, processType);
	}

    /**
     * 设置图片
     * @param url 地址
     * @param imageRes  默认图片，URL为空，图片加载中，加载失败时显示的
     * @param progressType 进度监听器，调用者在此做进度的渲染 {@link com.lq.albumDemo.image.progress.ProgressRenderFactory.ProgressRenderType}
     * @param processType 回调图片的处理类型，通过{@link com.lq.albumDemo.image.processer.ImageProcesserFactory.ProcessType}获取对应的处理器
     */
    public void setImageUrl(String url, int imageRes, ProgressRenderFactory.ProgressRenderType progressType, ImageProcesserFactory.ProcessType processType) {
        setImageUrl(url, imageRes, progressType, processType, false);
    }
	/**
	 * 设置图片
	 * @param url 地址
	 * @param imageRes  默认图片，URL为空，图片加载中，加载失败时显示的
	 * @param progressType 进度监听器，调用者在此做进度的渲染 {@link com.lq.albumDemo.image.progress.ProgressRenderFactory.ProgressRenderType}
	 *
	 * @param processType 回调图片的处理类型，通过{@link com.lq.albumDemo.image.processer.ImageProcesserFactory.ProcessType}获取对应的处理器
	 * @param use565      使用Bitmap.Config.RGB_565解析图片
	 */
	public void setImageUrl(String url, int imageRes, ProgressRenderFactory.ProgressRenderType progressType, ImageProcesserFactory.ProcessType processType, boolean use565){
		//考虑到在复用情况下，进度条的风格也是一样的，所以ImageProgressListener只在为空时获取
		if (mImageProgressListener == null && progressType != ProgressRenderFactory.ProgressRenderType.NONE){
			mImageProgressListener = ProgressRenderFactory.getInstance().getProgressRender(getContext(), progressType);
		}

		if (mImageProgressListener != null) {
			mImageProgressListener.reset();
			ProgressManager.getInstance().addProgressListener(url, this);
			ImageHelper.displayImage(this, url, imageRes, this, this, processType, use565);
		} else {
			ImageHelper.displayImage(this, url, imageRes, null, null, processType, use565);
		}
	}
	/** 获取改对象的进度观察者 */
	public ImageProgressListener getImageProgressListener() {
		return mImageProgressListener;
	}
    /**
     * 处理自定义的绘制      因为有些继承该控件的自定义控件在@#onDraw(Canvas)}中没有调用super.onDraw
     */
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if(mImageProgressListener != null){
			mImageProgressListener.draw(canvas, getWidth(), getHeight());
		}
	}
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		
//		ImageLoader.getInstance().cancelDisplayTask(this);
	}

	/**
	 * 下载进度观察者
	 */
	public interface ImageProgressListener {
		
		public final int STATE_STARTED = 1;
		public final int STATE_FAILED = 2;
		public final int STATE_COMPLETE = 3;
		public final int STATE_CANCELLED = 4;
		
		/**
		 * 状态变化
		 * @param imageUri
		 * @param view
		 * @param newState
		 */
		public void onStateChange(String imageUri, View view, int newState);
		
		/**
		 * Is called when image loading progress changed.
		 *
		 * @param imageUri Image URI
		 * @param view     View for image. Can be <b>null</b>.
		 * @param current  Downloaded size in bytes
		 * @param total    Total size in bytes
		 */
		public void onProgressUpdate(String imageUri, View view, int current, int total);
		
		/**
		 * 自定义进度绘制
		 * @param canvas
		 * @param width     imageview的宽度
		 * @param height    imageview的高度
		 */
		public void draw(Canvas canvas, int width, int height);
		
		/** 重置数据 */
		public void reset();
		
	}
	
	/**
	 * 回调图片的处理  会存储到memory cache中， 目前使用第三方的网络图片模块的 接口
	 * {@link BitmapProcessor#process(Bitmap bitmap)}
	 */
	public interface ImageProcesser extends BitmapProcessor {
		
	}

	@Override
	public void onProgressUpdate(String imageUri, View view, int current, int total) {
		// TODO Auto-generated method stub
		ProgressManager.getInstance().notifyProgressChanged(imageUri, view, current, total);
	}

	@Override
	public void onLoadingStarted(String imageUri, View view) {
		// TODO Auto-generated method stub
		ProgressManager.getInstance().notifyStateChanged(imageUri, view, ImageProgressListener.STATE_STARTED);
	}

	@Override
	public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
		// TODO Auto-generated method stub
		ProgressManager.getInstance().notifyStateChanged(imageUri, view, ImageProgressListener.STATE_FAILED);
	}

	@Override
	public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
		// TODO Auto-generated method stub
		ProgressManager.getInstance().notifyStateChanged(imageUri, view, ImageProgressListener.STATE_COMPLETE);
	}

	@Override
	public void onLoadingCancelled(String imageUri, View view) {
		// TODO Auto-generated method stub
		ProgressManager.getInstance().notifyStateChanged(imageUri, view, ImageProgressListener.STATE_CANCELLED);
	}
}
