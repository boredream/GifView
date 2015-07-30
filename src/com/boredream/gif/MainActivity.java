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

		// ���ż���,ͬ��seekbar�Ľ���
		gif3.setOnGifPlayingListener(new OnGifPlayingListener() {
			@Override
			public void onProgress(int time) {
				// �����϶�ʱ������seekbar����
				if (!startDrag) {
					sb_time.setProgress(time);
				}
			}
		});

		// seekbar�仯����,����֡���Ÿ���gifͼƬ
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
				// �Զ�����ʱҲ����øý��ȱ仯����,����������ñ�־λ�ж�,ֻ���ֶ��϶�ʱ�Ž��д���
				if (!fromUser) {
					return;
				}

				gif3.seekTo(progress);
			}
		});

		// ��ͣ/���Ű�ť
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

		// xml���Ѿ����ù�gif,ֱ�ӻ�ȡ
		// int duration = gif2.getDuration();
		// sb_time.setMax(duration);

		try {
			// ��ȡasset�е��ļ�,��ȡΪ��,ģ�������ȡgifͼƬ�ļ��������
			InputStream is1 = getAssets().open("se.webp");
			gif1.setGifInputStream(is1);

			InputStream is3 = getAssets().open("se.apng");
			gif3.setGifInputStream(is3);

			// ��ȡgif��Դ�Ժ��������������
			int duration = gif3.getDuration();
			sb_time.setMax(duration);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}