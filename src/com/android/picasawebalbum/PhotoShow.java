package com.android.picasawebalbum;

import com.android.picasawebalbum.*;
import com.android.picasawebalbum.ImageLoader.LoadedCallback;
import com.android.picasawebalbum.data.PhotoItem;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.util.Log;
import android.util.Xml;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ViewSwitcher.ViewFactory;

public class PhotoShow extends Activity implements ViewFactory {
	
  private final String TAG = "PhotoShow";

  private final int    sDefaultSlidesShowTimeout = 3000;
  private final int    SLIDE_SHOW_MESSAGE = 991;   

  private ImageSwitcher mSwitcher;

  private Bitmap previousBitmap = null;
  
  private Gallery mGallery;  
  private List<String> smallPhoto=new ArrayList<String>();
  private List<String> bigPhoto=new ArrayList<String>();
  
  private ArrayList<PhotoItem> mPhotoItemList = new ArrayList<PhotoItem>(); 
  private int mPlayingIndex = -1;
  private DrawableManager mDrawableManager;
  
  private String mUserID   = null;
  private String mAlbumID  = null;
  private String mTitle    = null;
  private String mPhotoURL = null;
  
  private View mProgressView;
  
  private boolean mIsSlidesShow = false;
  
  private ImageLoader mImageLoader  = null;

  
  @Override
  public void onCreate(Bundle savedInstanceState)  {
    super.onCreate(savedInstanceState);
    
    setContentView(R.layout.photoshow);
    
    Intent intent=this.getIntent();
    Bundle bunde = intent.getExtras();
    mUserID   = bunde.getString("userId");
    mAlbumID  = bunde.getString("albumId");
    mTitle    = bunde.getString("title");
    mPhotoURL = bunde.getString("photUrl");    
    
        
    mDrawableManager = new DrawableManager( getResources().getDrawable(R.drawable.loading_fail));
    
    mSwitcher = (ImageSwitcher) findViewById(R.id.switcher);
    mSwitcher.setFactory(this);    
    mSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,
            android.R.anim.fade_in));    
    mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,
            android.R.anim.fade_out));
    
    mProgressView = findViewById(R.id.photo_progress_indicator);   
    
    mImageLoader = new ImageLoader(this, this.getContentResolver() );    
    
    /*
    mGallery = (Gallery) findViewById(R.id.gallery);
    mGallery.setAdapter( new PhotoShowAdapter(this,smallPhoto) );
    mGallery.setOnItemSelectedListener(
    		new Gallery.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        
        mDrawableManager.fetchDrawableOnThreadForImageSwitcher(bigPhoto.get(arg2).toString(), mSwitcher);                
      }

      @Override
      public void onNothingSelected(AdapterView<?> arg0)  {
    	  
      }   
      
    });
    */
    
  } 
  
  public void onStart() {
    Log.i(TAG,"onStart()....");
    
    onStartAction();
    
    super.onStart();    

  }
  
  @Override
  public void onPause() {
    onPauseAction();
    super.onPause();
  }
  
  @Override
  public void onResume() {
    
    super.onResume();
  }

  @Override
  public void onStop() {      
    onStopAction();
    
    finish();
    super.onStop();
  }

  
  public void onDestory(){
    
    onDestoryAction();
    super.onDestroy();
  }

  
  private void onStartAction() {
    
    //mDrawableManager.fetchDrawableOnThreadForImageSwitcher(mPhotoURL, mSwitcher);        
    
    getPhotoList(mUserID, mAlbumID);
    
    normalPlayAction();
    
    
  }
  
  /**
   * 
   */
  private void onPauseAction() {

  }
  
  private void onStopAction() {
    
    mSwitcher = null;
    
    mHandler.removeCallbacksAndMessages(null);

    if( mImageLoader != null ) {
      mImageLoader.clearQueue();
    }
    
    // recycle bitmap memory
    if( previousBitmap != null && !previousBitmap.isRecycled() ) {
      previousBitmap.recycle();
      previousBitmap = null;
      System.gc();
    }

  }  
  
  private void onDestoryAction() {
    
  }

  
  public View makeView()  {
    ImageView i = new ImageView(this);
    //i.setBackgroundColor(0xFFFFFFFF);
    i.setBackgroundColor(0x00000000); // black background
    i.setScaleType(ImageView.ScaleType.FIT_CENTER);
    i.setLayoutParams(new ImageSwitcher.LayoutParams(
        LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
    
    return i;
  }
  
  
  private void getPhotoList(String userId,String albumId)  {
	  
    URL url = null;
    // get photo image size = 1600
    String path="http://picasaweb.google.com/data/feed/api/user/"
                +userId.trim()+"/albumid/"+albumId.trim()+"?imgmax=1600";
    
    try {  
      url = new URL(path);
      
      PhotoShowHandler handler = new PhotoShowHandler(); 
      Xml.parse(url.openConnection().getInputStream(),
                Xml.Encoding.UTF_8,handler);
      
      smallPhoto =handler.getSmallPhoto();
      bigPhoto =handler.getBigPhoto();
      
      
      
    } catch (Exception e) { 
      
      Intent intent=new Intent();
      Bundle bundle = new Bundle();
      bundle.putString("error",""+e);
      intent.putExtras(bundle);      
      PhotoShow.this.setResult(99, intent);
      PhotoShow.this.finish();
    }
    
    
    if(mPhotoItemList == null ) {      
      mPhotoItemList = new ArrayList<PhotoItem>();  
    }
    
    
    mPhotoItemList.clear();
    
    for(int ii = 0; ii< smallPhoto.size(); ii++  ) {
      PhotoItem photoItem    = new PhotoItem();
      photoItem.photoURL     = bigPhoto.get(ii);
      photoItem.thumbnailURL = smallPhoto.get(ii);
      
      mPhotoItemList.add(photoItem);
      
      // update playing index
      if(  photoItem.photoURL.equals( mPhotoURL ) ) {
        mPlayingIndex = ii;        
      }
      
    }
    
    
  }
  
  
  private void normalPlayAction() {
    
    getPhotoList(mUserID, mAlbumID);
    
    onLoadImageAction();
  }
  
  private void onLoadImageAction()  {
    
    if( mImageLoader  ==  null ) {
      //BitmapFactory.Options mOptions = new BitmapFactory.Options(); 
      mImageLoader = new ImageLoader(this, this.getContentResolver() );
    }

    mImageLoader.getPhotoImage(mPhotoItemList.get(mPlayingIndex), mPhotoItemList.get(mPlayingIndex).photoURL, mImageLoadCallback, 0);
          
    mProgressView.setVisibility(View.VISIBLE);

  }
  
  /**
   *  Receive Key event
   */
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK ) {
      finish();
      return true;
    } else {
      
      switch( keyCode ) {
        case KeyEvent.KEYCODE_ENTER:
        case KeyEvent.KEYCODE_DPAD_CENTER:
          onPlayPauseAction();
          return true;
          
        case KeyEvent.KEYCODE_DPAD_LEFT:
          onPreviousAction();
          return true;
          
        case KeyEvent.KEYCODE_DPAD_RIGHT:
          onNextAction();
          return true;

        case KeyEvent.KEYCODE_DPAD_DOWN:
          return true;
          
        case KeyEvent.KEYCODE_DPAD_UP:
          return true;
          
        case KeyEvent.KEYCODE_MENU:
          return true;
      }

      /*
      if( mMediaController != null && !mMediaController.isShowing() ) {
        mMediaController.show(keyCode, event);
        
      }
      */
      
      return true;
    }
    
    //return super.onKeyDown(keyCode, event);
    
  }

  
  /**
   * 
   */
  private Handler mHandler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
        
        Bundle data = null;
        
          switch (msg.what) {
              case SLIDE_SHOW_MESSAGE:
                doSlideShowAction(sDefaultSlidesShowTimeout);
                  break;
          }
      }
  };

  /**
   * 
   */
  private void doSlideShowAction(int timeout) {
    
    if( (mPlayingIndex + 1) == mPhotoItemList.size() ) {        
      mPlayingIndex = -1;
    }     
       
    onNextAction();     
    
  }

  /**
   * 
   */
  private void onNextAction() {
    
    // goto last item
    if( (mPlayingIndex + 1) == mPhotoItemList.size() ) {        
      mPlayingIndex = -1;
    }

    if(mPlayingIndex + 1 < mPhotoItemList.size() ) {
      mPlayingIndex += 1;     
      
      Log.i(TAG, "Next Image[" + mPlayingIndex + "]");
            
      
      onLoadImageAction();
    }

  }
  
  /**
   * 
   */
  private void onPreviousAction() {
    
    // goto first item
    if( (mPlayingIndex - 1) < 0 ) {       
      mPlayingIndex = mPhotoItemList.size();
    }

    if(mPlayingIndex - 1 >= 0  ) {              
      
      mPlayingIndex -= 1;
      
      Log.i(TAG, "Privous Image[" + mPlayingIndex + "]");
      
      onLoadImageAction();
    }

  }
  
  /**
   * 
   */
  private void onPlayPauseAction() {
    
    if( mIsSlidesShow == false ) {
      // start to slide show
          Message msg = mHandler.obtainMessage(SLIDE_SHOW_MESSAGE);        
          mHandler.removeMessages(SLIDE_SHOW_MESSAGE);
          
          
          mHandler.sendMessageDelayed(msg, sDefaultSlidesShowTimeout);
          mIsSlidesShow = true;
          
    } else {
      // stop slide show
          Message msg = mHandler.obtainMessage(SLIDE_SHOW_MESSAGE);        
          mHandler.removeMessages(SLIDE_SHOW_MESSAGE);            
          mIsSlidesShow = false;
    }
    
    //mMediaController.doPauseResume(mIsSlidesShow);
      //show(sDefaultTimeout);

  }

  public boolean onTouchEvent(MotionEvent event) {
    Log.i(TAG, "onTouchEvent....");
    //mMediaController.show();    
      return true;
  }

  @Override
  public boolean onTrackballEvent(MotionEvent ev) {
    Log.i(TAG, "onTrackballEvent....");
    //mMediaController.show();
      return false;
  }


  /**
   *  Previous button
   */
  OnClickListener mPrevListener =  new OnClickListener() {
    @Override
    public void onClick(View v) {
      Log.i(TAG, "Pre Button Event....");
      // TODO Auto-generated method stub
      onPreviousAction();    
    }   
  };
  
  /**
   * Next button
   */
  OnClickListener mNextListener =  new OnClickListener() {
    
    @Override
    public void onClick(View v) {
      Log.i(TAG, "Next Button Event....");
      // TODO Auto-generated method stub
      
      onNextAction();
    }

    
  };
  

  /**
   * 
   */
  OnClickListener mBackListener =  new OnClickListener() {

    @Override
    public void onClick(View v) {
      // TODO Auto-generated method stub
      Log.i(TAG, "Next Button Event....");
      finish();
    }
    
  };

  /**
   * Pause button
   */
  private View.OnClickListener mPauseListener = new View.OnClickListener() {
    
      public void onClick(View v) {
        Log.i(TAG, "Pause Button Event....");

        onPlayPauseAction();
        
      }
  };


  /**
   * 
   */
  private LoadedCallback mImageLoadCallback =  new LoadedCallback() {

    @Override
    public void run(PhotoItem photoItem, String imageUrl, int index,  final Bitmap result) {
      // TODO Auto-generated method stub
      mHandler.post(new Runnable() {
        public void run() {
          
          // This photo had been removed. To finish this photo show
          if( result == null ) {
            finish();
          }
          
          if(mSwitcher == null ) {
            return;
          }
          
          //mProgressDialog.dismiss();
          
          mProgressView.setVisibility(View.INVISIBLE);
          
          Drawable drawable = new BitmapDrawable(result);
          
          mSwitcher.setImageDrawable(drawable);
          
          previousBitmap = result;
          
          if( mIsSlidesShow ==  true ) {
              // next time 
                Message msg = mHandler.obtainMessage(SLIDE_SHOW_MESSAGE);        
                if (sDefaultSlidesShowTimeout != 0) {
                    mHandler.removeMessages(SLIDE_SHOW_MESSAGE);
                    mHandler.sendMessageDelayed(msg, sDefaultSlidesShowTimeout);
                }
          }
          

        }
      });
      
    }
    
  };
  
  

 

}