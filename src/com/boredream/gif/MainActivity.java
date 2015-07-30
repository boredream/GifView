package com.boredream.gif;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.boredream.gif.widget.GifView;
import com.boredream.gif.widget.GifView.OnGifPlayingListener;

public class MainActivity extends Activity {

	private GifView gif1;
	@SuppressWarnings("unused")
	private GifView gif2;
	private GifView gif3;
	private SeekBar sb_time;
	private Button btn_pause_or_start;

	private boolean startDrag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		gif1 = (GifView) findViewById(R.id.gif1);
		gif2 = (GifView) findViewById(R.id.gif2);
		gif3 = (GifView) findViewById(R.id.gif3);
		sb_time = (SeekBar) findViewById(R.id.pb_time);
		btn_pause_or_start = (Button) findViewById(R.id.btn_pause_or_start);

		// 播放监听,同步seekbar的进度
		gif3.setOnGifPlayingListener(new OnGifPlayingListener() {
			@Override
			public void onProgress(int time) {
				// 正在拖动时不更新seekbar进度
				if (!startDrag) {
					sb_time.setProgress(time);
				}
			}
		});

		// seekbar变化监听,利用帧播放更新gif图片
		sb_time.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				startDrag = false;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				startDrag = true;
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// 自动播放时也会调用该进度变化方法,因此这里利用标志位判断,只有手动拖动时才进行处理
				if (!fromUser) {
					return;
				}

				gif3.seekTo(progress);
			}
		});

		// 暂停/播放按钮
		btn_pause_or_start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (gif3.isPaused()) {
					gif3.resume();
				} else {
					gif3.pause();
				}
			}
		});

		// xml中已经设置过gif,直接获取
		// int duration = gif2.getDuration();
		// sb_time.setMax(duration);

		try {
			// 获取asset中的文件,读取为流,模拟网络获取gif图片文件流的情况
			InputStream is1 = getAssets().open("se.webp");
			gif1.setGifInputStream(is1);

			InputStream is3 = getAssets().open("se.apng");
			gif3.setGifInputStream(is3);

			// 获取gif资源以后再设置相关数据
			int duration = gif3.getDuration();
			sb_time.setMax(duration);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}