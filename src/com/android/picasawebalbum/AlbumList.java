package com.android.picasawebalbum;


import java.net.URL;
import java.util.List;
import java.util.ArrayList;
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

public class AlbumList extends Activity {
  private GridView gView;
  private String userId="";
  private List<String[]> li=new ArrayList<String[]>();  
  
  private String TAG = "AlbumList";

  
  @Override
  public void onCreate(Bundle savedInstanceState)  {
	  
    super.onCreate(savedInstanceState);

    setContentView(R.layout.albumlist);
    gView=(GridView) findViewById(R.id.myGrid);

 
    Intent intent=this.getIntent();
    Bundle bunde = intent.getExtras();
    userId = bunde.getString("userId");
    
    li=this.getAlbumList(userId);
    gView.setAdapter(new AlbumAdapter(this,li));
          
    
    gView.setOnItemClickListener(
        new AdapterView.OnItemClickListener() { 
      @Override
      public void onItemClick(AdapterView<?> arg0,View arg1,
                              int arg2,long arg3)  {
    	  
        Intent intent = new Intent();
        intent.setClass(AlbumList.this,PhotoList.class);
        Bundle bundle = new Bundle();
        bundle.putString("userId",userId);
        bundle.putString("albumId",li.get(arg2)[0]);
        bundle.putString("title",li.get(arg2)[2]);
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
  private List<String[]> getAlbumList(String id) {
	  
    List<String[]> data=new ArrayList<String[]>();
    URL url = null;
    String path="http://picasaweb.google.com/data/feed/api/user/"
                +id.trim();
    try {
      url = new URL(path);

      AlbumHandler alHandler = new AlbumHandler(); 
      Xml.parse(url.openConnection().getInputStream(),
                Xml.Encoding.UTF_8,alHandler);
      
      data =alHandler.getParsedData();
      
    } catch (Exception e) { 
      
      Intent intent=new Intent();
      Bundle bundle = new Bundle();
      bundle.putString("error",""+e);
      intent.putExtras(bundle);
      
      AlbumList.this.setResult(99, intent);
      AlbumList.this.finish();
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
    new AlertDialog.Builder(AlbumList.this).setTitle("Message")
     .setMessage(mess)
     .setNegativeButton("",new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which)  {
        	
        }
      })
      .show();
  }
  
}