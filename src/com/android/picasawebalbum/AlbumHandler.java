package com.android.picasawebalbum; 


import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes; 
import org.xml.sax.SAXException; 
import org.xml.sax.helpers.DefaultHandler; 

public class AlbumHandler extends DefaultHandler {
  private String gphotoURI="";
  private String mediaURI="";
  private boolean in_entry = false;
  private boolean in_title = false;
  private boolean in_id = false;
  private List<String[]> li;
  private String[] s;
  private StringBuffer buf=new StringBuffer();

  
  public List<String[]> getParsedData()  { 
    return li; 
  }

  
  @Override 
  public void startDocument() throws SAXException { 
    li = new ArrayList<String[]>(); 
  }
  
  
  @Override 
  public void endDocument() throws SAXException  {
    
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
    
    if (localName.equals("entry")) { 
      this.in_entry = true;      
      s=new String[3];
      
    } else if (localName.equals("title")) { 
      if(this.in_entry)  {
        this.in_title = true;
      }
      
    } else if (localName.equals("id")) { 
      
      if(gphotoURI.equals(namespaceURI)) {
        this.in_id = true;  
      }
      
    }  else if (localName.equals("thumbnail")) {
      
      if(mediaURI.equals(namespaceURI)) {        
        s[1]=atts.getValue("url");
      }
    }
    
  }
  
  
  @Override 
  public void endElement(String namespaceURI, String localName,
                         String qName) throws SAXException { 
    
    if (localName.equals("entry")) { 
      this.in_entry = false;      
      li.add(s);
      
    } else if (localName.equals("title")) {
      
      if(this.in_entry) {
        
        s[2]=buf.toString().trim();
        buf.setLength(0);
        this.in_title = false;
      }
      
    } else if (localName.equals("id")) {
      
      if(gphotoURI.equals(namespaceURI)) {
        
        s[0]=buf.toString().trim();
        buf.setLength(0);
        this.in_id = false;
      }
    }
    
  }
  
  
  @Override 
  public void characters(char ch[], int start, int length)  {
    
    if(this.in_title||this.in_id) {    
      buf.append(ch,start,length);
    }
  } 
  
}