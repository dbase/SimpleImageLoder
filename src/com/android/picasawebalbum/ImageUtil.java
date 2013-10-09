package com.android.picasawebalbum;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;


import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.URLUtil;

public class ImageUtil {

	private static int reflectionGap = 4;
	private final static String TAG = "ImageUtil";
	private final static int MIN_SIDE_LENGTH = 1920;
	private final static int MAX_NUM_OF_PIXELS = 3 *1024 * 1024;
	
	//private final static int MIN_SIDE_LENGTH   = 720;
	//private final static int MAX_NUM_OF_PIXELS = 1280 * 720;
	
	private final static String PACKAGE_NAME = "com.android.picasawebalbum";
	
	// 
	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidht = ((float) w / width);
		float scaleHeight = ((float) h / height);
		matrix.postScale(scaleWidht, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		
		if(bitmap != null && !bitmap.isRecycled() )  {
			bitmap.recycle();
			bitmap = null;
			System.gc();			
		}
		
		return newbmp;
	}

	// 
	public static Bitmap drawableToBitmap(Drawable drawable) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
				.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;

	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	// 
	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
		final int reflectionGap = 4;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
				width, height / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 2), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint deafalutPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);

		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
				0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);
		
		if( reflectionImage != null && !reflectionImage.isRecycled() ) {
			reflectionImage.recycle();
			reflectionImage = null;
			System.gc();
		}

		return bitmapWithReflection;
	}

		
    public static Bitmap getRefelection(Bitmap image, float skew, int dw) {

    	final int refScale = 5;
    	
    	//Get you bit map from drawable folder
	    Bitmap originalImage = image ;
	    
	    int width = originalImage.getWidth();
	    int height = originalImage.getHeight();
	    
	    
	    //This will not scale but will flip on the Y axis
	    Matrix matrix = new Matrix();
	    //matrix.preScale(1, -1);
	    
	    if (skew!=0) matrix.setSkew(skew, 0f);
	    matrix.preScale(1, -1);
	    
	    //Create a Bitmap with the flip matrix applied to it.
	    //We only want the bottom half of the image
	    Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0
	    		   , height*(refScale-1)/refScale, width, height/refScale, matrix, false);
	      
	    int width2 = reflectionImage.getWidth();
	    int height2 = reflectionImage.getHeight();
	    
	    //Create a new bitmap with same width but taller to fit reflection	    
	    Bitmap bitmapWithReflection = Bitmap.createBitmap(width2+dw
	    		   , (height + height/refScale + reflectionGap), Config.ARGB_8888);
	    
	    //Create a new Canvas with the bitmap that's big enough for
	    //the image plus gap plus reflection
	    Canvas canvas = new Canvas(bitmapWithReflection);
	    //Draw in the original image
	    canvas.drawBitmap(originalImage, width2-width+dw, 0, null);
	      
	    //Draw in the gap ---> do not draw to keep the gap transparent
	    /*
	    Paint deafaultPaint = new Paint();
	    canvas.drawRect(0, height, width, height + reflectionGap, deafaultPaint);
	    */
	      
	    //Draw in the reflection
	    canvas.drawBitmap(reflectionImage, dw, height + reflectionGap, null);
	    
	    //Create a shader that is a linear gradient that covers the reflection
	    Paint paint = new Paint();
	    LinearGradient shader = new LinearGradient(0, originalImage.getHeight()+reflectionGap, 0,
	        bitmapWithReflection.getHeight() , 0x70ffffff, 0x00ffffff,
	        TileMode.CLAMP);
	    //Set the paint to use this shader (linear gradient)
	    paint.setShader(shader);
	    
	    //Set the Transfer mode to be porter duff and destination in
	    paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
	    
	    //Draw a rectangle using the paint with our linear gradient
	    canvas.drawRect(0, height+reflectionGap, width2+dw,
	        bitmapWithReflection.getHeight() , paint);
	    
	    if( reflectionImage != null && !reflectionImage.isRecycled() ) {
	    	reflectionImage.recycle();
	    	reflectionImage = null;
	    	
	    	System.gc();
	    }
	    
	    return bitmapWithReflection;
    }

    /**
     * 
     * @param url
     * @return
     * @throws IOException 
     */
	public static Bitmap LoadImageFromMediaProvider( ContentResolver cr, Uri uri ) throws IOException {		

		Bitmap bm =  null;
		
        if(uri == null || uri.equals("") ) {        	
        	return bm;
        }
        
        BitmapFactory.Options options = null;
        
        options = new BitmapFactory.Options();
        options.inSampleSize = 1;   
		options.inPurgeable = true;
		options.inInputShareable = true;

        bm = getBitmapFromMediaProvider(cr, uri, options );        
        
        return bm;
	}

    
    
    /**
     * 
     * @param url
     * @return
     * @throws IOException 
     */
	public static Bitmap LoadImageFromRemoteServer( String url ) throws IOException {		

		Bitmap bm =  null;
		
        if(url == null || url.equals("") ) {        	
        	return bm;
        }
        
        BitmapFactory.Options options = null;
        
        options = new BitmapFactory.Options();
        options.inSampleSize = 1;   
		options.inPurgeable = true;
		options.inInputShareable = true;

        bm = getBitmapFromRemoteServer(url, options );        
        
        return bm;
	}

	/**
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static Bitmap LoadImageFromFile( String url ) throws IOException {		

		Bitmap bm =  null;
		
        if(url == null || url.equals("") ) {        	
        	return bm;
        }
                
        BitmapFactory.Options options = null;
        
        options = new BitmapFactory.Options();
        options.inSampleSize = 1;   
		options.inPurgeable = true;
		options.inInputShareable = true;
		
		File f = new File(url);
		
		if(f.exists() ) {
			bm = getBitmapFromFile(f, options );
		} 
                
        
        return bm;
	}

	/**
	 * 
	 * @param url
	 * @param options
	 * @return
	 */
	public static Bitmap getBitmapFromMediaProvider(ContentResolver cr, Uri uri ,BitmapFactory.Options options) {
		
		Bitmap bm =  null;
		
        InputStream is = null;
        //HttpURLConnection cn = null;
		try {
            is = cr.openInputStream(uri);            
            options.inJustDecodeBounds = true;
	        bm = BitmapFactory.decodeStream(is, null, options );
	        is.close();
	        	        
            options.inSampleSize = computeSampleSize(
                    options, MIN_SIDE_LENGTH, MAX_NUM_OF_PIXELS);
            options.inJustDecodeBounds = false;

            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
							
			if (bm != null &&  bm.isRecycled() ==false)
			{
				bm.recycle();
				bm = null;
				System.gc();
			}

            is = cr.openInputStream(uri);
	        bm = BitmapFactory.decodeStream(is, null, options);			
	        			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
			
		} catch (OutOfMemoryError ex) {
            Log.e(TAG, "Got oom exception ", ex);
            return null;
            
		} finally {
			
			if( is != null ) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
															
			System.gc();
				
		}
		
		return bm;
	}


	/**
	 * 
	 * @param src
	 * @param options
	 * @return
	 */
	public static Bitmap getBitmapFromFile(File src ,BitmapFactory.Options options) {
		
	    try {	    	
	        InputStream input = new FileInputStream(src);
	        int scale = 1;
	        
	        Bitmap myBitmap =null;
	        try {
		        options.inJustDecodeBounds = true;
		        
				Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);
				input.close();
				
	            options.inSampleSize = computeSampleSize(
	                    options, MIN_SIDE_LENGTH, MAX_NUM_OF_PIXELS);
	            options.inJustDecodeBounds = false;

	            options.inDither = false;
	            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
	            	            
				if (bitmap != null &&  bitmap.isRecycled() ==false)
				{
					bitmap.recycle();
					bitmap = null;
					System.gc();
				}
				
				input = new FileInputStream(src);
				myBitmap = BitmapFactory.decodeStream(input, null, options);
				//myBitmap = Bitmap.createScaledBitmap( BitmapFactory.decodeFile( src.getPath(), options ), 1600, 1067, false);
						        
	        } catch (OutOfMemoryError ex) {
	            Log.e(TAG, "Got oom exception ", ex);
	            return null;
	            
	        } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
				
			} finally {
                if (input != null) {
                	input.close();
                }
                
        		System.gc();
	        }
			
	        return myBitmap;
	        	
	    } catch (IOException e) {
	        e.printStackTrace();
	        return null;
	    }
	}
	
	
	public static Bitmap getBitmapFromRemoteServer(String url ,BitmapFactory.Options options) {
		
		Bitmap bm =  null;
		
        InputStream is = null;
        HttpURLConnection cn = null;
		try {
			URL mUrl = new URL(url);        
            cn = (HttpURLConnection) mUrl.openConnection();
            cn.setDoInput(true);
            cn.connect();
            is = cn.getInputStream();
            
            options.inJustDecodeBounds = true;
	        bm = BitmapFactory.decodeStream(is, null, options );
	        cn.disconnect();
	        is.close();
	        	        
            options.inSampleSize = computeSampleSize(
                    options, MIN_SIDE_LENGTH, MAX_NUM_OF_PIXELS);
            options.inJustDecodeBounds = false;

            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
							
			if (bm != null &&  bm.isRecycled() ==false)
			{
				bm.recycle();
				bm = null;
				System.gc();
			}
			
			if( options.inSampleSize > 1 ) {
				
				// Download the url stream image to a temporary file
				String src = preCacheData(url);	
				if( src != null ) {
					InputStream input = new FileInputStream(src);
					// decode temporary file to bitmap 
					bm = BitmapFactory.decodeStream(input, null, options);
					
					// remove temporary file
					File srcFile = new File(src);
					srcFile.delete();
				}
				
				
				
			} else {

	            cn = (HttpURLConnection) mUrl.openConnection();
	            cn.setDoInput(true);
	            cn.connect();
	            is = cn.getInputStream();
		        bm = BitmapFactory.decodeStream(is, null, options);

			}
			
	        			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
			
		} catch (OutOfMemoryError ex) {
            Log.e(TAG, "Got oom exception ", ex);
            return null;
            
		} finally {
			
			if( is != null ) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if( cn!= null ) {
				cn.disconnect();	
			}
												
			System.gc();
				
		}
		
		return bm;
	}
	
	
    /*
     * Compute the sample size as a function of minSideLength
     * and maxNumOfPixels.
     * minSideLength is used to specify that minimal width or height of a
     * bitmap.
     * maxNumOfPixels is used to specify the maximal size in pixels that is
     * tolerable in terms of memory usage.
     *
     * The function returns a sample size based on the constraints.
     * Both size and minSideLength can be passed in as IImage.UNCONSTRAINED,
     * which indicates no care of the corresponding constraint.
     * The functions prefers returning a sample size that
     * generates a smaller bitmap, unless minSideLength = IImage.UNCONSTRAINED.
     *
     * Also, the function rounds up the sample size to a power of 2 or multiple
     * of 8 because BitmapFactory only honors sample size this way.
     * For example, BitmapFactory downsamples an image by 2 even though the
     * request is 3. So we round up the sample size to avoid OOM.
     */
    public static int computeSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    /**
     * 
     * @param options
     * @param minSideLength
     * @param maxNumOfPixels
     * @return
     */
    private static int computeInitialSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        } else {
        	return upperBound;
        }

    }

    private static byte[] readStream(InputStream inStream) throws Exception {
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        while ((len = inStream.read(buffer)) != -1) {
                 outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return data;
   } 
    
    private static Bitmap getPicFromBytes(byte[] bytes, BitmapFactory.Options opts) {
        if (bytes != null)
            if (opts != null)
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,opts);
            else
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return null;
    } 


    /**
     * @brief
     * If the user has specified a local url, then we download the
     * url stream to a temporary location and then call the preCacheData
     * for that local file
     *
     * @param path
     * @throws IOException
     */
    public static String preCacheData(String path) throws IOException {

        String tempPath;

        FileOutputStream out;

            if (!URLUtil.isNetworkUrl(path)) {
                return path;
            } else {
            	Log.i(TAG, "preCache Path = " + path );
            	
                URL url = new URL(path);
                URLConnection cn = url.openConnection();
                cn.connect();
                InputStream stream = cn.getInputStream();
                if (stream == null)
                    throw new RuntimeException("stream is null");
                
                /*
                File mDirectory = Environment.getExternalStorageDirectory();

                // create temporary file
                if( mDirectory.exists() && mDirectory.canWrite()) {                	
                	File temp = File.createTempFile("mediaplayertmp", "."+getFileExtension(path), mDirectory);      
                	tempPath = temp.getAbsolutePath();
                    out = new FileOutputStream(tempPath);
                    
                } else {                	
                	mDirectory =  new File("/data/data/"+DMCConst.PACKAGE_NAME+"/files/");
            		if( !mDirectory.exists() ) {
            			mDirectory.mkdir();
            		}
                	                	
                	File temp = File.createTempFile("mediaplayertmp", "."+getFileExtension(path), mDirectory);
                	tempPath = temp.getAbsolutePath();
                    out = new FileOutputStream(tempPath);
                }
                */

            	File mDirectory =  new File("/data/data/"+PACKAGE_NAME+"/files/");
        		if( !mDirectory.exists() ) {
        			mDirectory.mkdir();
        		}
            	                	
            	File temp = File.createTempFile("mediaplayertmp", "."+getFileExtension(path), mDirectory);
            	tempPath = temp.getAbsolutePath();
                out = new FileOutputStream(tempPath);

                
                Log.d(TAG, "Cache data file location on " + tempPath);

                byte buf[] = new byte[1024];
                do {
                    int numread = stream.read(buf);
                    if (numread <= 0)
                        break;
                    out.write(buf, 0, numread);
                } while (true);

                out.flush();
                out.close();

                try {
                    stream.close();
                }
                catch (IOException ex) {
                    Log.e(TAG, "error: " + ex.getMessage(), ex);
                }

                //return temporary location
                return tempPath;
            }

        }

        /**
         * @brief save Image Meta data to Media Provider
         *     
         */
        public static String getFileExtension(String strFileName)      {
                   File myFile = new File(strFileName);
                   String strFileExtension=myFile.getName();
                   strFileExtension=(strFileExtension.substring
                   (strFileExtension.lastIndexOf(".")+1)).toLowerCase();

                   if(strFileExtension=="")
                   {
                     strFileExtension = "dat";
                   }
                   return strFileExtension;
        }
        
}


