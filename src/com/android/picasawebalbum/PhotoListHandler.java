package com.android.picasawebalbum;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class PhotoListHandler extends DefaultHandler {
    
    private final String TAG = "PhotoListHandler";
    private int thumbnailNum=0;
    private String mAlbumID = "";
    private List<String> list1;
    private List<String> list2;
    private List<String> photoIDList;
    
    private String gphotoURI="";
    private String mediaURI="";
    
    private boolean in_photoID = false;
    
    private StringBuffer buf=new StringBuffer();

    public void setAlbumID(String albumID ) {
      mAlbumID = albumID;
    }
    
    public List<String> getSmallPhoto() { 
      return list1;
    }

    public List<String> getBigPhoto() {
      return list2;
    }
    
    @Override 
    public void startDocument() throws SAXException  {    
      list1 = new ArrayList<String>();      
      list2 = new ArrayList<String>();
      photoIDList = new ArrayList<String>();
    } 

    @Override 
    public void endDocument() throws SAXException {
      
    }

    @Override 
    public void startPrefixMapping(String prefix,String uri) {
        
      if(prefix.equals("gphoto")) {
        gphotoURI=uri;
        
      } else if(prefix.equals("media")) {
        mediaURI=uri;
      }
    }


    @Override 
    public void startElement(String namespaceURI, String localName, 
                 String qName, Attributes atts) throws SAXException  {
      
      if ( localName.equals("thumbnail"))    {
        
        if(mediaURI.equals(namespaceURI)) {

          if(thumbnailNum==0) {       
            list1.add( atts.getValue("url"));
          }
          
          thumbnailNum++;      

        }
        
        
      } else if( localName.equals("id"))    {
        
        if(gphotoURI.equals(namespaceURI)) {          
          this.in_photoID = true;
        }
        
      } else if( localName.equals("content") )    {
        
        if( atts.getValue("type").equals("image/jpeg") ) {
          
          String srcString = atts.getValue("src");
           if( srcString != null ) {                                    
                 list2.add(srcString);          
           }
        }
        
      }
      
    }

    @Override 
    public void characters(char ch[], int start, int length)  {
      
      if( this.in_photoID ) {    
        buf.append(ch,start,length);
        Log.i(TAG, "gphoto:id = " + buf );
      }
    } 

    @Override 
    public void endElement(String namespaceURI, String localName,
                           String qName) throws SAXException   {
      
      if (localName.equals("group"))   { 
        
        thumbnailNum=0;
      }
      
      if( gphotoURI.equals(namespaceURI) && localName.equals("id") && this.in_photoID == true ) {
      //if( this.in_photoID == true ) {
        
        if( !mAlbumID.equals( buf.toString().trim() )) {          
          photoIDList.add(buf.toString().trim());
        }
        
        buf.setLength(0);
        this.in_photoID = false;        
      }
        
    } 
    


}
