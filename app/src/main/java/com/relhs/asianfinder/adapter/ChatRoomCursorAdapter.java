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
import com.relhs.asianfinder.data.ThreadsInfo;
import com.relhs.asianfinder.loader.ImageLoader;
import com.relhs.asianfinder.operation.MessagesOperations;

public class ChatRoomCursorAdapter extends CursorAdapter {

    private MessagesOperations messagesOperations;
    private LayoutInflater mInflater;
    private ImageLoader imageLoader;
    private Context mContext;

    public ChatRoomCursorAdapter(Context context, Cursor c, MessagesOperations mO, int flags) {
        super(context, c, flags);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        imageLoader = new ImageLoader(context);

        mContext = context;
        messagesOperations = mO;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if(messagesOperations.CheckIsDataAlreadyInDBorNot(DataBaseWrapper.MESSAGESTHREADINFO,
                DataBaseWrapper.MESSAGESTHREADINFO_THREADID,
                cursor.getInt(cursor.getColumnIndex(DataBaseWrapper.ROOMINFO_THREADID)), 0, null)) {

        TextView userName = (TextView) view.findViewById(R.id.userName);
        TextView briefMsg = (TextView) view.findViewById(R.id.briefMsg);
        TextView lastChatDate = (TextView) view.findViewById(R.id.lastChatDate);
        ImageView photo = (ImageView) view.findViewById(R.id.photo);

        userName.setText(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.ROOMINFO_USERNAME)));

        //lastChatDate.setText(lastThread.get);
        //imageLoader.DisplayImage(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.ROOMINFO_MAINPHOTO)), photo);
        imageLoader.DisplayImageRounded(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.ROOMINFO_MAINPHOTO)), photo, mContext.getResources().getInteger(R.integer.room_resize_user_photo_size_size), mContext.getResources().getInteger(R.integer.room_resize_user_photo_size_size));
        ThreadsInfo lastThread = messagesOperations.getLastThread(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.ROOMINFO_THREADID)));
//            if(lastThread.getIsSeen() == 0) {
//                view.setBackgroundColor(context.getResources().getColor(R.color.orange));
//            }
            lastChatDate.setText(lastThread.getDate());
            if(lastThread.getMessageType().equalsIgnoreCase(Constants.TEXT_STICKER)) {
                briefMsg.setText("Sticker");
            } else if(lastThread.getMessageType().equalsIgnoreCase(Constants.TEXT_PHOTO)) {
                briefMsg.setText("Photo");
            } else {
                briefMsg.setText(lastThread.getMessage());
            }

        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.list_item_chatroom, parent, false);
    }

}