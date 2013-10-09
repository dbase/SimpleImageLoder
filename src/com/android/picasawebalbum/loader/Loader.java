

package com.android.picasawebalbum.loader;

public class Loader extends Thread {
	public LoadManager loadManager = null;
	public boolean started = false;
	public boolean isCallbackOnUIThread = false;
	public Object userDataObject = null;
	public String userDataString = "";
	public int userDataInt1 = 0;
	public int userDataInt2 = 0;

	public void cancel() {}
	public LoaderEvent loaderEvent = null;
}
