package com.android.picasawebalbum;


import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import com.android.picasawebalbum.loader.ImageLoader2;
import com.android.picasawebalbum.loader.ImageLoaderRebuilder;
import com.android.picasawebalbum.loader.LoadManager;
import com.android.picasawebalbum.loader.Loader;
import com.android.picasawebalbum.loader.LoaderEvent;
import com.android.picasawebalbum.loader.UpdateImage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class AlbumAdapter extends BaseAdapter {
  
  private LayoutInflater mInflater;
  private List<String[]> items;

  //private DrawableManager mDrawableManager;
  private Context mContext = null;
  
  public AlbumAdapter(Context context,List<String[]> it) {
    mContext =  context;
    mInflater = LayoutInflater.from(context);
    items = it;
    
    //mDrawableManager = new DrawableManager( context.getResources().getDrawable(R.drawable.loading_fail));
  }
  

  @Override
  public int getCount() {
    return items.size();
  }

  @Override
  public Object getItem(int position) {
    return items.get(position);
  }
  
  @Override
  public long getItemId(int position) {
    return position;
  }
  
  @Override
  public View getView(int position,View conView,ViewGroup par) {
    ViewHolder holder;
    
    if(conView == null)   {
      
      conView = mInflater.inflate(R.layout.album, null);
      
      holder = new ViewHolder();
      holder.text = (TextView) conView.findViewById(R.id.myText);
      holder.image = (ImageView)conView.findViewById(R.id.myImage);
      conView.setTag(holder);
      
    } else {
      holder = (ViewHolder) conView.getTag();
    }
    
    
    String[] tmpS=(String[])items.get(position);
    holder.text.setText(tmpS[2]);   

    //mDrawableManager.fetchDrawableOnThread(tmpS[1], holder.image);
    
    // update image
    LoadManager.setupBitmap(mContext, holder.image, tmpS[1], position, imageLoaderEvent, null);
    
    return conView;
  }
  
  /**
   * 
   *
   */
  private class ViewHolder {
    TextView text;
    ImageView image;
  }
  
  private class ImageLoaderEvent implements LoaderEvent {
    @Override
    public void onComplete(Loader loader, boolean success, Object result) {
        UpdateImage updateImage = (UpdateImage) loader.userDataObject;
        updateImage.bmpIcon = (Bitmap) result;
        
        updateImage.imageView.setImageBitmap( updateImage.bmpIcon) ;
    }
  }
  ImageLoaderEvent imageLoaderEvent = new ImageLoaderEvent();
  
}