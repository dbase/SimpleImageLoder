

package com.android.picasawebalbum.loader;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

public class ImageViewLoader extends ImageLoader2 implements LoaderEvent {

	Handler handler = null;
	public ImageView imageView = null;
	public Bitmap image = null;
	
	ImageViewLoader(String url) {
		super(url);
	}

	public ImageViewLoader(ImageView imageView, String url, Handler handler) {
		this(url);
		this.handler = handler;
		this.loaderEvent = this;
		this.imageView = imageView;
	}

	@Override
	public void onComplete(Loader loader, boolean success, Object result) {
		Bitmap bm = (Bitmap) result;
		this.image = bm;
		if (handler!=null) {
			Message m = new Message();
			m.obj = this;
			handler.sendMessage(m);
		}
		
	}
	
	
	
}
