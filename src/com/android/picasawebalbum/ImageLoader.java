package com.android.picasawebalbum;

import java.io.IOException;
import java.util.ArrayList;

import com.android.picasawebalbum.data.PhotoItem;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;


/**
  * A dedicated decoding thread used by ImageGallery.
  */
public class ImageLoader {
      @SuppressWarnings("unused")
      private static final String TAG = "ImageLoader";

      // Queue of work to do in the worker thread. The work is done in order.
      private final ArrayList<WorkItem> mQueue = new ArrayList<WorkItem>();

      // the worker thread and a done flag so we know when to exit
      private boolean mDone;
      private Thread mDecodeThread;
      private ContentResolver mCr;
      private PhotoItem mImageListItem;
      private Context mContext;

      public interface LoadedCallback {
          public void run(PhotoItem imageItem, String imageUrl, int index, Bitmap result);
      }

      public void getPhotoImage(PhotoItem imageItem,
                  String imageUrl,
                            LoadedCallback imageLoadedRunnable,                          
                            int tag ) {
        
          if (mDecodeThread == null) {
              start();
          }
          synchronized (mQueue) {         
              WorkItem w = new WorkItem(imageItem, imageUrl, imageLoadedRunnable, tag);
              mQueue.add(w);
              mQueue.notifyAll();
          }
      }

      public boolean cancel(final String imageUrl) {
          synchronized (mQueue) {
              int index = findItem(imageUrl);
              if (index >= 0) {
                  mQueue.remove(index);
                  return true;
              } else {
                  return false;
              }
          }
      }

      // The caller should hold mQueue lock.
      private int findItem(String image) {
          for (int i = 0; i < mQueue.size(); i++) {
              if (mQueue.get(i).mImageUrl.equals(image) ) {
                  return i;
              }
          }
          return -1;
      }

      // Clear the queue. Returns an array of tags that were in the queue.
      public int[] clearQueue() {
          synchronized (mQueue) {
              int n = mQueue.size();
              int[] tags = new int[n];
              for (int i = 0; i < n; i++) {
                  tags[i] = mQueue.get(i).mTag;
              }
              mQueue.clear();
              return tags;
          }
      }

      private static class WorkItem {
        PhotoItem     mImageItem;
        String         mImageUrl;
          LoadedCallback mOnLoadedRunnable;
          int mTag;

          WorkItem(PhotoItem imageItem, String imageUrl, LoadedCallback onLoadedRunnable, int tag) {
            mImageUrl         = imageUrl;
            mImageItem        = imageItem;
              mOnLoadedRunnable = onLoadedRunnable;
              mTag              = tag;
          }
      }

      public ImageLoader(ContentResolver cr, Handler handler) {
        mCr = cr;
          start();
      }
      
      public ImageLoader(Context context, ContentResolver cr) {
        mContext = context;
        mCr = cr;        
          start();
      }

      private class WorkerThread implements Runnable {

          // Pick off items on the queue, one by one, and compute their bitmap.
          // Place the resulting bitmap in the cache, then call back by executing
          // the given runnable so things can get updated appropriately.
          public void run() {
              while (true) {
                  WorkItem workItem = null;
                  synchronized (mQueue) {
                      if (mDone) {
                          break;
                      }
                      if (!mQueue.isEmpty()) {
                          workItem = mQueue.remove(0);
                      } else {
                          try {
                              mQueue.wait();
                          } catch (InterruptedException ex) {
                              // ignore the exception
                          }
                          continue;
                      }
                  }
                      
                  int ret = 0;
                  Bitmap b = null;
                  if( workItem.mImageUrl != null &&  !workItem.mImageUrl.equals("") ) {
                    // decode image from remote server
                    try {
                      b =  ImageUtil.LoadImageFromRemoteServer(workItem.mImageUrl);
                    } catch (IOException e) {
                      // TODO Auto-generated catch block
                      e.printStackTrace();
                    }
                                      
                  }
                
                  if (workItem.mOnLoadedRunnable != null) {
                      workItem.mOnLoadedRunnable.run(workItem.mImageItem, workItem.mImageUrl, workItem.mTag , b);
                  }
                  
              }
          }
      }
      
      private void start() {
          if (mDecodeThread != null) {
              return;
          }

          mDone = false;
          Thread t = new Thread(new WorkerThread());
          t.setName("image-loader");
          mDecodeThread = t;
          t.start();
      }

      public void stop() {
          synchronized (mQueue) {
              mDone = true;
              mQueue.notifyAll();
          }
          if (mDecodeThread != null) {
              try {
                  Thread t = mDecodeThread;                                
                  t.join();
                  mDecodeThread = null;
              } catch (InterruptedException ex) {
                  // so now what?
              }
          }
      }


}
