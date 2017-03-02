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
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.example.baidu.testgpuimagefilter.gles.EglCore;
import com.example.baidu.testgpuimagefilter.gles.GlUtil;
import com.example.baidu.testgpuimagefilter.gles.WindowSurface;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilterGroup;
import jp.co.cyberagent.android.gpuimage.OpenGlUtils;

import static android.media.CamcorderProfile.get;
import static com.example.baidu.testgpuimagefilter.GPUImageFilterTools.FilterType.CONTRAST;
import static com.example.baidu.testgpuimagefilter.GPUImageFilterTools.FilterType.EMBOSS;
import static com.example.baidu.testgpuimagefilter.GPUImageFilterTools.FilterType.FILTER_GROUP;
import static com.example.baidu.testgpuimagefilter.GPUImageFilterTools.FilterType.GAMMA;
import static com.example.baidu.testgpuimagefilter.GPUImageFilterTools.FilterType.GRAYSCALE;
import static com.example.baidu.testgpuimagefilter.GPUImageFilterTools.FilterType.NOFILTER;
import static com.example.baidu.testgpuimagefilter.GPUImageFilterTools.FilterType.SEPIA;
import static com.example.baidu.testgpuimagefilter.GPUImageFilterTools.FilterType.SHARPEN;
import static com.example.baidu.testgpuimagefilter.GPUImageFilterTools.FilterType.THREE_X_THREE_CONVOLUTION;
import static jp.co.cyberagent.android.gpuimage.util.TextureRotationUtil.TEXTURE_NO_ROTATION;

/**
 * Created by baidu on 2017/3/1.
 */

public class MultiFilterPreviewActivity extends Activity implements AdapterView.OnItemClickListener,
        SurfaceTexture.OnFrameAvailableListener, SurfaceHolder.Callback {
    public static final String TAG = "MultiFilter";
    private GridView mGridView;
    private ArrayList<GridItem> mGridData;
    private GridViewAdapter mGridViewAdapter;
    MediaPlayer mediaPlayer;
    SurfaceTexture mediaSurfaceTexture;

    public static String[] arrText = new String[]{
            "No Filter", "CONTRAST", "GRAYSCALE",
            "SHARPEN", "SEPIA", "GAMMA",
            "THREE_X_THREE_CONVOLUTION", "FILTER_GROUP", "EMBOSS",
            "No Filter", "CONTRAST", "GRAYSCALE",
            "SHARPEN", "SEPIA", "GAMMA",
            "THREE_X_THREE_CONVOLUTION", "FILTER_GROUP", "EMBOSS"
    };
    public static GPUImageFilterTools.FilterType[] arrImages=new GPUImageFilterTools.FilterType[]{
            NOFILTER, CONTRAST, GRAYSCALE,
            SHARPEN, SEPIA, GAMMA,
            THREE_X_THREE_CONVOLUTION, FILTER_GROUP, EMBOSS,
            NOFILTER, CONTRAST, GRAYSCALE,
            SHARPEN, SEPIA, GAMMA,
            THREE_X_THREE_CONVOLUTION, FILTER_GROUP, EMBOSS
    };

    TextureView oneTextureView;

    int genTextureID = -1;

//    private static SurfaceHolder sSurfaceHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        oneTextureView = new TextureView(this);
//        setContentView(oneTextureView, new ViewGroup.LayoutParams(-1, -1));

//        SurfaceView sv = new SurfaceView(this);
//        SurfaceHolder sh = sv.getHolder();
//        sh.addCallback(this);
//        setContentView(sv, new ViewGroup.LayoutParams(-1, -1));

//        setContentView(R.layout.activity_multisurfaceview);
//        SurfaceHolder sh1 = ((SurfaceView)findViewById(R.id.surfaceView1)).getHolder();
//        sh1.addCallback(this);
//        SurfaceHolder sh2 = ((SurfaceView)findViewById(R.id.surfaceView2)).getHolder();
//        sh2.addCallback(this);

        setContentView(R.layout.activity_multi_preview);

        mGridView = (GridView) findViewById(R.id.gridView);

        mGridData = new ArrayList<GridItem>();
        for (int i = 0; i < arrText.length; i++) {
            GridItem item = new GridItem();
            item.setTitle(arrText[i]);
            item.setFilterType(arrImages[i]);
            mGridData.add(item);
        }
        mGridViewAdapter = new GridViewAdapter(this, R.layout.grid_item, mGridData);
        mGridView.setAdapter(mGridViewAdapter);
        mGridView.setOnItemClickListener(this);

//        mediaPlayer = MediaPlayer.create(this, R.raw.video_480x360_mp4_h264_500kbps_30fps_aac_stereo_128kbps_44100hz);
//        mediaPlayer.setLooping(true);
//        mediaPlayer.start();


        // do effect-reencode
//        ExtractDecodeTest test = new ExtractDecodeTest(this);
//        try {
//            final long startTime = System.currentTimeMillis();
//            MainActivity.ResultListener resultListener = new MainActivity.ResultListener() {
//                @Override
//                public void onResult(boolean success, String extra) {
//                    long endTime = System.currentTimeMillis();
//                    Log.d(TAG, "extractAndDecode time consuming=" + (endTime - startTime) + ";success=" + success);
//                }
//            };
//            test.setFilterType(GPUImageFilterTools.FilterType.NOFILTER);
//            test.testExtractDecodeEditEncodeMuxAudioVideo(resultListener);
//
//        } catch (Throwable tr) {
//            tr.printStackTrace();
//        }



//        EglCore mEglCore = new EglCore(null, EglCore.FLAG_TRY_GLES3);
//        WindowSurface windowSurface = new WindowSurface(mEglCore, mediaSurfaceTexture);
//        windowSurface.makeCurrent();
//        mediaPlayer.setSurface(new Surface(mediaSurfaceTexture));

        mHandler = new MainHandler(this);

//        oneTextureView.setSurfaceTextureListener(mRender);
//        mRender.start();
    }

