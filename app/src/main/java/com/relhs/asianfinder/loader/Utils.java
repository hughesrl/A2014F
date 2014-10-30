package com.relhs.asianfinder.loader;

import android.app.Activity;
import android.graphics.Bitmap;

import com.relhs.asianfinder.R;
import com.relhs.asianfinder.view.RoundedAvatarDrawable;

import java.io.InputStream;
import java.io.OutputStream;

public class Utils {
    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
    public static RoundedAvatarDrawable resizedBitmap(Bitmap bitmap, int width, int height) {

        Bitmap resizedbitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        RoundedAvatarDrawable r = new RoundedAvatarDrawable(resizedbitmap);

        return r;
    }
}