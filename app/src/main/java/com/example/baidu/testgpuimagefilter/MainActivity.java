package com.example.baidu.testgpuimagefilter;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import jp.co.cyberagent.android.gpuimage.GPUImageExtRotationTexFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilterGroup;


public class MainActivity extends Activity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private VideoSurfaceView videoSurfaceView = null;
    private MediaPlayer mediaPlayer = null;

    private GPUImageFilter mFilter;
    private GPUImageFilterTools.FilterAdjuster mFilterAdjuster;
    private GPUImageFilterTools.FilterType currentFilterType = GPUImageFilterTools.FilterType.NOFILTER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ((SeekBar) findViewById(R.id.seekBar)).setOnSeekBarChangeListener(this);
        findViewById(R.id.button_choose_filter).setOnClickListener(this);
        findViewById(R.id.btnGenerate).setOnClickListener(this);
        findViewById(R.id.btnMulti).setOnClickListener(this);
        /**
         * 初始化显示view
         */
        initGLView();

    }

    private volatile boolean isGenerating = false;
    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.button_choose_filter:
                GPUImageFilterTools.showDialog(this, new GPUImageFilterTools.OnGpuImageFilterChosenListener() {

                    @Override
                    public void onGpuImageFilterChosenListener(final GPUImageFilter filter, final GPUImageFilterTools.FilterType filterType) {
                        currentFilterType = filterType;
                        switchFilterTo(filter);
                    }
                });
                break;
            case R.id.btnMulti:
                Intent intent = new Intent(MainActivity.this, MultiFilterPreviewActivity.class);
                this.startActivityForResult(intent, 10000);
                break;
            case R.id.btnGenerate:
                if (isGenerating) {
                    Toast.makeText(MainActivity.this, "已经有一个正在生成的任务，请等待。。", Toast.LENGTH_LONG).show();
                    return;
                }
                isGenerating = true;
                final ResultListener resultListener = new ResultListener() {
                    @Override
                    public void onResult(final boolean success, final String extra) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                isGenerating = false;
                                Toast.makeText(MainActivity.this, success? "Generate Sucess!" : "GenerateFailed! reason="
                                        + extra, Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                };
                // do effect-reencode
                ExtractDecodeEditEncodeMuxTest test = new ExtractDecodeEditEncodeMuxTest(MainActivity.this);
                try {
                    test.setFilterType(currentFilterType);
                    test.testExtractDecodeEditEncodeMuxAudioVideo(resultListener);

                } catch (Throwable tr) {

                }
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10000) {
            if (resultCode == RESULT_OK) {
                // set the selected filter
                String filterSelect = data.getStringExtra("filter");
                GPUImageFilterTools.FilterType filterType = GPUImageFilterTools.FilterType.valueOf(filterSelect);
                switchFilterTo(GPUImageFilterTools.createFilterForType(this, filterType));
            }
        }
    }

    private void switchFilterTo(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            mFilter = filter;
//            mGPUImage.setFilter(mFilter);
            GPUImageFilterGroup filterGroup = new GPUImageFilterGroup();
            filterGroup.addFilter(new GPUImageExtTexFilter());
            filterGroup.addFilter(mFilter);
            videoSurfaceView.setFilter(filterGroup);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
    }

    @Override
    public void onProgressChanged(final SeekBar seekBar, final int progress,
                                  final boolean fromUser) {
        if (mFilterAdjuster != null) {
            mFilterAdjuster.adjust(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(final SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(final SeekBar seekBar) {
    }

    public void initGLView() {
        RelativeLayout rlGlViewContainer = (RelativeLayout)findViewById(R.id.rlGlViewContainer);
        mediaPlayer = MediaPlayer.create(this, R.raw.we_chat_sight723);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        videoSurfaceView = new VideoSurfaceView(this, mediaPlayer);
        videoSurfaceView.setSourceSize(mediaPlayer.getVideoWidth(), mediaPlayer.getVideoHeight());

        RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(-1, -1);
        rlGlViewContainer.addView(videoSurfaceView, rllp);
    }

    static public interface ResultListener {
        void onResult(boolean success, String extra);
    }

    @Override
    protected void onPause() {
        // do sth
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // do sth
        mediaPlayer.start();
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        super.onDestroy();
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
//    public native String stringFromJNI();
}
