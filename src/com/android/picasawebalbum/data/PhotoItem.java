package com.android.picasawebalbum.data;

import android.util.Log;

public class PhotoItem {
  
  private final String TAG = "PhotoItem";
  
  public String photoID = "";  // <gphoto:id>photoID</gphoto:id>
  public String albumID = "";  // <gphoto:albumid>albumID</gphoto:albumid>
  public String position = "";  
  
  public String description = ""; // <media:description type='plain'></media:description>
  
  public String photoURL = "";    // <media:content url='https://photoPath/invisible_bike.jpg'  height='295' width='410' type='image/jpeg' medium='image' />
  public String photoWidth = "";  // <gphoto:width>410</gphoto:width>
  public String photoHeight = ""; //<gphoto:height>295</gphoto:height>
  public String photoSize = "";   // <gphoto:size>23044</gphoto:size>

  /**
   * <media:thumbnail url='https://thumbnailPath/s72/invisible_bike.jpg' height='52' width='72' />
   * 
   */
  public String thumbnailURL = "";
  public String thumbnailWidth = "";
  public String thumbnailHeight = "";
  
  
  
  /**
   * 
   */
  public Object clone() {
    Object o = null;
    try{
            o = super.clone();
    }catch(CloneNotSupportedException e){

            Log.e(TAG,e.getMessage(),e);
    }

    return o;
  }
  
  public void dumpContent() {
    
    if( photoID != null )         Log.i(TAG,"photo id    = " + photoID );
    if( albumID != null )         Log.i(TAG,"album id    = " + albumID );
    if( position != null )        Log.i(TAG,"position    = " + position );
    if( description != null )     Log.i(TAG,"description = " + description );
    if( photoURL != null )        Log.i(TAG,"Url         = " + photoURL );
    if( photoWidth != null )      Log.i(TAG,"width       = " + photoWidth );
    if( photoHeight != null )     Log.i(TAG,"height      = " + photoHeight );
    if( photoSize != null )       Log.i(TAG,"size        = " + photoSize );
    if( thumbnailURL != null )    Log.i(TAG,"thumbnail Url    = " + thumbnailURL );
    if( thumbnailWidth != null )  Log.i(TAG,"thumbnail width  = " + thumbnailWidth );
    if( thumbnailHeight != null ) Log.i(TAG,"thumbnail height = " + thumbnailHeight );
    
  }


}
