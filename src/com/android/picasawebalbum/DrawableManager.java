package com.android.picasawebalbum;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageSwitcher;
import android.widget.ImageView;

public class DrawableManager {
	private final Map<String, Drawable> drawableMap;
	private Drawable m_defDrawable;

    public DrawableManager(Drawable defaultDrawable) {
        drawableMap = new HashMap<String, Drawable>();
        m_defDrawable = defaultDrawable;
    }

    public Drawable fetchDrawable(String urlString) {
        if (drawableMap.containsKey(urlString)) {
            return drawableMap.get(urlString);
        }

        Log.d(this.getClass().getSimpleName(), "image url:" + urlString);
        try {
            InputStream is = fetch(urlString);
            Drawable drawable = Drawable.createFromStream(is, "src");

            if (drawable != null) {
                drawableMap.put(urlString, drawable);
                Log.d(this.getClass().getSimpleName(), "got a thumbnail drawable: " + drawable.getBounds() + ", "
                        + drawable.getIntrinsicHeight() + "," + drawable.getIntrinsicWidth() + ", "
                        + drawable.getMinimumHeight() + "," + drawable.getMinimumWidth());
                return drawable;
            } 

            Log.w(this.getClass().getSimpleName(), "could not get thumbnail");
            drawableMap.put(urlString, m_defDrawable);
            
            return m_defDrawable;
        } catch (Exception e) {
        	drawableMap.put(urlString, m_defDrawable);
            return m_defDrawable;//return null;
        }
        /*catch (MalformedURLException e) {
            Log.e(this.getClass().getSimpleName(), "fetchDrawable failed", e);
            drawableMap.put(urlString, m_defDrawable);
            return m_defDrawable;//return null;
        } catch (IOException e) {
            Log.e(this.getClass().getSimpleName(), "fetchDrawable failed", e);
            drawableMap.put(urlString, m_defDrawable);
            return m_defDrawable;//return null;
        }*/
    }

    public void fetchDrawableOnThread(final String urlString, final ImageView imageView) {
        if (drawableMap.containsKey(urlString)) {
            imageView.setImageDrawable(drawableMap.get(urlString));
            return;
        }

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                imageView.setImageDrawable((Drawable) message.obj);
            }
        };

        Thread thread = new Thread() {
            @Override
            public void run() {
                //set imageView to a "pending" image
                Drawable drawable = fetchDrawable(urlString);
                Message message = handler.obtainMessage(1, drawable);
                handler.sendMessage(message);
            }
        };
        thread.start();
    }
    
    public void fetchDrawableOnThreadForImageSwitcher(final String urlString, final ImageSwitcher imageSwitcher) {
      if (drawableMap.containsKey(urlString)) {
        imageSwitcher.setImageDrawable(drawableMap.get(urlString));
          return;
      }

      final Handler handler = new Handler() {
          @Override
          public void handleMessage(Message message) {
            imageSwitcher.setImageDrawable((Drawable) message.obj);
          }
      };

      Thread thread = new Thread() {
          @Override
          public void run() {
              //set imageView to a "pending" image
              Drawable drawable = fetchDrawable(urlString);
              Message message = handler.obtainMessage(1, drawable);
              handler.sendMessage(message);
          }
      };
      thread.start();
  }


    private InputStream fetch(String urlString) throws MalformedURLException, IOException {
    	HttpParams httpParameters = new BasicHttpParams();
    	// Set the timeout in milliseconds until a connection is established.
    	// The default value is zero, that means the timeout is not used. 
    	int timeoutConnection = 3000;
    	HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
    	// Set the default socket timeout (SO_TIMEOUT) 
    	// in milliseconds which is the timeout for waiting for data.
    	int timeoutSocket = 5000;
    	HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
    	DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);

    	HttpGet request = new HttpGet(urlString);
        HttpResponse response = httpClient.execute(request);
        return response.getEntity().getContent();
    }
}