package com.dunteam.android.geniusface;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity  implements OnClickListener{
	
	private PreviewFrameLayout frame = null;
	private SurfaceView preview = null;
	private SurfaceHolder previewHolder = null;
	private Camera camera = null;
	private boolean inPreview = false;
	private boolean cameraConfigured = false;
	private Button btnSwitch;
	private int sWidth;
	private int sHeight;
	private int numberOfCamera = Camera.getNumberOfCameras();
	private boolean isCurrentCameraFront;
	private boolean frontAvailable;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		frame = (PreviewFrameLayout) findViewById(R.id.frame);
		preview = (SurfaceView) findViewById(R.id.preview);
		
		previewHolder = preview.getHolder();
		previewHolder.addCallback(surfaceCallback);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		if(getFrontCameraId() != -1)
			frontAvailable = true;
		else
			frontAvailable = false;
	}

	@Override
	public void onResume() {
		super.onResume();

		if(frontAvailable) 
		{
			camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
			isCurrentCameraFront = true;
		}
		else
		{
			camera = Camera.open();
			isCurrentCameraFront = false;
		}
		startPreview();
	}

	@Override
	public void onPause() {
		if (inPreview) {
			camera.stopPreview();
		}

		camera.release();
		camera = null;
		inPreview = false;

		super.onPause();
	}

    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }	

	private void initPreview(int width, int height) {
		if (camera != null && previewHolder.getSurface() != null) {
			try {
				camera.setPreviewDisplay(previewHolder);
			} catch (Throwable t) {
				Log.e("PreviewDemo-surfaceCallback", "Exception in setPreviewDisplay()", t);
				Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
			}

			if (!cameraConfigured) {
				Camera.Parameters parameters = camera.getParameters();
				Camera.Size size = getOptimalPreviewSize(parameters.getSupportedPreviewSizes(), width, height);

				DrawView dv = new DrawView(this, sWidth, sHeight);
				frame.addView(dv);
				
				if(numberOfCamera > 1)
				{
					btnSwitch = new Button(this);
					btnSwitch.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
					btnSwitch.setId(R.id.switch_button_id);
					btnSwitch.setOnClickListener(this);
					btnSwitch.setBackgroundResource(R.drawable.device_access_switch_camera);
					frame.addView(btnSwitch);
				}

				if (size != null) {
					parameters.setPreviewSize(size.width, size.height);
					frame.setAspectRatio((double) size.width / size.height);
					camera.setParameters(parameters);
					cameraConfigured = true;
				}
			}
		}
	}

	private void startPreview() {
		if (cameraConfigured && camera != null) {
			camera.startPreview();
			inPreview = true;
		}
	}

	public void restartPreview(boolean isFront) {
		if (inPreview) {
			camera.stopPreview();
		}

		camera.release();
		
		int camId = Camera.CameraInfo.CAMERA_FACING_FRONT;
		int camId2 = Camera.CameraInfo.CAMERA_FACING_BACK;
		if (isFront) {
			camera = Camera.open(camId2);
			isCurrentCameraFront = false;
		} 
			else 
		{
			camera = Camera.open(camId);
			isCurrentCameraFront = true;
		}

		initPreview(sWidth, sHeight);
		camera.startPreview();
	}

	public void capture(View v) {
		camera.takePicture(null, null, mPicture);
		startPreview();
	}
	
	public void switchCamera() {
		if(numberOfCamera > 0)
			restartPreview(isCurrentCameraFront);
	}
	
	private int getFrontCameraId() {
	    CameraInfo ci = new CameraInfo();
	    for (int i = 0 ; i < Camera.getNumberOfCameras(); i++) {
	        Camera.getCameraInfo(i, ci);
	        if (ci.facing == CameraInfo.CAMERA_FACING_FRONT) 
	        	return i;
	    }
	    return -1; // No front-facing camera found
	}
	
	SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
		public void surfaceCreated(SurfaceHolder holder) {
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			sHeight = height;
			sWidth = width;
			initPreview(width, height);
			startPreview();
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
		}
	};
	
    PictureCallback mPicture = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
        	Intent intent = new Intent(MainActivity.this, ConfirmationActivity.class);
        	intent.putExtra("IMAGE", data);
        	intent.putExtra("ISFRONT", isCurrentCameraFront);
        	startActivityForResult(intent, 1);
        }
    };

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.switch_button_id:
				switchCamera();
				break;
		}
		
	}
	

}