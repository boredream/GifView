package com.boredream.gif.widget;

import java.io.InputStream;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Build;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import com.boredream.gif.R;

public class GifView extends View {
	private Resources resources;
	
	/**
	 * ����gif��Ӱ�ӹ�����
	 */
	private Movie mMovie;
	
	/**
	 * 0-����״̬ 1-��ͣ״̬
	 */
	private int playStatus;
	/**
	 * gif������ʼʱ��
	 */
	private long mMovieStart;
	/**
	 * gif��ǰ���Ž���ʱ��
	 */
	private int relTime;
	/**
	 * ��ͣ/֡�������²��ŵĲ���ʱ��
	 */
	private int offsetTime;
	
	/**
	 * ��ȵ����ű���(�ؼ����:gifͼƬ���)
	 */
	private float ratioWidth;
	/**
	 * �߶ȵ����ű���(�ؼ��߶�:gifͼƬ�߶�)
	 */
	private float ratioHeight;

	public GifView(Context context) {
		this(context, null);
	}
	
	public GifView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public GifView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs, defStyleAttr);
	}

	private void init(Context context, AttributeSet attrs, int defStyleAttr) {
		setFocusable(true);
		
		// 3.0����ϵͳ���Զ���Ӳ������,��Ҫ�ֶ��رղſ�����������gif
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}

		resources = context.getResources();
		
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.GifView);
		int drawableResId = ta.getResourceId(R.styleable.GifView_src, -1);
		setGifResource(drawableResId);
		
		ta.recycle();
	}

	/**
	 * ����gif��Դ(�����ڱ���drawableͼƬ)
	 * 
	 * @param resourceId gifͼƬ��id
	 */
	public void setGifResource(int resourceId) {
		if(resourceId == -1) {
			return;
		}
		InputStream is = resources.openRawResource(resourceId);
		mMovie = Movie.decodeStream(is);
		requestLayout();
	}
	
	/**
	 * ����gif������(����������ͼƬ)
	 * 
	 * @param InputStream gifͼƬ��������
	 */
	public void setGifInputStream(InputStream is) {
		mMovie = Movie.decodeStream(is);
		requestLayout();
	}

	/**
	 * ��ͣ����
	 */
	public void pause() {
		playStatus = 1;
		// ��ʼʱ����Ϊ0,onDrawaʱ�����»�ȡ
		mMovieStart = 0;
		// ��¼��ͣʱ�Ѿ����ŵĽ���,�����²���ʱ���в�������
		offsetTime = relTime;
		
		invalidate();
	}
	
	/**
	 * �ָ�����
	 */
	public void resume() {
		playStatus = 0;
		
		invalidate();
	}
	
	/**
	 * ��ͷ��ʼ����
	 */
	public void restart() {
		playStatus = 0;
		// ��ʼʱ����Ϊ0,onDrawaʱ�����»�ȡ
		mMovieStart = 0;
		
		invalidate();
	}
	
	/**
	 * �Ƿ�Ϊ��ͣ״̬
	 * 
	 * @return true-��ͣ  false-������
	 */
	public boolean isPaused() {
		// ��ͣ��֡���Ŷ�����pause
		return playStatus != 0;
	}
	
	/**
	 * �ƶ���ָ������
	 * 
	 * @param progress ͣ������,С��0�����gif�����ܳ���ʱ��Ч
	 */
	public void seekTo(int progress) {
		if(mMovie == null) {
			return;
		}
		
		if(progress >= 0 && progress < mMovie.duration()) {
			// ��ʼʱ����Ϊ0,onDrawʱ�����ÿ�ʼʱ��
			mMovieStart = 0;
			// ��¼��ǰ���õĽ���,�����²���ʱ���в�������
			offsetTime = progress;
			invalidate();
		}
	}
	
	/**
	 * ��ȡgif������ǰ����
	 * 
	 * @return ����ֵ,����Ϊ��ʱ,����-1
	 */
	public int getProgress() {
		if(mMovie == null) {
			return - 1;
		}
		return relTime;
	}
	
	/**
	 * ��ȡgif������ʱ��
	 * 
	 * @return ��ʱ��,����Ϊ��ʱ,����-1
	 */
	public int getDuration() {
		if(mMovie == null) {
			return - 1;
		}
		return mMovie.duration();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// gif�����ǿ�ʱ���д�С����,���㷽������ImageView�е�onMeasure
		if (mMovie != null) {
			int w;
	        int h;

	        // ��ȡgif���
            w = mMovie.width();
            h = mMovie.height();
            if (w <= 0) w = 1;
            if (h <= 0) h = 1;

	        int pleft = getPaddingLeft();
	        int pright = getPaddingRight();
	        int ptop = getPaddingTop();
	        int pbottom = getPaddingBottom();

	        int widthSize;
	        int heightSize;

            /* We are either don't want to preserve the drawables aspect ratio,
               or we are not allowed to change view dimensions. Just measure in
               the normal way.
            */
            w += pleft + pright;
            h += ptop + pbottom;
                
            w = Math.max(w, getSuggestedMinimumWidth());
            h = Math.max(h, getSuggestedMinimumHeight());

            // ���ݿ�ߵ�MeasureSpec��������ֵ,�����ǰ�ؼ���Ҫ�Ĵ�С
            widthSize = resolveSizeAndState(w, widthMeasureSpec, 0);
            heightSize = resolveSizeAndState(h, heightMeasureSpec, 0);
            
            // ����ؼ���ߺ�gifͼ��ߵı���,����onDraw����ʱ��gifͼƬ���к�������,ʹ����Ӧ�ؼ���С
            ratioWidth = (float) widthSize / w;
            ratioHeight = (float) heightSize / h;
            
            // ���ÿؼ����
	        setMeasuredDimension(widthSize, heightSize);
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		long now = SystemClock.uptimeMillis();

		if (mMovie != null) {
			// ��ȡgif��������ʱ��
			int dur = mMovie.duration();
			if (dur == 0) {
				dur = 1000;
			}
			
			switch (playStatus) {
			case 0: // ����
				// ���¿�ʼ����ʱ,�ٴλ�ȡ��ʼʱ��
				if (mMovieStart == 0) {
					mMovieStart = now;
				}
				
				// ��������ʱ��  % ��������ʱ��,�����ǰ�������ŵĽ���ʱ��
				// ע���Ƴ�����ͣ/֡������ɵĶ���ʱ��ƫ��ֵoffsetTime
				relTime = (int) ((now - mMovieStart + offsetTime) % dur);
				
				// ���ò��Ž��ȼ���
				if(onGifPlayingListener != null) {
					onGifPlayingListener.onProgress(relTime);
				}
				break;
			case 1: // ��ͣ
				// �����½���ʱ��relTime,һֱͣ������ͣʱ��ͼƬ
				relTime = (int) offsetTime;
				break;
			default:
				break;
			}
			
			// �ö�����ĳ������ʱ��Ķ���
			mMovie.setTime(relTime);

			// �������ű���
			canvas.scale(Math.min(ratioWidth, ratioHeight), 
					Math.min(ratioWidth, ratioHeight));

			// ����
			mMovie.draw(canvas, 0, 0);
			
			invalidate();
		}
	}
	
	private OnGifPlayingListener onGifPlayingListener;
	
	public void setOnGifPlayingListener(OnGifPlayingListener onGifPlayingListener) {
		this.onGifPlayingListener = onGifPlayingListener;
	}

	public interface OnGifPlayingListener {
		void onProgress(int time);
	}

}
