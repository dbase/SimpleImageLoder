
package com.android.picasawebalbum.loader;

import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageLoader2 extends Loader {
	public String url = "";
	int what = 0;

	public static final int SAMPLE_SCREEN = 0;
	public static final int SAMPLE_SCREEN_THUMB = 1;
	public static final int SAMPLE_200 = 2;
	
	public int sampleType = SAMPLE_SCREEN;
	public boolean downSampled = false;

	public ImageLoaderRebuilder imageLoaderRebuilder = null;
	
	public ImageLoader2(String url) {
		this.url = url;
	}


	private int near2(int v) {
		int ret = v;
		for (int t=1; t<=8; t++) {
			int x = v & 0x80;
			if (x>0) {
				return 1 <<(8-t);
			}
			v = v << 1;
		}
		return ret;
	}

	private BitmapFactory.Options getOptions(String url) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(url, options);
		
		downSampled = false;
		
		int inSampleSize = 1;
		if (sampleType==SAMPLE_SCREEN) {
			if (options.outWidth>1920*4 || options.outHeight>1080*4) {
				inSampleSize = 4;
			} else if (options.outWidth>1920*2 || options.outHeight>1080*2) {
				inSampleSize = 2;
			}
		} else if (sampleType==SAMPLE_SCREEN_THUMB) {
			if (options.outWidth>1920*4 || options.outHeight>1080*4) {
				inSampleSize = 32;
				downSampled = true;
			} else if (options.outWidth>1920*2 || options.outHeight>1080*2) {
				inSampleSize = 16;
				downSampled = true;
			} else if (options.outWidth>1920 || options.outHeight>1080) {
				inSampleSize = 8;
				downSampled = true;
			}
		} else if (sampleType==SAMPLE_200) {
			double sw = Math.ceil((float)options.outWidth / 200);
			double sh = Math.ceil((float)options.outHeight / 200);
			double s = (sw>sh)?sh:sw;
			inSampleSize = ((int)Math.floor(s));
			//if (inSampleSize>=200) inSampleSize = 256;
			inSampleSize = near2(inSampleSize);
			downSampled = true;
		}

		
		BitmapFactory.Options options2 = new BitmapFactory.Options();
		if (inSampleSize!=1) {
			options2.inSampleSize = inSampleSize;
			//options.inScaled = true;
			//options.inDensity = 240; //DisplayMetrics.DENSITY_DEFAULT;
			//options.inTargetDensity = 120;
		}
		return options2;
	}
	
	private Bitmap decodeFile(String url) {

		BitmapFactory.Options options = getOptions(url);
		
		Bitmap bmp = null;
		try {
			bmp = BitmapFactory.decodeFile(url, options);
		} catch (Exception ex) {ex.printStackTrace();}
		return bmp;
	}
	
	private void fireEvent(boolean success, Bitmap image) {
		Bitmap imageRet = image;
		if (imageLoaderRebuilder!=null) {
			imageRet = imageLoaderRebuilder.getRebuildImage(this, image);
			if (imageRet!=null && imageRet!=image) {
				if (image!=null) image.recycle();
			}
		}
		loadManager.fireEvent(this, success, imageRet);
	}
	
	@Override
	public void run() {
		boolean isLocal = this.url.length()>0 && this.url.substring(0, 1).equals("/");
		if (isLocal) {
			Bitmap bmp = null;
			bmp = decodeFile(this.url);
			fireEvent(true, bmp);
		} else {
			try {
				URL u = new URL(this.url);
				Bitmap bmp = null;
				try {
					bmp = BitmapFactory.decodeStream(u.openConnection().getInputStream());
				} catch (Exception ex) {ex.printStackTrace();}
				fireEvent(true, bmp);
			} catch (Exception e) {
				e.printStackTrace();
				fireEvent(false, null);
			}
		}
	}

	@Override
	public void cancel() {
		this.interrupt();
		try {
			this.join(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
}
