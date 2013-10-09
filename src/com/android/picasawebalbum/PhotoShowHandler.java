package com.android.picasawebalbum; 


import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes; 
import org.xml.sax.SAXException; 
import org.xml.sax.helpers.DefaultHandler; 

import com.android.picasawebalbum.data.PhotoItem;

import android.util.Log;

public class PhotoShowHandler extends DefaultHandler {
	
  private final String TAG = "PhotoHandler";
  private int thumbnailNum=0;
  private List<String> list1;
  private List<String> list2;
  private ArrayList<PhotoItem> photoList;

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
    //photoList = new ArrayList<PhotoItem>(); 
  } 

  @Override 
  public void endDocument() throws SAXException {
	  
  }


  @Override 
  public void startElement(String namespaceURI, String localName, 
               String qName, Attributes atts) throws SAXException  {
	  
    if ( localName.equals("thumbnail"))    {
    	
      if(thumbnailNum==0) {       
        list1.add(atts.getValue("url"));
      }
      
      thumbnailNum++;
      
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
  public void endElement(String namespaceURI, String localName,
                         String qName) throws SAXException   { 
    if (localName.equals("group"))   { 
      
      thumbnailNum=0;
    }
  } 
  
}