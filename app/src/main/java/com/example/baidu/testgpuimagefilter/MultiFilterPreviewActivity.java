package com.example.baidu.testgpuimagefilter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.baidu.testgpuimagefilter.gles.EglCore;
import com.example.baidu.testgpuimagefilter.gles.GlUtil;
import com.example.baidu.testgpuimagefilter.gles.WindowSurface;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.baidu.testgpuimagefilter.GPUImageFilterTools.FilterType.CONTRAST;
import static com.example.baidu.testgpuimagefilter.GPUImageFilterTools.FilterType.EMBOSS;
import static com.example.baidu.testgpuimagefilter.GPUImageFilterTools.FilterType.FILTER_GROUP;
import static com.example.baidu.testgpuimagefilter.GPUImageFilterTools.FilterType.GAMMA;
import static com.example.baidu.testgpuimagefilter.GPUImageFilterTools.FilterType.GRAYSCALE;
import static com.example.baidu.testgpuimagefilter.GPUImageFilterTools.FilterType.NOFILTER;
import static com.example.baidu.testgpuimagefilter.GPUImageFilterTools.FilterType.SEPIA;
import static com.example.baidu.testgpuimagefilter.GPUImageFilterTools.FilterType.SHARPEN;
import static com.example.baidu.testgpuimagefilter.GPUImageFilterTools.FilterType.THREE_X_THREE_CONVOLUTION;

/**
 * Created by baidu on 2017/3/1.
 */

public class MultiFilterPreviewActivity extends Activity implements AdapterView.OnItemClickListener, SurfaceTexture.OnFrameAvailableListener{
    public static final String TAG = "MultiFilter";
    private GridView mGridView;
    private ArrayList<GridItem> mGridData;
    private GridViewAdapter mGridViewAdapter;
    MediaPlayer mediaPlayer;
    SurfaceTexture mediaSurfaceTexture;

    private String[] arrText = new String[]{
            "No Filter", "CONTRAST", "GRAYSCALE",
            "SHARPEN", "SEPIA", "GAMMA",
            "THREE_X_THREE_CONVOLUTION", "FILTER_GROUP", "EMBOSS"
    };
    private GPUImageFilterTools.FilterType[] arrImages=new GPUImageFilterTools.FilterType[]{
            NOFILTER, CONTRAST, GRAYSCALE,
            SHARPEN, SEPIA, GAMMA,
            THREE_X_THREE_CONVOLUTION, FILTER_GROUP, EMBOSS
    };

    TextureView oneTextureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        oneTextureView = new TextureView(this);
        setContentView(oneTextureView, new ViewGroup.LayoutParams(-1, -1));

//        setContentView(R.layout.activity_multi_preview);
//
//        mGridView = (GridView) findViewById(R.id.gridView);
//
//        mGridData = new ArrayList<GridItem>();
//        for (int i = 0; i < arrText.length; i++) {
//            GridItem item = new GridItem();
//            item.setTitle(arrText[i]);
//            item.setFilterType(arrImages[i]);
//            mGridData.add(item);
//        }
//        mGridViewAdapter = new GridViewAdapter(this, R.layout.grid_item, mGridData);
//        mGridView.setAdapter(mGridViewAdapter);
//        mGridView.setOnItemClickListener(this);

        mediaPlayer = MediaPlayer.create(this, R.raw.video_480x360_mp4_h264_500kbps_30fps_aac_stereo_128kbps_44100hz);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        mediaSurfaceTexture = new SurfaceTexture(getPreviewTexture());
        mediaSurfaceTexture.setOnFrameAvailableListener(this);
//        EglCore mEglCore = new EglCore(null, EglCore.FLAG_TRY_GLES3);
//        WindowSurface windowSurface = new WindowSurface(mEglCore, mediaSurfaceTexture);
//        windowSurface.makeCurrent();
        mediaPlayer.setSurface(new Surface(mediaSurfaceTexture));

        oneTextureView.setSurfaceTextureListener(mRender);
        mRender.start();
    }

