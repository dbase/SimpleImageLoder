package com.android.picasawebalbum;


import com.android.picasawebalbum.R;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupWindow;

public class PopupShow {
	
	private final String TAG = "PopupShow";

    private static final int    sDefaultTimeout = 5000;    
    private static final int    FADE_OUT = 1;

    private Context             mContext;
    private View                mAnchor;
    private View                mRoot;

    private boolean             mShowing;

    
    private PopupWindow 		mPopWindow;

    
    private DisplayMetrics      mDisplayMetrics;
    private int                 mScreenWidth;
    private int                 mScreenHeight;

    private int                 mControllerWidth  = 0;
    private int                 mControllerHeight = 150;

    
    public PopupShow(Context context) {    	
        //super(context);
        mContext = context;
        
        mDisplayMetrics = mContext.getResources().getDisplayMetrics();
        mScreenWidth    = mDisplayMetrics.widthPixels;
        mScreenHeight   = mDisplayMetrics.heightPixels;
        
        mControllerWidth = mScreenWidth;
    }
    
    
    /**
     * Set the view that acts as the anchor for the control view.
     * This can for example be a VideoView, or your Activity's main view.
     * @param view The view to which to anchor the controller when it is visible.
     */
    public void setAnchorView(View view) {
        mAnchor = view;

        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT
        );

        View v = makePopupView();
    }
    
    /**
     *  Always display constant view 
     */
    public void setConstantView(View view) {
    	mRoot = view;
    	View v = makePopupView();
    }
    
    /**
     * Create the view that holds the widgets that control popup view.
     * Derived classes can override this to create their own.
     * @return The controller view.
     * @hide This doesn't work as advertised
     */
    protected View makePopupView() {
        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRoot = inflate.inflate(R.layout.photoshow_popupwindow, null);
        initPopupView(mRoot);       
        
        mPopWindow = new PopupWindow(mRoot, mControllerWidth, mControllerHeight, true);

        return mRoot;
    }
    
    
    private void initPopupView(View v) {
    	
    }
    
    View.OnKeyListener mKeyListener = new View.OnKeyListener() {
    	
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			Log.i(TAG, "onKeyListener id = " + v.getId() + " code = " + keyCode + "Action = " + event.getAction() );
			
			if( event.getAction() == KeyEvent.ACTION_DOWN ) {

				switch( keyCode ) {
					case KeyEvent.KEYCODE_ENTER:
					case KeyEvent.KEYCODE_DPAD_CENTER:
						return true;
						
					case KeyEvent.KEYCODE_DPAD_LEFT:
						
						return true;
						
					case KeyEvent.KEYCODE_DPAD_RIGHT:
						
						return true;

					case KeyEvent.KEYCODE_DPAD_DOWN:
						return true;
						
					case KeyEvent.KEYCODE_DPAD_UP:
						return true;
						
					case KeyEvent.KEYCODE_MENU:
						return true;
												
				}
				
			
			}			
			
			// TODO Auto-generated method stub
			return true;
		}
    	
    	
    };

    /**
     * Show the controller on screen. It will go away
     * automatically after 3 seconds of inactivity.
     */
    public void show() {
        show(sDefaultTimeout);        
    }
    
    public void show(int keyCode, KeyEvent event) {
    	//mKeyListener.onKey(mAnchor, keyCode, event);
        show(sDefaultTimeout);        
    }

    /**
     * Show the controller on screen. It will go away
     * automatically after 'timeout' milliseconds of inactivity.
     * @param timeout The timeout in milliseconds. Use 0 to show
     * the controller until hide() is called.
     */
    public void show(int timeout) {
    	
    	Log.i(TAG, "show ==>");
    	if( mAnchor == null ) {
        	// for constant View
        	constantShow();
        	
    	} else {
    		
            if (!mShowing && mAnchor != null) {

                int [] anchorpos = new int[2];
                mAnchor.getLocationOnScreen(anchorpos);
                
                mPopWindow.setBackgroundDrawable(new BitmapDrawable());
                //mPopWindow.showAsDropDown(mAnchor);                
                mPopWindow.showAtLocation(mAnchor, Gravity.BOTTOM, 0, 0);
                
                // make the control bar handle keys for seeking and pausing
                mPopWindow.setFocusable(true);
                mPopWindow.setTouchable(true);
                
                mShowing = true;
            }
            
            Message msg = mHandler.obtainMessage(FADE_OUT);
            if (timeout != 0) {
                mHandler.removeMessages(FADE_OUT);
                mHandler.sendMessageDelayed(msg, timeout);
            }
    		
    	}
    	
    }
    
    public boolean isShowing() {
        return mShowing;
    }

    /**
     * Remove the controller from the screen.
     */
    public void hide() {
        if (mAnchor == null) {
        	// for constant View
        	constantHide();
        } else {

            if (mShowing) {
                try {
                    mPopWindow.dismiss();
                } catch (IllegalArgumentException ex) {
                    Log.w(TAG, "PopupView already removed");
                }
                mShowing = false;
            }

        }

    }
    
    /**
     * 
     */
    public void constantShow() {
        mShowing = true;
    }
    
    /**
     * 
     */
    public void constantHide()  {
        
    	try {
    		
    		mShowing = false;
    	} catch (IllegalArgumentException ex) {
    		Log.w(TAG, "PopupView already removed");
        }

    }
    

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case FADE_OUT:
                    hide();
                    break;
            }
        }
    };




}
