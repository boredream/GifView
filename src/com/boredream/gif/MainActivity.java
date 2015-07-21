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
        
        gif3.setOnGifPlayingListener(new OnGifPlayingListener() {
			@Override
			public void onProgress(int time) {
				sb_time.setProgress(time);
			}
		});
        
        sb_time.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				System.out.println("onStopTrackingTouch");
				startDrag = false;
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				System.out.println("onStartTrackingTouch");
				startDrag = true;
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if(startDrag) {
					System.out.println("onProgressChanged = " + progress);
					gif3.setProgress(progress);
				}
			}
		});
        btn_pause_or_start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(gif3.isPaused()) {
					gif3.resume();
				} else {
					gif3.pause();
				}
			}
		});
        
        // xml中已经设置过gif,直接获取
//        int duration = gif2.getDuration();
//        sb_time.setMax(duration);
        
        try {
        	InputStream is1 = getAssets().open("nuodun.gif");
        	gif1.setGifInputStream(is1);
        	
        	InputStream is3 = getAssets().open("xinhengjieyi.gif");
        	gif3.setGifInputStream(is3);
        	
        	// xml中没有src,在获取gif资源以后再设置相关数据
        	int duration = gif3.getDuration();
        	sb_time.setMax(duration);
        	
        } catch (IOException e) {
        	e.printStackTrace();
        }
        
        
        
    }
}