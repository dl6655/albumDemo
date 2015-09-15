package com.lq.albumDemo.image.progress;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import com.lq.albumDemo.R;
import com.lq.albumDemo.image.WebImageView;

/**
 * 实现猎趣的加载动画
 */
public class DefaultProgressRender implements WebImageView.ImageProgressListener {

	private static final String TAG = "DefaultProgressRender";

	/** 处于加载的状态 */
	private boolean mInProgressStatus = false;

	/** 进度条值 0.0f - 1.0f */
	private float mProgressValue = 0.0f;

    private Context mContext;

	private Paint paint = new Paint();

    private Paint xPaint = new Paint();
    private int mProgressWodth;

    public DefaultProgressRender(Context context) {
		init(context);
	}

	private void init(Context context) {
        mContext = context;
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        paint.setColor(0xff929292);
        mProgressWodth = mContext.getResources().getDimensionPixelOffset(R.dimen.web_image_view_progress_radius);
    }

	@Override
	public void onStateChange(String imageUri, View view, int newState) {
		mInProgressStatus = newState == STATE_STARTED;
		if (view != null) {
			view.postInvalidate();
		}
	}

	@Override
	public void onProgressUpdate(String imageUri, View view, int current, int total) {
		mInProgressStatus = true;
		if (total > 0) {
			mProgressValue = (float) current / (float) total;
		}
		if (view != null) {
			view.postInvalidate();
		}
	}

	@Override
	public void draw(Canvas canvas, int width, int height) {
        if (mInProgressStatus) {
            int left = (width - mProgressWodth) >> 1;
            int top = (height - mProgressWodth) >> 1;
            RectF bufferRect = new RectF(left, top, mProgressWodth +left, mProgressWodth +top);

            float sweepAngle = 360 * (mProgressValue);
            canvas.drawArc(bufferRect, 0, sweepAngle, false, paint);
        }
    }

	@Override
	public void reset() {
		mInProgressStatus = false;
		mProgressValue = 0.0f;
	}

}
