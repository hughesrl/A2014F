package com.relhs.asianfinder.loader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Window;
import android.view.WindowManager;

import com.relhs.asianfinder.R;
import com.relhs.asianfinder.view.RoundedAvatarDrawable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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


    public static ProgressDialog createProgressDialog(Context mContext) {
        ProgressDialog dialog = new ProgressDialog(mContext);
        try {
            dialog.show();
        } catch (WindowManager.BadTokenException e) {

        }
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.progressdialog);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();

        windowParams.dimAmount = 0.90f;
        windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(windowParams);
        // dialog.setMessage(Message);
        return dialog;
    }

    public static ArrayList<String> getStickersFromAssets(Context context) throws IOException {
        AssetManager assetManager = context.getAssets();
        ArrayList<String> paths = new ArrayList<String>();
        String[] files = assetManager.list("stickers");
        List<String> it = Arrays.asList(files);
        for (short i = 0; i < it.size(); i++) {
            paths.add(it.get(i));
        }
        return paths;
    }
    private static Bitmap getBitmapFromAsset(Context context, String strName) {
        AssetManager assetManager = context.getAssets();
        InputStream istr = null;
        try {
            istr = assetManager.open(strName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(istr);
        return bitmap;
    }

}