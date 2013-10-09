package com.android.picasawebalbum;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

public class PhotoList extends Activity {
  
  private final String TAG = "PhotoList";
  
  private GridView gView;
  private String userId  = "";
  private String albumId = ""; 
  private String title   = "";
  private List<String> li=new ArrayList<String>();  
  private List<String> mBigPhoto=new ArrayList<String>();
  
  
  @Override
  public void onCreate(Bundle savedInstanceState)  {
    
    super.onCreate(savedInstanceState);

    setContentView(R.layout.photolist);

    Intent intent=this.getIntent();
    Bundle bunde = intent.getExtras();
    userId = bunde.getString("userId");
    albumId = bunde.getString("albumId");
    title = bunde.getString("title");
    
    li=this.getPhotoList(userId, albumId);
    
    gView=(GridView) findViewById(R.id.myPhotoGrid);
    gView.setAdapter(new PhotoListAdapter(this,li));
    
    
    gView.setOnItemClickListener(
        new AdapterView.OnItemClickListener() { 
      @Override
      public void onItemClick(AdapterView<?> arg0,View arg1,
                              int arg2,long arg3)  {
        
        Intent intent = new Intent();
        intent.setClass(PhotoList.this,PhotoShow.class);
        Bundle bundle = new Bundle();
        bundle.putString("userId", userId );
        bundle.putString("albumId", albumId );
        bundle.putString("title", title );
        bundle.putString("photUrl", mBigPhoto.get(arg2) );
        intent.putExtras(bundle);
        startActivityForResult(intent,1);
      } 
    }); 
  }
  
  
  /**
   * 
   * @param id
   * @return
   */
  private List<String> getPhotoList(String userId,String albumId) {
    
    List<String> data=new ArrayList<String>();
    URL url = null;
    
    // get photo image size = 1600
    String path="http://picasaweb.google.com/data/feed/api/user/"
                +userId.trim()+"/albumid/"+albumId.trim()+"?imgmax=1600&thumbsize=160c";

    Log.i(TAG, "URL = " + path );
    
    try {
      url = new URL(path);

      PhotoListHandler alHandler = new PhotoListHandler();
      
      alHandler.setAlbumID(albumId.trim());
      
      Xml.parse(url.openConnection().getInputStream(),
                Xml.Encoding.UTF_8,alHandler);
      
      data = alHandler.getSmallPhoto();
      
      mBigPhoto = alHandler.getBigPhoto();
          
    } catch (Exception e) { 
      
      Intent intent=new Intent();
      Bundle bundle = new Bundle();
      bundle.putString("error",""+e);
      intent.putExtras(bundle);
      
      PhotoList.this.setResult(99, intent);
      PhotoList.this.finish();
    }
    
    return data;
  }
  
  
  @Override
  protected void onActivityResult(int requestCode,int resultCode, Intent data)  {
    
    switch (resultCode) { 
      case 99:        
        Bundle bunde = data.getExtras();
        String error = bunde.getString("error");
        showDialog(error);
        break;     
      default: 
        break; 
    } 
  } 
  
  /**
   * 
   * @param mess
   */
  private void showDialog(String mess){
    new AlertDialog.Builder(PhotoList.this).setTitle("Message")
     .setMessage(mess)
     .setNegativeButton("",new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which)  {
          
        }
      })
      .show();
  }

}
