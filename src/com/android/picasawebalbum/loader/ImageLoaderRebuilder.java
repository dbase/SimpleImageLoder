

package com.android.picasawebalbum.loader;

import android.graphics.Bitmap;

public interface ImageLoaderRebuilder {
	public Bitmap getRebuildImage(ImageLoader2 imageLoader, Bitmap image);
}
