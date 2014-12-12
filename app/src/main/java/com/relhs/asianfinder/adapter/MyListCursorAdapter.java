package com.relhs.asianfinder.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.relhs.asianfinder.Constants;
import com.relhs.asianfinder.DataBaseWrapper;
import com.relhs.asianfinder.R;
import com.relhs.asianfinder.data.MyListInfo;
import com.relhs.asianfinder.data.ThreadsInfo;
import com.relhs.asianfinder.loader.ImageLoader;
import com.relhs.asianfinder.operation.MessagesOperations;
import com.relhs.asianfinder.operation.MyListOperations;

public class MyListCursorAdapter extends CursorAdapter {

    private MyListOperations myListOperations;
    private LayoutInflater mInflater;
    private ImageLoader imageLoader;

    public MyListCursorAdapter(Context context, Cursor c, MyListOperations mO, int flags) {
        super(context, c, flags);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        imageLoader = new ImageLoader(context);

        myListOperations = mO;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {


        TextView userName = (TextView) view.findViewById(R.id.userName);
        TextView briefMsg = (TextView) view.findViewById(R.id.briefMsg);
        TextView lastChatDate = (TextView) view.findViewById(R.id.lastChatDate);
        ImageView photo = (ImageView) view.findViewById(R.id.photo);

        userName.setText(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.MYLISTINFO_USERNAME)));

        imageLoader.DisplayImage(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.ROOMINFO_MAINPHOTO)), photo);
//        MyListInfo lastThread = myListOperations.getLastThread(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.ROOMINFO_THREADID)));
////            if(lastThread.getIsSeen() == 0) {
////                view.setBackgroundColor(context.getResources().getColor(R.color.orange));
////            }
//            lastChatDate.setText(lastThread.getDate());
//            if(lastThread.getMessageType().equalsIgnoreCase(Constants.TEXT_STICKER)) {
//                briefMsg.setText("Sticker");
//            } else {
//                briefMsg.setText(lastThread.getMessage());
//            }

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.list_item_my_list, parent, false);
    }

}