//    private HandlerThread eventThread = new HandlerThread() {
//
//    };

    EglCore mEglCore;
    private Renderer mRender = new Renderer();

    @Override   // SurfaceHolder.Callback
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated holder=" + holder + " (static=" + ")");
//        if (sSurfaceHolder != null) {
//            throw new RuntimeException("sSurfaceHolder is already set");
//        }
//
//        sSurfaceHolder = holder;

        if (mRenderThread != null) {
            // Normal case -- render thread is running, tell it about the new surface.
            RenderHandler rh = mRenderThread.getHandler();
            rh.sendSurfaceAvailable(holder, true);
        } else {
            // Sometimes see this on 4.4.x N5: power off, power on, unlock, with device in
            // landscape and a lock screen that requires portrait.  The surface-created
            // message is showing up after onPause().
            //
            // Chances are good that the surface will be destroyed before the activity is
            // unpaused, but we track it anyway.  If the activity is un-paused and we start
            // the RenderThread, the SurfaceHolder will be passed in right after the thread
            // is created.
            Log.d(TAG, "render thread not running");
        }
    }

    @Override   // SurfaceHolder.Callback
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged fmt=" + format + " size=" + width + "x" + height +
                " holder=" + holder);

        if (mRenderThread != null) {
            RenderHandler rh = mRenderThread.getHandler();
            rh.sendSurfaceChanged(holder, format, width, height);
        } else {
            Log.d(TAG, "Ignoring surfaceChanged");
            return;
        }
    }

    @Override   // SurfaceHolder.Callback
    public void surfaceDestroyed(SurfaceHolder holder) {
        // In theory we should tell the RenderThread that the surface has been destroyed.
        if (mRenderThread != null) {
            RenderHandler rh = mRenderThread.getHandler();
            rh.sendSurfaceDestroyed(holder);
        }
        Log.d(TAG, "surfaceDestroyed holder=" + holder);
//        sSurfaceHolder = null;
    }
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

        public int randInt(int min, int max) {

            // NOTE: This will (intentionally) not run as written so that folks
            // copy-pasting have to think about how to initialize their
            // Random instance.  Initialization of the Random instance is outside
            // the main scope of the question, but some decent options are to have
            // a field that is initialized once and then re-used as needed or to
            // use ThreadLocalRandom (if using at least Java 1.7).
            Random rand = new Random();

            // nextInt is normally exclusive of the top value,
            // so add 1 to make it inclusive
            int randomNum = rand.nextInt((max - min) + 1) + min;

            return randomNum;
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

//                WindowSurface windowSurface = new WindowSurface(mEglCore, mSurfaceTexture);
//                windowSurface.makeCurrent();

//                mediaSurfaceTexture.updateTexImage();

                mediaSurfaceTexture.updateTexImage();
                GlUtil.checkGlError("draw start");
//                int randomNum = ThreadLocalRandom.current().nextInt(1, 3 + 1);
                int rand = randInt(1, 3);
//                if (rand == 1) {
//                    GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
//                } else if (rand == 2) {
//                    GLES20.glClearColor(0.0f, 1.0f, 0.0f, 1.0f);
//                } else {
//                    GLES20.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
//                }

                GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
//                mRect.draw(mTexProgram, mDisplayProjectionMatrix);

                gpuImageFilter.onDraw(genTextureID, mGLCubeBuffer, mGLTextureBuffer);

                mWindowSurface.swapBuffers();

                GlUtil.checkGlError("draw done");



                // Render frames until we're told to stop or the SurfaceTexture is destroyed.
//        doAnimation(windowSurface);

//                windowSurface.swapBuffers();
//                windowSurface.release();




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
            if (mWindowSurface != null) {
                mWindowSurface.release();
                mWindowSurface = null;
            }
        }

        WindowSurface mWindowSurface;

        int genTextureID = OpenGlUtils.NO_TEXTURE;
        final float CUBE[] = {
                -1.0f, -1.0f,
                1.0f, -1.0f,
                -1.0f, 1.0f,
                1.0f, 1.0f,
        };
        private FloatBuffer mGLCubeBuffer;
        private FloatBuffer mGLTextureBuffer;

        GPUImageFilterGroup gpuImageFilter;

        @Override   // will be called on UI thread
        public void onSurfaceTextureAvailable(SurfaceTexture st, int width, int height) {
            Log.d(TAG, "onSurfaceTextureAvailable(" + width + "x" + height + ")");

//            mediaPlayer.setSurface(new Surface(oneTextureView.getSurfaceTexture()));

            isCreated = true;
            synchronized (mLock) {
                mSurfaceTexture = st;
//                mLock.notify();
            }
            mEglCore = new EglCore(null, 0);
            mWindowSurface = new WindowSurface(mEglCore, new Surface(st), false);
            mWindowSurface.makeCurrent();
            genTextureID = getPreviewTexture();
            mediaSurfaceTexture = new SurfaceTexture(genTextureID);
            mediaSurfaceTexture.setOnFrameAvailableListener(MultiFilterPreviewActivity.this);

            mGLCubeBuffer = ByteBuffer.allocateDirect(CUBE.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            mGLCubeBuffer.put(CUBE).position(0);

            mGLTextureBuffer = ByteBuffer.allocateDirect(TEXTURE_NO_ROTATION.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            mGLTextureBuffer.put(TEXTURE_NO_ROTATION).position(0);

            gpuImageFilter = new GPUImageFilterGroup();
            gpuImageFilter.addFilter(new GPUImageExtTexFilter());
            GPUImageFilter filter = GPUImageFilterTools.createFilterForType(MultiFilterPreviewActivity.this, GPUImageFilterTools.FilterType.GRAYSCALE);
            gpuImageFilter.addFilter(filter);

            GLES20.glClearColor(0, 0, 0, 1);
            GLES20.glDisable(GLES20.GL_DEPTH_TEST);

            gpuImageFilter.init();
            GLES20.glUseProgram(gpuImageFilter.getProgram());
            gpuImageFilter.onOutputSizeChanged(480, 360);

            // make current and setmediaPlayersurface

            mediaPlayer.setSurface(new Surface(mediaSurfaceTexture));
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


    // Thread that handles rendering and controls the camera.  Started in onResume(),
    // stopped in onPause().
    private RenderThread mRenderThread;

    // Receives messages from renderer thread.
    private MainHandler mHandler;

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume BEGIN");
        super.onResume();

        mRenderThread = new RenderThread(mHandler, this);
        mRenderThread.setName("TexFromCam Render");
        mRenderThread.start();
        mRenderThread.waitUntilReady();

        RenderHandler rh = mRenderThread.getHandler();
//        rh.sendZoomValue(mZoomBar.getProgress());
//        rh.sendSizeValue(mSizeBar.getProgress());
//        rh.sendRotateValue(mRotateBar.getProgress());

        // FIXME i change here, remember to reset!
//        if (sSurfaceHolder != null) {
//            Log.d(TAG, "Sending previous surface");
//            rh.sendSurfaceAvailable(sSurfaceHolder, false);
//        } else {
//            Log.d(TAG, "No previous surface");
//        }

        Log.d(TAG, "onResume END");
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause BEGIN");
        super.onPause();

        RenderHandler rh = mRenderThread.getHandler();
        rh.sendShutdown();
        try {
            mRenderThread.join();
        } catch (InterruptedException ie) {
            // not expected
            throw new RuntimeException("join was interrupted", ie);
        }
        mRenderThread = null;
        Log.d(TAG, "onPause END");
    }

    /**
     * Custom message handler for main UI thread.
     * <p>
     * Receives messages from the renderer thread with UI-related updates, like the camera
     * parameters (which we show in a text message on screen).
     */
    private static class MainHandler extends Handler {
        private static final int MSG_SEND_CAMERA_PARAMS0 = 0;
        private static final int MSG_SEND_CAMERA_PARAMS1 = 1;
        private static final int MSG_SEND_RECT_SIZE = 2;
        private static final int MSG_SEND_ZOOM_AREA = 3;
        private static final int MSG_SEND_ROTATE_DEG = 4;

        private WeakReference<MultiFilterPreviewActivity> mWeakActivity;

        public MainHandler(MultiFilterPreviewActivity activity) {
            mWeakActivity = new WeakReference<MultiFilterPreviewActivity>(activity);
        }

        /**
         * Sends the updated camera parameters to the main thread.
         * <p>
         * Call from render thread.
         */
        public void sendCameraParams(int width, int height, float fps) {
            // The right way to do this is to bundle them up into an object.  The lazy
            // way is to send two messages.
            sendMessage(obtainMessage(MSG_SEND_CAMERA_PARAMS0, width, height));
            sendMessage(obtainMessage(MSG_SEND_CAMERA_PARAMS1, (int) (fps * 1000), 0));
        }

        /**
         * Sends the updated rect size to the main thread.
         * <p>
         * Call from render thread.
         */
        public void sendRectSize(int width, int height) {
            sendMessage(obtainMessage(MSG_SEND_RECT_SIZE, width, height));
        }

        /**
         * Sends the updated zoom area to the main thread.
         * <p>
         * Call from render thread.
         */
        public void sendZoomArea(int width, int height) {
            sendMessage(obtainMessage(MSG_SEND_ZOOM_AREA, width, height));
        }

        /**
         * Sends the updated zoom area to the main thread.
         * <p>
         * Call from render thread.
         */
        public void sendRotateDeg(int rot) {
            sendMessage(obtainMessage(MSG_SEND_ROTATE_DEG, rot, 0));
        }

        @Override
        public void handleMessage(Message msg) {
            MultiFilterPreviewActivity activity = mWeakActivity.get();
            if (activity == null) {
                Log.d(TAG, "Got message for dead activity");
                return;
            }

            switch (msg.what) {
                default:
                    throw new RuntimeException("Unknown message " + msg.what);
            }
        }
    }

    /**
     * Thread that handles all rendering and camera operations.
     */
    private class RenderThread extends Thread implements
            SurfaceTexture.OnFrameAvailableListener {
        // Object must be created on render thread to get correct Looper, but is used from
        // UI thread, so we need to declare it volatile to ensure the UI thread sees a fully
        // constructed object.
        private volatile RenderHandler mHandler;

        // Used to wait for the thread to start.
        private Object mStartLock = new Object();
        private boolean mReady = false;

        private MainHandler mMainHandler;

//        private Camera mCamera;
        private MediaPlayer mediaPlayer;
        private int mCameraPreviewWidth, mCameraPreviewHeight;

        private EglCore mEglCore;
        private WindowSurface mWindowSurface1;
        private WindowSurface mWindowSurface2;
        private int mWindowSurfaceWidth;
        private int mWindowSurfaceHeight;

        // Receives the output from the camera preview.
        private SurfaceTexture mCameraTexture;

        // Orthographic projection matrix.
//        private float[] mDisplayProjectionMatrix = new float[16];

//        private Texture2dProgram mTexProgram;
//        private final ScaledDrawable2d mRectDrawable =
//                new ScaledDrawable2d(Drawable2d.Prefab.RECTANGLE);
//        private final Sprite2d mRect = new Sprite2d(mRectDrawable);

//        private int mZoomPercent = DEFAULT_ZOOM_PERCENT;
//        private int mSizePercent = DEFAULT_SIZE_PERCENT;
//        private int mRotatePercent = DEFAULT_ROTATE_PERCENT;
//        private float mPosX, mPosY;


        Activity activity;
        /**
         * Constructor.  Pass in the MainHandler, which allows us to send stuff back to the
         * Activity.
         */
        public RenderThread(MainHandler handler, Activity activity) {
            mMainHandler = handler;
            this.activity = activity;
        }

        /**
         * Thread entry point.
         */
        @Override
        public void run() {
            Looper.prepare();

            // We need to create the Handler before reporting ready.
            mHandler = new RenderHandler(this);
            synchronized (mStartLock) {
                mReady = true;
                mStartLock.notify();    // signal waitUntilReady()
            }

            // Prepare EGL and open the camera before we start handling messages.
            mEglCore = new EglCore(null, 0);
//            openCamera(REQ_CAMERA_WIDTH, REQ_CAMERA_HEIGHT, REQ_CAMERA_FPS);
            mediaPlayer = MediaPlayer.create(activity, R.raw.video_480x360_mp4_h264_500kbps_30fps_aac_stereo_128kbps_44100hz);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();

            Looper.loop();

            Log.d(TAG, "looper quit");
//            releaseCamera();
            mediaPlayer.release();
            releaseGl();
            mEglCore.release();

            synchronized (mStartLock) {
                mReady = false;
            }
        }

        /**
         * Waits until the render thread is ready to receive messages.
         * <p>
         * Call from the UI thread.
         */
        public void waitUntilReady() {
            synchronized (mStartLock) {
                while (!mReady) {
                    try {
                        mStartLock.wait();
                    } catch (InterruptedException ie) { /* not expected */ }
                }
            }
        }

        /**
         * Shuts everything down.
         */
        private void shutdown() {
            Log.d(TAG, "shutdown");
            Looper.myLooper().quit();
        }

        /**
         * Returns the render thread's Handler.  This may be called from any thread.
         */
        public RenderHandler getHandler() {
            return mHandler;
        }

        int mTextureId = -1;

//        int genTextureID = OpenGlUtils.NO_TEXTURE;
        final float CUBE[] = {
                -1.0f, -1.0f,
                1.0f, -1.0f,
                -1.0f, 1.0f,
                1.0f, 1.0f,
        };
        private FloatBuffer mGLCubeBuffer;
        private FloatBuffer mGLTextureBuffer;

        GPUImageFilterGroup gpuImageFilter1;
        GPUImageFilterGroup gpuImageFilter2;

        HashSet<SurfaceHolder> holders = new HashSet<SurfaceHolder>();
        HashMap<SurfaceHolder, WindowSurface> windowSurfacesMap = new HashMap<SurfaceHolder, WindowSurface>();
        HashMap<Integer, GPUImageFilterGroup> gpuImageFilters = new HashMap<Integer, GPUImageFilterGroup>();

        SurfaceHolder surfaceHolder1;
        SurfaceHolder surfaceHolder2;
        /**
         * Handles the surface-created callback from SurfaceView.  Prepares GLES and the Surface.
         */
        private void surfaceAvailable(SurfaceHolder holder, boolean newSurface) {

            if (holders.contains(holder)) {
                // added before
                Log.e(TAG, "surfaceAvailable holder contains should never comein");
            }
            holders.add(holder);

            if (holders.size() <= 1) {
                surfaceHolder1 = holder;
                // only create once
                Surface surface = holder.getSurface();
                mWindowSurface1 = new WindowSurface(mEglCore, surface, false);
//                windowSurfaces.add(mWindowSurface1);
                windowSurfacesMap.put(holder, mWindowSurface1);
                mWindowSurface1.makeCurrent();

                mTextureId = getPreviewTexture();
                Log.d(TAG, "mTextureId=" + mTextureId);
                mCameraTexture = new SurfaceTexture(mTextureId);


                mGLCubeBuffer = ByteBuffer.allocateDirect(CUBE.length * 4)
                        .order(ByteOrder.nativeOrder())
                        .asFloatBuffer();
                mGLCubeBuffer.put(CUBE).position(0);

                mGLTextureBuffer = ByteBuffer.allocateDirect(TEXTURE_NO_ROTATION.length * 4)
                        .order(ByteOrder.nativeOrder())
                        .asFloatBuffer();
                mGLTextureBuffer.put(TEXTURE_NO_ROTATION).position(0);

//            mRect.setTexture(textureId);

//            if (!newSurface) {
//                // This Surface was established on a previous run, so no surfaceChanged()
//                // message is forthcoming.  Finish the surface setup now.
//                //
//                // We could also just call this unconditionally, and perhaps do an unnecessary
//                // bit of reallocating if a surface-changed message arrives.
//                mWindowSurfaceWidth = mWindowSurface.getWidth();
//                mWindowSurfaceHeight = mWindowSurface.getHeight();
//                finishSurfaceSetup(holder);
//            }

                mCameraTexture.setOnFrameAvailableListener(this);
            } else {
                surfaceHolder2 = holder;
                // the second come
                Surface surface = holder.getSurface();
                mWindowSurface2 = new WindowSurface(mEglCore, surface, false);
                windowSurfacesMap.put(holder, mWindowSurface2);
                mWindowSurface2.makeCurrent();
            }

        }

        public int getPreviewTexture() {
            int textureId = -1;
            if (textureId == GlUtil.NO_TEXTURE) {
                textureId = GlUtil.createTextureObject(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
            }
            return textureId;
        }

        /**
         * Releases most of the GL resources we currently hold (anything allocated by
         * surfaceAvailable()).
         * <p>
         * Does not release EglCore.
         */
        private void releaseGl() {
            GlUtil.checkGlError("releaseGl start");

//            if (mWindowSurface1 != null) {
//                mWindowSurface1.release();
//                mWindowSurface1 = null;
//            }
//            if (mWindowSurface2 != null) {
//                mWindowSurface2.release();
//                mWindowSurface2 = null;
//            }
//            for (int i = 0; i < windowSurfaces.size(); ++i) {
//                WindowSurface windowSurface = windowSurfaces.get(i);
//                if (windowSurface != null) {
//                    windowSurface.release();
//                }
//            }
//            if (mTexProgram != null) {
//                mTexProgram.release();
//                mTexProgram = null;
//            }
            GlUtil.checkGlError("releaseGl done");

            mEglCore.makeNothingCurrent();
        }

        /**
         * Handles the surfaceChanged message.
         * <p>
         * We always receive surfaceChanged() after surfaceCreated(), but surfaceAvailable()
         * could also be called with a Surface created on a previous run.  So this may not
         * be called.
         */
        private void surfaceChanged(SurfaceHolder surfaceHolder, int width, int height) {
            Log.d(TAG, "RenderThread surfaceChanged " + width + "x" + height + ";surfaceHolder=" + surfaceHolder);

            mWindowSurfaceWidth = width;
            mWindowSurfaceHeight = height;
            if (holders.size() <= 1) {
                // create all filter
                if (gpuImageFilters.size() > 0) {
                    gpuImageFilters.clear();
                }
                for (int i = 0; i < MultiFilterPreviewActivity.arrText.length; ++i) {
                    GPUImageFilterGroup gpuImageFilter = new GPUImageFilterGroup();
                    gpuImageFilter.addFilter(new GPUImageExtTexFilter());
                    GPUImageFilter filter = GPUImageFilterTools.createFilterForType(activity, MultiFilterPreviewActivity.arrImages[i]);
                    gpuImageFilter.addFilter(filter);
                    gpuImageFilter.init();
                    gpuImageFilter.onOutputSizeChanged(width, height);
                    gpuImageFilters.put(i, gpuImageFilter);
                }
                finishSurfaceSetup(surfaceHolder);
            }

//            if (surfaceHolder == surfaceHolder1) {
//                gpuImageFilter1 = new GPUImageFilterGroup();
//                gpuImageFilter1.addFilter(new GPUImageExtTexFilter());
//                GPUImageFilter filter = GPUImageFilterTools.createFilterForType(activity, GPUImageFilterTools.FilterType.GRAYSCALE);
//                gpuImageFilter1.addFilter(filter);
//
////            GLES20.glClearColor(0, 0, 0, 1);
////            GLES20.glDisable(GLES20.GL_DEPTH_TEST);
//
//                gpuImageFilter1.init();
////                GLES20.glUseProgram(gpuImageFilter1.getProgram());
//                gpuImageFilter1.onOutputSizeChanged(width, height);
//            } else {
//                gpuImageFilter2 = new GPUImageFilterGroup();
//                gpuImageFilter2.addFilter(new GPUImageExtTexFilter());
//                GPUImageFilter filter = GPUImageFilterTools.createFilterForType(activity, GPUImageFilterTools.FilterType.GAMMA);
//                gpuImageFilter2.addFilter(filter);
//
////            GLES20.glClearColor(0, 0, 0, 1);
////            GLES20.glDisable(GLES20.GL_DEPTH_TEST);
//
//                gpuImageFilter2.init();
////                GLES20.glUseProgram(gpuImageFilter2.getProgram());
//                gpuImageFilter2.onOutputSizeChanged(width, height);
//            }

        }

        /**
         * Handles the surfaceDestroyed message.
         */
        private void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            // In practice this never appears to be called -- the activity is always paused
            // before the surface is destroyed.  In theory it could be called though.
            Log.d(TAG, "RenderThread surfaceDestroyed");
            releaseGl();
        }

        /**
         * Sets up anything that depends on the window size.
         * <p>
         * Open the camera (to set mCameraAspectRatio) before calling here.
         */
        private void finishSurfaceSetup(SurfaceHolder surfaceHolder) {
            int width = mWindowSurfaceWidth;
            int height = mWindowSurfaceHeight;
            Log.d(TAG, "finishSurfaceSetup size=" + width + "x" + height +
                    " camera=" + mCameraPreviewWidth + "x" + mCameraPreviewHeight);
            // Use full window.
//            GLES20.glViewport(0, 0, width, height);
            // Simple orthographic projection, with (0,0) in lower-left corner.
//            Matrix.orthoM(mDisplayProjectionMatrix, 0, 0, width, 0, height, -1, 1);

            // Ready to go, start the camera.
            Log.d(TAG, "starting camera preview");

            mediaPlayer.setSurface(new Surface(mCameraTexture));

        }

        @Override   // SurfaceTexture.OnFrameAvailableListener; runs on arbitrary thread
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
            mHandler.sendFrameAvailable();
        }

        /**
         * Handles incoming frame of data from the camera.
         */
        private void frameAvailable() {
            mCameraTexture.updateTexImage();
            draw();
        }

        /**
         * Draws the scene and submits the buffer.
         */
        private void draw() {
            Log.d(TAG, "frameAvailable draw texture start");
//            GlUtil.checkGlError("draw start");



            for (Map.Entry<SurfaceHolder, WindowSurface> entry : windowSurfacesMap.entrySet()) {

//                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                WindowSurface windowSurface = entry.getValue();
                windowSurface.makeCurrent();
                int position = holderMap.get(entry.getKey());
                GPUImageFilterGroup filter = gpuImageFilters.get(position);
                filter.onDraw(mTextureId, mGLCubeBuffer, mGLTextureBuffer);
                windowSurface.swapBuffers();
            }
//            for (int i = 0; i < windowSurfaces.size(); ++i) {
//                WindowSurface windowSurface = windowSurfaces.get(i);
//                windowSurface.makeCurrent();
//                gpuImageFilter1.onDraw(mTextureId, mGLCubeBuffer, mGLTextureBuffer);
//                windowSurface.swapBuffers();
//            }
//            mWindowSurface1.makeCurrent();
////            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
////            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
//
//            gpuImageFilter1.onDraw(mTextureId, mGLCubeBuffer, mGLTextureBuffer);
////            mRect.draw(mTexProgram, mDisplayProjectionMatrix);
//            mWindowSurface1.swapBuffers();
//
//            mWindowSurface2.makeCurrent();
////            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
////            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
//
//            gpuImageFilter2.onDraw(mTextureId, mGLCubeBuffer, mGLTextureBuffer);
////            mRect.draw(mTexProgram, mDisplayProjectionMatrix);
//            mWindowSurface2.swapBuffers();

            GlUtil.checkGlError("draw done");
            Log.d(TAG, "frameAvailable draw texture end");
        }

    }


    /**
     * Handler for RenderThread.  Used for messages sent from the UI thread to the render thread.
     * <p>
     * The object is created on the render thread, and the various "send" methods are called
     * from the UI thread.
     */
    private static class RenderHandler extends Handler {
        private static final int MSG_SURFACE_AVAILABLE = 0;
        private static final int MSG_SURFACE_CHANGED = 1;
        private static final int MSG_SURFACE_DESTROYED = 2;
        private static final int MSG_SHUTDOWN = 3;
        private static final int MSG_FRAME_AVAILABLE = 4;
        private static final int MSG_REDRAW = 9;

        // This shouldn't need to be a weak ref, since we'll go away when the Looper quits,
        // but no real harm in it.
        private WeakReference<RenderThread> mWeakRenderThread;

        /**
         * Call from render thread.
         */
        public RenderHandler(RenderThread rt) {
//            super();
            mWeakRenderThread = new WeakReference<RenderThread>(rt);
        }

        /**
         * Sends the "surface available" message.  If the surface was newly created (i.e.
         * this is called from surfaceCreated()), set newSurface to true.  If this is
         * being called during Activity startup for a previously-existing surface, set
         * newSurface to false.
         * <p>
         * The flag tells the caller whether or not it can expect a surfaceChanged() to
         * arrive very soon.
         * <p>
         * Call from UI thread.
         */
        public void sendSurfaceAvailable(SurfaceHolder holder, boolean newSurface) {
            sendMessage(obtainMessage(MSG_SURFACE_AVAILABLE,
                    newSurface ? 1 : 0, 0, holder));
        }

        /**
         * Sends the "surface changed" message, forwarding what we got from the SurfaceHolder.
         * <p>
         * Call from UI thread.
         */
        public void sendSurfaceChanged(SurfaceHolder holder, @SuppressWarnings("unused") int format, int width,
                                       int height) {
            // ignore format
            sendMessage(obtainMessage(MSG_SURFACE_CHANGED, width, height, holder));
        }

        /**
         * Sends the "shutdown" message, which tells the render thread to halt.
         * <p>
         * Call from UI thread.
         */
        public void sendSurfaceDestroyed(SurfaceHolder holder) {
            sendMessage(obtainMessage(MSG_SURFACE_DESTROYED));
        }

        /**
         * Sends the "shutdown" message, which tells the render thread to halt.
         * <p>
         * Call from UI thread.
         */
        public void sendShutdown() {
            sendMessage(obtainMessage(MSG_SHUTDOWN));
        }

        /**
         * Sends the "frame available" message.
         * <p>
         * Call from UI thread.
         */
        public void sendFrameAvailable() {
            sendMessage(obtainMessage(MSG_FRAME_AVAILABLE));
        }

        @Override  // runs on RenderThread
        public void handleMessage(Message msg) {
            int what = msg.what;
            //Log.d(TAG, "RenderHandler [" + this + "]: what=" + what);

            RenderThread renderThread = mWeakRenderThread.get();
            if (renderThread == null) {
                Log.w(TAG, "RenderHandler.handleMessage: weak ref is null");
                return;
            }

            switch (what) {
                case MSG_SURFACE_AVAILABLE:
                    renderThread.surfaceAvailable((SurfaceHolder) msg.obj, msg.arg1 != 0);
                    break;
                case MSG_SURFACE_CHANGED:
                    renderThread.surfaceChanged((SurfaceHolder) msg.obj, msg.arg1, msg.arg2);
                    break;
                case MSG_SURFACE_DESTROYED:
                    renderThread.surfaceDestroyed((SurfaceHolder) msg.obj);
                    break;
                case MSG_SHUTDOWN:
                    renderThread.shutdown();
                    break;
                case MSG_FRAME_AVAILABLE:
                    renderThread.frameAvailable();
                    break;

                case MSG_REDRAW:
                    renderThread.draw();
                    break;
                default:
                    throw new RuntimeException("unknown message " + what);
            }
        }
    }

    public int getPreviewTexture() {
        int textureId = -1;
        if (textureId == GlUtil.NO_TEXTURE) {
            textureId = GlUtil.createTextureObject(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        }
        return textureId;
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        Log.d(TAG, "onFrameAvailable ");
        // surface available
//        if (!mRender.isCreated()) {
//            surfaceTexture.updateTexImage();
//            return;
//        }

        mRender.notifyLock();
//        surfaceTexture.updateTexImage();
        //draw sth





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

    HashMap<SurfaceHolder, Integer> holderMap = new HashMap<SurfaceHolder, Integer>();


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
                holder.surfaceView = (SurfaceView) convertView.findViewById(R.id.texture_in_grid);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }



            GridItem item = mGridData.get(position);
            Log.d(TAG, "getView holder=" + holder + ";position=" + position);
            holder.textView.setText(item.getTitle());
            holder.surfaceView.getHolder().addCallback(MultiFilterPreviewActivity.this);
            holderMap.put(holder.surfaceView.getHolder(), position);
            return convertView;
        }

        private class ViewHolder {
            TextView textView;
            SurfaceView surfaceView;
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
