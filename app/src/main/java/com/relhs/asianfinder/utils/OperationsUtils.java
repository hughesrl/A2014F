package com.relhs.asianfinder.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class OperationsUtils {
	
	public static byte[] getBytes(Bitmap bitmap) {
	    ByteArrayOutputStream stream = new ByteArrayOutputStream();
	    bitmap.compress(CompressFormat.JPEG, 100, stream);
	   return stream.toByteArray();
	}
	
	
	public static Bitmap convertBlobToBitmap(byte[] blobByteArray) {       
	    Bitmap tempBitmap=null;   
	    if(blobByteArray!=null)
	    tempBitmap = BitmapFactory.decodeByteArray(blobByteArray, 0, blobByteArray.length);
	    return tempBitmap;
	}
	public class PatchInputStream extends FilterInputStream {
		  public PatchInputStream(InputStream in) {
		    super(in);
		  }
		  public long skip(long n) throws IOException {
		    long m = 0L;
		    while (m < n) {
		      long _m = in.skip(n-m);
		      if (_m == 0L) break;
		      m += _m;
		    }
		    return m;
		  }
		}
	
	public static String friendsLookup(String productID, Context c, String photoUrl) throws IOException {

	    URL url = new URL(photoUrl);

	    InputStream input = null;
	    FileOutputStream output = null;

	    try {
	        String outputName = productID + "-thumbnail.jpg";

	        input = url.openConnection().getInputStream();
	        output = c.openFileOutput(outputName, Context.MODE_PRIVATE);

	        int read;
	        byte[] data = new byte[1024];
	        while ((read = input.read(data)) != -1)
	            output.write(data, 0, read);

	        return outputName;

	    } finally {
	        if (output != null)
	            output.close();
	        if (input != null)
	            input.close();
	    }
	}
}
