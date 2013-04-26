package com.dunteam.android.geniusface;

import android.graphics.Bitmap;
import android.util.Log;

public class Utils {
	private float xscale;
	private float yscale;
	private float xshift;
	private float yshift;
	private int[] s;
	private int[] scalar;
	private int[] s1;
	private int[] s2;
	private int[] s3;
	private int[] s4;
	private String TAG = "Filters";

	public Utils() {

		Log.e(TAG, "***********inside filter constructor");
		s = new int[4];
		scalar = new int[4];
		s1 = new int[4];
		s2 = new int[4];
		s3 = new int[4];
		s4 = new int[4];
	}

	public Bitmap barrel(Bitmap input, float k, float cenx, float ceny) {
		// Log.e(TAG, "***********INSIDE BARREL METHOD ");

		// float centerX=input.getWidth()/2; //center of distortion
		// float centerY=input.getHeight()/2;
		float centerX = cenx;
		float centerY = ceny;

		int width = input.getWidth(); // image bounds
		int height = input.getHeight();

		Bitmap dst = Bitmap.createBitmap(width, height, input.getConfig()); // output
																			// pic

		xshift = calc_shift(0, centerX - 1, centerX, k);

		float newcenterX = width - centerX;
		float xshift_2 = calc_shift(0, newcenterX - 1, newcenterX, k);

		yshift = calc_shift(0, centerY - 1, centerY, k);

		float newcenterY = height - centerY;
		float yshift_2 = calc_shift(0, newcenterY - 1, newcenterY, k);

		xscale = (width - xshift - xshift_2) / width;

		yscale = (height - yshift - yshift_2) / height;

		int origPixel = 0;
		int[] arr = new int[input.getWidth() * input.getHeight()];
		int color = 0;

		int p = 0;
		int i = 0;
		long startLoop = System.currentTimeMillis();

		for (int j = 0; j < dst.getHeight(); j++) {
			for (i = 0; i < dst.getWidth(); i++, p++) {
				origPixel = input.getPixel(i, j);

				float x = getRadialX((float) j, (float) i, centerX, centerY, k);

				float y = getRadialY((float) j, (float) i, centerX, centerY, k);

				sampleImage(input, x, y);

				color = ((s[1] & 0x0ff) << 16) | ((s[2] & 0x0ff) << 8) | (s[3] & 0x0ff);

				if (Math.pow(i - centerX, 2) + (Math.pow(j - centerY, 2)) <= 3600) {
					arr[p] = color;
				} else {
					arr[p] = origPixel;
				}
			}
		}
		long endLoop = System.currentTimeMillis();
		long dur = endLoop - startLoop;
		Log.e(TAG, "loop took " + dur + "ms");

		Bitmap dst2 = Bitmap.createBitmap(arr, width, height, input.getConfig());
		return dst2;

	}// end of barrel()

	void sampleImage(Bitmap arr, float idx0, float idx1) {
		if (idx0 < 0 || idx1 < 0 || idx0 > (arr.getHeight() - 1) || idx1 > (arr.getWidth() - 1)) {
			s[0] = 0;
			s[1] = 0;
			s[2] = 0;
			s[3] = 0;
			return;
		}

		float idx0_fl = (float) Math.floor(idx0);
		float idx0_cl = (float) Math.ceil(idx0);
		float idx1_fl = (float) Math.floor(idx1);
		float idx1_cl = (float) Math.ceil(idx1);

		s1 = getARGB(arr, (int) idx0_fl, (int) idx1_fl);
		s2 = getARGB(arr, (int) idx0_fl, (int) idx1_cl);
		s3 = getARGB(arr, (int) idx0_cl, (int) idx1_cl);
		s4 = getARGB(arr, (int) idx0_cl, (int) idx1_fl);

		float x = idx0 - idx0_fl;
		float y = idx1 - idx1_fl;

		s[0] = (int) (s1[0] * (1 - x) * (1 - y) + s2[0] * (1 - x) * y + s3[0] * x * y + s4[0] * x * (1 - y));
		s[1] = (int) (s1[1] * (1 - x) * (1 - y) + s2[1] * (1 - x) * y + s3[1] * x * y + s4[1] * x * (1 - y));
		s[2] = (int) (s1[2] * (1 - x) * (1 - y) + s2[2] * (1 - x) * y + s3[2] * x * y + s4[2] * x * (1 - y));
		s[3] = (int) (s1[3] * (1 - x) * (1 - y) + s2[3] * (1 - x) * y + s3[3] * x * y + s4[3] * x * (1 - y));

	}

	int[] getARGB(Bitmap buf, int x, int y) {

		int rgb = buf.getPixel(y, x); // Returns by default ARGB.
		// int [] scalar = new int[4];
		scalar[0] = (rgb >>> 24) & 0xFF;
		scalar[1] = (rgb >>> 16) & 0xFF;
		scalar[2] = (rgb >>> 8) & 0xFF;
		scalar[3] = (rgb >>> 0) & 0xFF;
		return scalar;
	}

	float getRadialX(float x, float y, float cx, float cy, float k) {

		x = (x * xscale + xshift);
		y = (y * yscale + yshift);
		float res = x
				+ ((x - cx) * k * ((x - cx) * (x - cx) + (y - cy) * (y - cy)));
		return res;
	}

	float getRadialY(float x, float y, float cx, float cy, float k) {

		x = (x * xscale + xshift);
		y = (y * yscale + yshift);
		float res = y
				+ ((y - cy) * k * ((x - cx) * (x - cx) + (y - cy) * (y - cy)));
		return res;
	}

	float thresh = 1;

	float calc_shift(float x1, float x2, float cx, float k) {

		float x3 = (float) (x1 + (x2 - x1) * 0.5);
		float res1 = x1 + ((x1 - cx) * k * ((x1 - cx) * (x1 - cx)));
		float res3 = x3 + ((x3 - cx) * k * ((x3 - cx) * (x3 - cx)));

		if (res1 > -thresh && res1 < thresh)
			return x1;
		if (res3 < 0) {
			return calc_shift(x3, x2, cx, k);
		} else {
			return calc_shift(x1, x3, cx, k);
		}
	}

}
