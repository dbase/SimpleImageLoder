package com.android.picasawebalbum;


import com.android.picasawebalbum.loader.LoadManager;
import com.android.picasawebalbum.loader.Loader;
import com.android.picasawebalbum.loader.LoaderEvent;
import com.android.picasawebalbum.loader.UpdateImage;

import java.util.List;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;


public class PhotoShowAdapter extends BaseAdapter {
  
  private LayoutInflater mInflater;
  private List<String> items;
  private Context mContext;
  
  
  public PhotoShowAdapter(Context context,List<String> it)  {
    mInflater = LayoutInflater.from(context);
    items = it;
    mContext =  context;
    //mDrawableManager = new DrawableManager( context.getResources().getDrawable(R.drawable.loading_fail));
  }
  
  
  @Override
  public int getCount()   {
    return items.size();
  }

  @Override
  public Object getItem(int position)   {
    return items.get(position);
  }
  
  @Override
  public long getItemId(int position)   {
    return position;
  }
  
  @Override
  public View getView(int position,View conView,ViewGroup par)   {
    ViewHolder holder;
    
    if(conView == null)  {
      
      conView = mInflater.inflate(R.layout.gallery, null);
      
      holder = new ViewHolder();
      holder.image = (ImageView)conView.findViewById(R.id.myImage);
      conView.setTag(holder);
      
    } else {
      holder = (ViewHolder) conView.getTag();
    }
    
    //mDrawableManager.fetchDrawableOnThread(items.get(position).toString(), holder.image);
    // update image
    LoadManager.setupBitmap(mContext, holder.image, items.get(position).toString(), position, imageLoaderEvent, null);

    
    return conView;
  }
  
  private class ViewHolder  {
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