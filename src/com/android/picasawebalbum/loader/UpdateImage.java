package com.android.picasawebalbum.loader;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

public class UpdateImage implements Cloneable{
	
	public int index = -1;
	public String url = "";
	public ImageView imageView;
	public Bitmap bmpIcon = null;
		
	public void dumpContent() {
		Log.i("ImageReturn", "index = " + index );
		Log.i("ImageReturn", "url = " + url );
	}
	
	@Override
	public Object clone() {
		Object o = null;
        try{
        	o = super.clone();
        }catch(CloneNotSupportedException e){

                Log.e("ImageReturn",e.getMessage(),e);
        }

        return o;
	}

}