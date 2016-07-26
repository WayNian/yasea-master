package net.ossrs.yasea;


import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Spinner;

import java.io.IOException;
import java.util.List;

public class CameraActivity extends Activity {
    // UI相关
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Button okButton;
    private Spinner  channelSpinner;

    private int height = 290;// 制式通道的改变
    private int width = 0;
    private int mNumberOfCameras;

    private Camera camera = null;
    private Parameters param = null;
    private boolean previewRunning = false;
    private boolean isOpen = false;

    // 数据保存
    private String[] channelList = {
            "单通道1", "单通道2", "单通道3", "单通道4", "双通道", "四通道",
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 取消标题栏和状态栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);

        //获取系统的分辨率
        Display display = this.getWindowManager().getDefaultDisplay();
        int nHeight = display.getHeight();
        int nWidth = display.getWidth();
        Log.e("高度", String.valueOf(nHeight));
        Log.e("宽度", String.valueOf(nWidth));

        // 初始化界面
        view_init();

        // 初始化surfaceView
        surfaceView();

    }

    private void surfaceView() {
        // TODO Auto-generated method stub
        System.out.println("------surfaceView init------");
        surfaceView = (SurfaceView) findViewById(R.id.surface);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(new MySurfaceViewCallback());
        surfaceHolder.setType(surfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private class MySurfaceViewCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            // TODO Auto-generated method stub
            System.out.println("------surfaceChanged------");
            surfaceHolder = holder;
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            System.out.println("------surfaceCreated------");
            surfaceHolder = holder;

            ok_choice();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            System.out.println("------surfaceDestroyed------");
            surfaceView = null;
            surfaceHolder = null;
        }
    }


    private void view_init() {
        // TODO Auto-generated method stub
        System.out.println("------view_init------");

        channelSpinner = (Spinner) findViewById(R.id.channel_spinner);
        okButton = (Button) findViewById(R.id.ok_button);
        okButton.setOnClickListener(new buttonClick());

        SpinnerAdapter channelAdapter = new SpinnerAdapter(CameraActivity.this,
                android.R.layout.simple_spinner_item, channelList);
        channelSpinner.setAdapter(channelAdapter);

        ok_choice();

    }

    public class buttonClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.ok_button:
                    ok_choice();
                    break;
                default:
                    break;
            }
        }
    }

    private void ok_choice() {
        System.out.println("------ok button down------");

        String channel = channelSpinner.getSelectedItem().toString();


        if (channel.equals("单通道1")) {
            width = 500;
        } else if (channel.equals("单通道2")) {
            width = 501;
        } else if (channel.equals("单通道3")) {
            width = 502;
        } else if (channel.equals("单通道4")) {
            width = 503;
        } else if (channel.equals("双通道")) {
            width = 505;
        } else if (channel.equals("四通道")) {
            width = 504;
        }

        CloseCamera();

        InitCamera();

        mNumberOfCameras = Camera.getNumberOfCameras();
        Log.e("摄像头", String.valueOf(mNumberOfCameras));
    }

    // 初始化camera
    private void InitCamera() {
        System.out.println("------InitCamera------");

        if (!isOpen) {
            camera = Camera.open(); // 取得第一个摄像头
            param = camera.getParameters();// 获取param


            List<Camera.Size> pictureSizes = param.getSupportedPictureSizes();
            int length = pictureSizes.size();
            for (int i = 0; i < length; i++) {
                Log.e("SupportedPictureSizes","SupportedPictureSizes : " + pictureSizes.get(i).width + "x" + pictureSizes.get(i).height);
            }

            List<Camera.Size> previewSizes = param.getSupportedPreviewSizes();
            length = previewSizes.size();
            for (int i = 0; i < length; i++) {
                Log.e("SupportedPreviewSizes","SupportedPreviewSizes : " + previewSizes.get(i).width + "x" + previewSizes.get(i).height);
            }


            param.setPreviewSize(width, height);// 设置预览大小
            param.setPreviewFpsRange(4, 10);// 预览照片时每秒显示多少帧的范围张
            param.setPictureFormat(ImageFormat.JPEG);// 图片形式
            param.set("jpeg-quality", 95);
            param.setPictureSize(1600, 900);
            camera.setParameters(param);
            try {
                camera.setPreviewDisplay(surfaceHolder);// 设置预览显示
            } catch (IOException e) {
            }
            // 进行预览
            if (!previewRunning) {
                camera.startPreview(); // 进行预览
                previewRunning = true; // 已经开始预览
            }

            isOpen = true;
        }
    }

    // 关闭摄像头
    private void CloseCamera() {
        if (camera != null) {
            System.out.println("------CloseCamera------");
            if (previewRunning) {
                camera.stopPreview(); // 停止预览
                previewRunning = false;
            }
            camera.release();
            camera = null;
            isOpen = false;
        }
    }

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
        System.out.println("------onDestroy------");

        CloseCamera();

    }


}