//    private HandlerThread eventThread = new HandlerThread() {
//
//    };

    private Renderer mRender = new Renderer();
    /**
     * Handles GL rendering and SurfaceTexture callbacks.
     * <p>
     * We don't create a Looper, so the SurfaceTexture-by-way-of-TextureView callbacks
     * happen on the UI thread.
     */
    private class Renderer extends Thread implements TextureView.SurfaceTextureListener {
        private Object mLock = new Object();        // guards mSurfaceTexture, mDone
        private volatile SurfaceTexture mSurfaceTexture;
//        private EglCore mEglCore;
        private boolean mDone;

        public boolean isCreated() {
            return isCreated;
        }

        private volatile boolean isCreated = false;

        public Renderer() {
            super("TextureViewGL Renderer");
        }

        public void notifyLock() {
            synchronized (mLock) {
                mLock.notifyAll();
            }
        }

        @Override
        public void run() {
            while (true) {
                SurfaceTexture surfaceTexture = null;

                // Latch the SurfaceTexture when it becomes available.  We have to wait for
                // the TextureView to create it.
//                && (surfaceTexture = mSurfaceTexture) == null
                synchronized (mLock) {
                    if (!mDone ) {
                        try {
                            mLock.wait();
                        } catch (InterruptedException ie) {
                            throw new RuntimeException(ie);     // not expected
                        }
                    }
                    if (mDone) {
                        break;
                    }
                }
                Log.d(TAG, "Got surfaceTexture=" + surfaceTexture);
                if (mSurfaceTexture == null) {
                    continue;
                }
                // Create an EGL surface for our new SurfaceTexture.  We're not on the same
                // thread as the SurfaceTexture, which is a concern for the *consumer*, which
                // wants to call updateTexImage().  Because we're the *producer*, i.e. the
                // one generating the frames, we don't need to worry about being on the same
                // thread.
//                mEglCore = new EglCore(null, EglCore.FLAG_TRY_GLES3);
//                WindowSurface windowSurface = new WindowSurface(mEglCore, mSurfaceTexture);
//                windowSurface.makeCurrent();
//
//                // Render frames until we're told to stop or the SurfaceTexture is destroyed.
//                doAnimation(windowSurface);
//
//                windowSurface.release();
//                mEglCore.release();

                WindowSurface windowSurface = new WindowSurface(mEglCore, mSurfaceTexture);
                windowSurface.makeCurrent();

//                mediaSurfaceTexture.updateTexImage();

                // Render frames until we're told to stop or the SurfaceTexture is destroyed.
        doAnimation(windowSurface);
                windowSurface.swapBuffers();
                windowSurface.release();




//                if (!sReleaseInCallback) {
//                    Log.i(TAG, "Releasing SurfaceTexture in renderer thread");
//                    surfaceTexture.release();
//                }
            }

            Log.d(TAG, "Renderer thread exiting");
        }

        /**
         * Draws updates as fast as the system will allow.
         * <p>
         * In 4.4, with the synchronous buffer queue queue, the frame rate will be limited.
         * In previous (and future) releases, with the async queue, many of the frames we
         * render may be dropped.
         * <p>
         * The correct thing to do here is use Choreographer to schedule frame updates off
         * of vsync, but that's not nearly as much fun.
         */
        private void doAnimation(WindowSurface eglSurface) {
            final int BLOCK_WIDTH = 80;
            final int BLOCK_SPEED = 2;
            float clearColor = 0.0f;
            int xpos = -BLOCK_WIDTH / 2;
            int xdir = BLOCK_SPEED;
            int width = eglSurface.getWidth();
            int height = eglSurface.getHeight();

            Log.d(TAG, "Animating " + width + "x" + height + " EGL surface");

            while (true) {
                // Check to see if the TextureView's SurfaceTexture is still valid.
                synchronized (mLock) {
                    SurfaceTexture surfaceTexture = mSurfaceTexture;
                    if (surfaceTexture == null) {
                        Log.d(TAG, "doAnimation exiting");
                        return;
                    }
                }

                // Still alive, render a frame.
                GLES20.glClearColor(clearColor, clearColor, clearColor, 1.0f);
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

                GLES20.glEnable(GLES20.GL_SCISSOR_TEST);
                GLES20.glScissor(xpos, height / 4, BLOCK_WIDTH, height / 2);
                GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
                GLES20.glDisable(GLES20.GL_SCISSOR_TEST);

                // Publish the frame.  If we overrun the consumer, frames will be dropped,
                // so on a sufficiently fast device the animation will run at faster than
                // the display refresh rate.
                //
                // If the SurfaceTexture has been destroyed, this will throw an exception.
                eglSurface.swapBuffers();

                // Advance state
                clearColor += 0.015625f;
                if (clearColor > 1.0f) {
                    clearColor = 0.0f;
                }
                xpos += xdir;
                if (xpos <= -BLOCK_WIDTH / 2 || xpos >= width - BLOCK_WIDTH / 2) {
                    Log.d(TAG, "change direction");
                    xdir = -xdir;
                }
            }
        }

        /**
         * Tells the thread to stop running.
         */
        public void halt() {
            synchronized (mLock) {
                mDone = true;
                mLock.notify();
            }
        }

        @Override   // will be called on UI thread
        public void onSurfaceTextureAvailable(SurfaceTexture st, int width, int height) {
            Log.d(TAG, "onSurfaceTextureAvailable(" + width + "x" + height + ")");

//            mediaPlayer.setSurface(new Surface(oneTextureView.getSurfaceTexture()));

            isCreated = true;
            synchronized (mLock) {
                mSurfaceTexture = st;
//                mLock.notify();
            }
        }

        @Override   // will be called on UI thread
        public void onSurfaceTextureSizeChanged(SurfaceTexture st, int width, int height) {
            Log.d(TAG, "onSurfaceTextureSizeChanged(" + width + "x" + height + ")");
            // TODO: ?
        }

        @Override   // will be called on UI thread
        public boolean onSurfaceTextureDestroyed(SurfaceTexture st) {
            Log.d(TAG, "onSurfaceTextureDestroyed");
            isCreated = false;
            // We set the SurfaceTexture reference to null to tell the Renderer thread that
            // it needs to stop.  The renderer might be in the middle of drawing, so we want
            // to return false here so that the caller doesn't try to release the ST out
            // from under us.
            //
            // In theory.
            //
            // In 4.4, the buffer queue was changed to be synchronous, which means we block
            // in dequeueBuffer().  If the renderer has been running flat out and is currently
            // sleeping in eglSwapBuffers(), it's going to be stuck there until somebody
            // tears down the SurfaceTexture.  So we need to tear it down here to ensure
            // that the renderer thread will break.  If we don't, the thread sticks there
            // forever.
            //
            // The only down side to releasing it here is we'll get some complaints in logcat
            // when eglSwapBuffers() fails.
            synchronized (mLock) {
                mSurfaceTexture = null;
            }
//            if (sReleaseInCallback) {
//                Log.i(TAG, "Allowing TextureView to release SurfaceTexture");
//            }
            halt();
            return true;
        }

        @Override   // will be called on UI thread
        public void onSurfaceTextureUpdated(SurfaceTexture st) {
            //Log.d(TAG, "onSurfaceTextureUpdated");
        }
    }

    public int getPreviewTexture() {
        int textureId = -1;
        if (textureId == GlUtil.NO_TEXTURE) {
            textureId = GlUtil.createTextureObject(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        }
        return textureId;
    }

    EglCore mEglCore = new EglCore(null, EglCore.FLAG_RECORDABLE);

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {

        // surface available
        if (!mRender.isCreated()) {
            surfaceTexture.updateTexImage();
            return;
        }

        mRender.notifyLock();

//        WindowSurface windowSurface = new WindowSurface(mEglCore, oneTextureView.getSurfaceTexture());
//        windowSurface.makeCurrent();
//
//        surfaceTexture.updateTexImage();
//
//        // Render frames until we're told to stop or the SurfaceTexture is destroyed.
////        doAnimation(windowSurface);
//        windowSurface.swapBuffers();
//        windowSurface.release();
//        mEglCore.release();
    }


    public class GridViewAdapter extends ArrayAdapter<GridItem> {

        private Context mContext;
        private int layoutResourceId;
        private ArrayList<GridItem> mGridData = new ArrayList<GridItem>();

        public GridViewAdapter(Context context, int resource, ArrayList<GridItem> objects) {
            super(context, resource, objects);
            this.mContext = context;
            this.layoutResourceId = resource;
            this.mGridData = objects;
        }

        public void setGridData(ArrayList<GridItem> mGridData) {
            this.mGridData = mGridData;
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
                convertView = inflater.inflate(layoutResourceId, parent, false);
                holder = new ViewHolder();
                holder.textView = (TextView) convertView.findViewById(R.id.tv_title_in_grid);
                holder.textureView = (TextureView) convertView.findViewById(R.id.texture_in_grid);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            GridItem item = mGridData.get(position);
            holder.textView.setText(item.getTitle());

            return convertView;
        }

        private class ViewHolder {
            TextView textView;
            TextureView textureView;
        }
    }

    public class GridItem {
        private GPUImageFilterTools.FilterType filterType;
        private String title;

        public GridItem() {
            super();
        }
        public GPUImageFilterTools.FilterType getFilterType() {
            return filterType;
        }
        public void setFilterType(GPUImageFilterTools.FilterType image) {
            this.filterType = image;
        }
        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }
    }


    /**
     * GridView的点击回调函数
     *
     * @param adapter  -- GridView对应的dapterView
     * @param view     -- AdapterView中被点击的视图(它是由adapter提供的一个视图)。
     * @param position -- 视图在adapter中的位置。
     * @param rowid    -- 被点击元素的行id。
     */
    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int position, long rowid) {
        String index = arrImages[position].name();
        Intent intent = new Intent();
        intent.putExtra("filter", index);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        // destroy files
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (mediaSurfaceTexture != null) {
            mediaSurfaceTexture.release();
        }

        if (mEglCore != null) {
            mEglCore.release();
        }
        super.onDestroy();
    }
}
