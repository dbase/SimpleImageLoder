package com.android.picasawebalbum;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class PicasaWebAlbum extends Activity {
  private Button mButton;
  private EditText mEditText1;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    setContentView(R.layout.picasa_album);
    
    
    mEditText1=(EditText) findViewById(R.id.myEdit1);
    mButton=(Button) findViewById(R.id.myButton);
    
    
    mButton.setOnClickListener(new Button.OnClickListener() { 
      @Override 
      public void onClick(View v) { 
        
        String userId=mEditText1.getText().toString();
        
        if(userId.equals("")) {
          showDialog("!");
          
        } else {        
          
          Intent intent = new Intent();
          intent.setClass(PicasaWebAlbum.this,AlbumList.class);
          Bundle bundle = new Bundle();
          bundle.putString("userId",userId);
          intent.putExtras(bundle);
          startActivityForResult(intent,0);
                    
                
        }
      } 
    });
  }

  
  @Override
  protected void onActivityResult(int requestCode,int resultCode, Intent data) {
    
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
    new AlertDialog.Builder(PicasaWebAlbum.this).setTitle("Message")
     .setMessage(mess)
     .setNegativeButton("",new DialogInterface.OnClickListener()  {
       
        public void onClick(DialogInterface dialog, int which) {
          
        }
        
      })
      .show();
    }
  
}