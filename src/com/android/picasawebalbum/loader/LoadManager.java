

package com.android.picasawebalbum.loader;

import java.util.ArrayList;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

public class LoadManager {

	public int maxThread = 3;
	
	//private Context context = null;
	private int workIndex = 0;
	private ArrayList<Work> works = null;
	
	private boolean stopped = false;
	
	class Event {
		Loader loader = null;
		boolean success = false;
		Object result = null;
	}
	
	private Vector<Event> events = null; 
	
	private class Work {
		public int index = 0;
		public ArrayList<Loader> loaders = null;
	}

  public static void setupBitmap(Context context, ImageView imageView, String url, int position, LoaderEvent event, ImageLoaderRebuilder rebuilder) {
    UpdateImage args = new UpdateImage();
    args.imageView = imageView;
    args.url = url;
    args.index = position;
    ImageLoader2 imageLoader = new ImageLoader2(url);
    imageLoader.userDataObject = args;
    imageLoader.imageLoaderRebuilder = rebuilder;
    imageLoader.loaderEvent = event;
    imageLoader.isCallbackOnUIThread = true;
    LoadManager.getLoadManager().add(imageLoader);
  }

  
  private static LoadManager loadManager = null;

  public static LoadManager getLoadManager() {
      if (loadManager == null)
          loadManager = new LoadManager();
      return loadManager;
  }

  public static void stopLoadManager() {
      if (loadManager != null) {
          loadManager.stop();
          loadManager = null;
      }
  }

	public LoadManager() {
		//this.context = context;
		works = new ArrayList<Work>();
		events = new Vector<Event>();
		stopped = false;
		workHandler.sendEmptyMessage(0);
	}
	
	public int getWorkIndex() {
		workIndex = (workIndex + 1) % Integer.MAX_VALUE;
		return workIndex;
	}
	
	@SuppressLint("HandlerLeak")
    private Handler workHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (stopped) return;
			
			removeCompleted();
			
			int size = events.size();
			for (int t=0; t<size; t++) {
				Event event = events.get(t);
				event.loader.loaderEvent.onComplete(event.loader, event.success, event.result);
			}
			for (int t=size-1; t>=0; t--) {
				events.remove(t);
			}
			
			int runningCount = getRunningCount();
			if (runningCount<maxThread) {
				int works_size = works.size();
				boolean breakAll = false;
				for (int t=0; t<works_size; t++) {
					Work work = works.get(t);
					ArrayList<Loader> work_loaders = work.loaders;
					for (int t2=0; t2<work_loaders.size(); t2++) {
						if (runningCount>=maxThread) {
							breakAll = true;
							break;
						}
						Loader loader = work_loaders.get(t2);
						if (!loader.started && !loader.isAlive()) {
							loader.started = true;
							loader.loadManager = LoadManager.this;
							runningCount++;
							loader.start();
						}
					}
					if (breakAll) break;
				}
			}
			workHandler.sendEmptyMessageDelayed(0, 100);
		}
	};

	
	public int add(Loader loader) {
		ArrayList<Loader> loaders = new ArrayList<Loader>();
		loaders.add(loader);
		return add(loaders);
	}
	public int add(ArrayList<Loader> loaders) {
		int workIndex = getWorkIndex();
		return add(loaders, workIndex);
	}

	
	public int addFirst(Loader loader) {
		int workIndex = getWorkIndex();
		return addFirst(loader, workIndex);
	}
	public int addFirst(Loader loader, int workIndex) {
		ArrayList<Loader> loaders = new ArrayList<Loader>();
		loaders.add(loader);
		Work work = new Work();
		work.index = workIndex;
		work.loaders = loaders;
		works.add(0, work);
		return workIndex;
	}

	
	public int add(Loader loader, int workIndex) {
		ArrayList<Loader> loaders = new ArrayList<Loader>();
		loaders.add(loader);
		return add(loaders, workIndex);
	}
	public int add(ArrayList<Loader> loaders, int workIndex) {
		boolean found = false;
		for (int t=works.size()-1; t>=0; t--) {
			Work work = works.get(t);
			if (work.index==workIndex) {
				if (work.loaders==null) {
					work.loaders = loaders;
				} else {
					int loaders_size = loaders.size();
					for (int t2=0; t2<loaders_size; t2++) {
						work.loaders.add(loaders.get(t2));
					}
				}
				found = true;
				break;
			}
		}
		if (!found) {
			Work work = new Work();
			work.index = workIndex;
			work.loaders = loaders;
			works.add(work);
		}
		return workIndex;
	}
	
	private void removeCompleted() {
		for (int t=works.size()-1; t>=0; t--) {
			Work work = works.get(t);
			ArrayList<Loader> loaders = work.loaders;
			if (loaders!=null) {
				int loaders_size = loaders.size();
				for (int t2=loaders_size-1; t2>=0; t2--) {
					Loader loader = loaders.get(t2);
					if (loader.started && !loader.isAlive()) {
						loaders.remove(t2);
					}
				}
			}
			if (loaders==null || loaders.size()==0) works.remove(t);
		}
	}
	
	private int getRunningCount() {
		int n = 0;
		for (int t=works.size()-1; t>=0; t--) {
			Work work = works.get(t);
			ArrayList<Loader> loaders = work.loaders;
			if (loaders!=null) {
				int loaders_size = loaders.size();
				for (int t2=loaders_size-1; t2>=0; t2--) {
					Loader loader = loaders.get(t2);
					if (loader.isAlive()) n++;
				}
			}
		}
		return n;
	}
	
	public void remove(int workIndex) {
		for (int t=works.size()-1; t>=0; t--) {
			Work work = works.get(t);
			if (work.index==workIndex) {
				ArrayList<Loader> loaders = work.loaders;
				if (loaders!=null) {
					int loaders_size = loaders.size();
					for (int t2=loaders_size-1; t2>=0; t2--) {
						Loader loader = loaders.get(t2);
						if (loader.isAlive()) {
							loader.cancel();
							//loader.interrupt();
							try {
								loader.join(500);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						loaders.remove(t2);
					}
				}
				works.remove(t);
				break;
			}
		}
	}
	
	public void removeAll() {
		for (int t=works.size()-1; t>=0; t--) {
			Work work = works.get(t);
			ArrayList<Loader> loaders = work.loaders;
			if (loaders!=null) {
				int loaders_size = loaders.size();
				for (int t2=loaders_size-1; t2>=0; t2--) {
					Loader loader = loaders.get(t2);
					if (loader.isAlive()) {
						loader.cancel();
						//loader.interrupt();
						try {
							loader.join(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					loaders.remove(t2);
				}
			}
			works.remove(t);
		}
		works.clear();
	}
	
	public void fireEvent(Loader loader, boolean success, Object result) {
		if (loader!=null && loader.loaderEvent!=null) {
			if (loader.isCallbackOnUIThread) {
				Event event = new Event();
				event.loader = loader;
				event.success = success;
				event.result = result;
				events.add(event);
			} else {
				loader.loaderEvent.onComplete(loader, success, result);
			}
		}
	}
	
	public void stop() {
		stopped = true;
	}
	
}

