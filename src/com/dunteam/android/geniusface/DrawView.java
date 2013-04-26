package com.dunteam.android.geniusface;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.SurfaceView;

public class DrawView extends SurfaceView{

	private static final float goldenRatio = 1.61803398875f;
	private int faceOvalHorizontalSize;
	private int faceOvalVerticalSize;
	private int width;
	private int height;
    private Paint dashedLine = new Paint();
    private RectF faceOval = new RectF();
  
	public DrawView(Context context) {
		super(context);
        setWillNotDraw(false);
	}
	
	public DrawView(Context context, int width, int height) {
		super(context);
		this.width = width;
		this.height = height;
        dashedLine.setStyle(Paint.Style.STROKE);
        dashedLine.setPathEffect(new DashPathEffect(new float[] {10,20}, 0));
        dashedLine.setColor(Color.WHITE);
        

        setWillNotDraw(false);
	}
	
	@Override
    protected void onDraw(Canvas canvas){
		faceOvalHorizontalSize = (int)((float)height / goldenRatio + 1);

		faceOvalVerticalSize = (int)(faceOvalHorizontalSize * goldenRatio);

		int top = height / 2 - faceOvalHorizontalSize / 2;
		int bottom = height / 2 + faceOvalHorizontalSize / 2;
		int left = (int)((float)(width - faceOvalVerticalSize) / (goldenRatio + 1));;
		int right = left + faceOvalVerticalSize;
		
		faceOval.set(left, top, right, bottom);
        canvas.drawOval(faceOval, dashedLine);
	}


	public void set(int width, int height) {
		this.width = width;
		this.width = height;
	}
	
}
