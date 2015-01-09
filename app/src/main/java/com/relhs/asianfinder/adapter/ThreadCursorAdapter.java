package com.relhs.asianfinder.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.relhs.asianfinder.AsianFinderApplication;
import com.relhs.asianfinder.Constants;
import com.relhs.asianfinder.DataBaseWrapper;
import com.relhs.asianfinder.R;
import com.relhs.asianfinder.data.RoomInfo;
import com.relhs.asianfinder.data.UserInfo;
import com.relhs.asianfinder.loader.ImageLoader;
import com.relhs.asianfinder.loader.Utils;
import com.relhs.asianfinder.operation.MessagesOperations;
import com.relhs.asianfinder.operation.UserInfoOperations;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ThreadCursorAdapter extends CursorAdapter {

    // EMOTICONS
    private static final Spannable.Factory spannableFactory = Spannable.Factory.getInstance();

    private static final Map<Pattern, Integer> emoticons = new HashMap<Pattern, Integer>();


    private MessagesOperations messagesOperations;
    private UserInfoOperations userInfoOperations;
    private LayoutInflater mInflater;
    private ImageLoader imageLoader;
    private Context mContext;

    private RoomInfo roomInfo;
    private UserInfo userInfo;

    public ThreadCursorAdapter(Context context, Cursor c, MessagesOperations mO, UserInfoOperations uO, int flags) {
        super(context, c, flags);
        //mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mContext = context;
        imageLoader = new ImageLoader(context);

        messagesOperations = mO;
        roomInfo = new RoomInfo();

        userInfoOperations = uO;
        userInfo = new UserInfo();
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        roomInfo = messagesOperations.getChatRoomDetails(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.MESSAGESTHREADINFO_THREADID)));
        userInfo = userInfoOperations.getUser();

        ViewHolder holder = (ViewHolder) view.getTag();

        holder.stickerLeft.setVisibility(View.GONE);
        holder.stickerRight.setVisibility(View.GONE);

        String messageType = cursor.getString(cursor.getColumnIndex(DataBaseWrapper.MESSAGESTHREADINFO_TYPE));

        if(getItemViewType(cursor, userInfo.getUser_id()) == 0) {
            holder.chatLeft.setVisibility(View.GONE);
            holder.chatRight.setVisibility(View.VISIBLE);

            holder.layoutBubbleRight.setVisibility(View.VISIBLE);
            holder.layoutBubbleLeft.setVisibility(View.GONE);

            if(messageType.equalsIgnoreCase(Constants.TEXT_STICKER)) {
                holder.layoutBubbleRight.setVisibility(View.GONE);
                holder.stickerLeft.setVisibility(View.GONE);
                holder.stickerRight.setVisibility(View.VISIBLE);

                String stickerPath = context.getResources().getString(R.string.api_stickers)+"/"+ cursor.getString(cursor.getColumnIndex(DataBaseWrapper.MESSAGESTHREADINFO_FOLDER))+"/"+ cursor.getString(cursor.getColumnIndex(DataBaseWrapper.MESSAGESTHREADINFO_FILE));
                imageLoader.DisplayImage(stickerPath, holder.stickerRight);
            } else {
//                if(messageType.equalsIgnoreCase(Constants.TEXT_PHOTO)) {
//                    String photoHtmlImgSrc = cursor.getString(cursor.getColumnIndex(DataBaseWrapper.MESSAGESTHREADINFO_MESSAGE));
//
//                    String dateTimeMsg = AsianFinderApplication.getDateCurrentTimeZone(Long.parseLong(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.MESSAGESTHREADINFO_T))));
//                    String[] datetime = dateTimeMsg.split(" ");
//                    holder.textViewRight2.setText(datetime[1].trim());
//                } else {
                    holder.textViewRight1.setText(getSmiledText(context, cursor.getString(cursor.getColumnIndex(DataBaseWrapper.MESSAGESTHREADINFO_MESSAGE))));
                    String dateTimeMsg = AsianFinderApplication.getDateCurrentTimeZone(Long.parseLong(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.MESSAGESTHREADINFO_T))));
                    String[] datetime = dateTimeMsg.split(" ");
                    holder.textViewRight2.setText(datetime[1].trim());
//                }
            }
            imageLoader.DisplayImageRounded(userInfo.getMain_photo(), holder.imageViewRight, mContext.getResources().getInteger(R.integer.resize_user_photo_size_size), mContext.getResources().getInteger(R.integer.resize_user_photo_size_size));
            //imageLoader.DisplayImage(userInfo.getMain_photo(), holder.imageViewRight);
        } else {
            holder.chatLeft.setVisibility(View.VISIBLE);
            holder.chatRight.setVisibility(View.GONE);

            holder.layoutBubbleRight.setVisibility(View.GONE);
            holder.layoutBubbleLeft.setVisibility(View.VISIBLE);

            if(messageType.equalsIgnoreCase(Constants.TEXT_STICKER)) {
                holder.layoutBubbleLeft.setVisibility(View.GONE);
                holder.stickerRight.setVisibility(View.GONE);
                holder.stickerLeft.setVisibility(View.VISIBLE);

                String stickerPath = context.getResources().getString(R.string.api_stickers)+"/"+ cursor.getString(cursor.getColumnIndex(DataBaseWrapper.MESSAGESTHREADINFO_FOLDER))+"/"+ cursor.getString(cursor.getColumnIndex(DataBaseWrapper.MESSAGESTHREADINFO_FILE));
                imageLoader.DisplayImage(stickerPath, holder.stickerLeft);

            } else if(messageType.equalsIgnoreCase(Constants.TEXT_PHOTO)) {
                holder.stickerLeft.setVisibility(View.GONE);
                holder.textViewLeft1.setVisibility(View.GONE);
                holder.photoUploadLeft.setVisibility(View.VISIBLE);

                String photoUrl = mContext.getResources().getString(R.string.asian_frinder_url)+cursor.getString(cursor.getColumnIndex(DataBaseWrapper.MESSAGESTHREADINFO_MESSAGE));

                imageLoader.DisplayImageRoundedCorners(photoUrl, holder.photoUploadLeft);

                String dateTimeMsg = AsianFinderApplication.getDateCurrentTimeZone(Long.parseLong(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.MESSAGESTHREADINFO_T))));
                String[] datetime = dateTimeMsg.split(" ");
                holder.textViewLeft2.setText(datetime[1].trim());
            } else {
                holder.textViewLeft1.setText(getSmiledText(context, cursor.getString(cursor.getColumnIndex(DataBaseWrapper.MESSAGESTHREADINFO_MESSAGE))));
                String dateTimeMsg = AsianFinderApplication.getDateCurrentTimeZone(Long.parseLong(cursor.getString(cursor.getColumnIndex(DataBaseWrapper.MESSAGESTHREADINFO_T))));
                String[] datetime = dateTimeMsg.split(" ");
                holder.textViewLeft2.setText(datetime[1].trim());
            }
            imageLoader.DisplayImageRounded(roomInfo.getMain_photo(), holder.imageViewLeft, mContext.getResources().getInteger(R.integer.resize_user_photo_size_size), mContext.getResources().getInteger(R.integer.resize_user_photo_size_size));
            //imageLoader.DisplayImage(roomInfo.getMain_photo(), holder.imageViewLeft);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewHolder holder = new ViewHolder();
        View v = mInflater.inflate(R.layout.list_item_threads, null);

        holder.layoutBubbleLeft = (LinearLayout) v.findViewById(R.id.layoutBubbleLeft);
        holder.chatLeft = (RelativeLayout) v.findViewById(R.id.bubbleLeft);
        holder.textViewLeft1 = (TextView) v.findViewById(R.id.briefMsgLeft);
        holder.textViewLeft2 = (TextView) v.findViewById(R.id.lastChatDateLeft);
        holder.imageViewLeft = (ImageView) v.findViewById(R.id.photoLeft);
        holder.stickerLeft = (ImageView) v.findViewById(R.id.stickerLeft);
        holder.photoUploadLeft = (ImageView) v.findViewById(R.id.photoUploadLeft);

        holder.layoutBubbleRight = (LinearLayout) v.findViewById(R.id.layoutBubbleRight);
        holder.chatRight = (RelativeLayout) v.findViewById(R.id.bubbleRight);
        holder.textViewRight1 = (TextView) v.findViewById(R.id.briefMsgRight);
        holder.textViewRight2 = (TextView) v.findViewById(R.id.lastChatDateRight);
        holder.imageViewRight = (ImageView) v.findViewById(R.id.photoRight);
        holder.stickerRight = (ImageView) v.findViewById(R.id.stickerRight);
        holder.photoUploadRight = (ImageView) v.findViewById(R.id.photoUploadRight);

        v.setTag(holder);

        return v;

    }
    public static class ViewHolder {
        public LinearLayout layoutBubbleLeft;
        public RelativeLayout chatLeft;
        public TextView textViewLeft1;
        public TextView textViewLeft2;
        public ImageView imageViewLeft;
        public ImageView stickerLeft;
        public ImageView photoUploadLeft;

        public LinearLayout layoutBubbleRight;
        public RelativeLayout chatRight;
        public TextView textViewRight1;
        public TextView textViewRight2;
        public ImageView imageViewRight;
        public ImageView stickerRight;
        public ImageView photoUploadRight;

    }

    @Override
    public Cursor getCursor() {
        return super.getCursor();
    }

    @Override
    public void changeCursor(Cursor cursor) {
        super.swapCursor(cursor);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    private int getItemViewType(Cursor cursor, int userId) {
        if(cursor.getInt(cursor.getColumnIndex(DataBaseWrapper.MESSAGESTHREADINFO_F)) == userId) {
            return 0;
        } else {
            return 1;
        }
    }

    static {
        addPattern(emoticons,":)", R.drawable.emo_1);
        addPattern(emoticons,":(", R.drawable.emo_2);
        addPattern(emoticons,";)", R.drawable.emo_3);
        addPattern(emoticons,":D", R.drawable.emo_4);
        addPattern(emoticons,":-/", R.drawable.emo_5);
        addPattern(emoticons,":X", R.drawable.emo_6);
        addPattern(emoticons,":\">", R.drawable.emo_7);
        addPattern(emoticons,":P", R.drawable.emo_8);
        addPattern(emoticons,":-*", R.drawable.emo_9);
        addPattern(emoticons,"=((", R.drawable.emo_10);
        addPattern(emoticons,":O", R.drawable.emo_11);
        addPattern(emoticons,"X(", R.drawable.emo_12);
        addPattern(emoticons,":>", R.drawable.emo_13);
        addPattern(emoticons,"B-)", R.drawable.emo_14);
        addPattern(emoticons,":-S", R.drawable.emo_15);
        addPattern(emoticons,":|", R.drawable.emo_16);
        addPattern(emoticons,"=))", R.drawable.emo_17);
        addPattern(emoticons,"O:-)", R.drawable.emo_18);
        addPattern(emoticons,":-B", R.drawable.emo_19);
        addPattern(emoticons,"=;", R.drawable.emo_20);
        addPattern(emoticons,"I-)", R.drawable.emo_21);
        addPattern(emoticons,"8-|", R.drawable.emo_22);
        addPattern(emoticons,"L-)", R.drawable.emo_23);
        addPattern(emoticons,":-&", R.drawable.emo_24);
        addPattern(emoticons,":-$", R.drawable.emo_25);
        addPattern(emoticons,"[-(", R.drawable.emo_26);
        addPattern(emoticons,"8-}", R.drawable.emo_27);
        addPattern(emoticons,"=P~", R.drawable.emo_28);
        addPattern(emoticons,":-?", R.drawable.emo_29);
        addPattern(emoticons,"#-O", R.drawable.emo_30);
        addPattern(emoticons,"=D>", R.drawable.emo_31);
        addPattern(emoticons,"@-)", R.drawable.emo_32);
        addPattern(emoticons,":^O", R.drawable.emo_33);
        addPattern(emoticons,":-W", R.drawable.emo_34);
        addPattern(emoticons,":-<", R.drawable.emo_35);
        addPattern(emoticons,":@)", R.drawable.emo_36);
        addPattern(emoticons,"3:-O", R.drawable.emo_37);
        addPattern(emoticons,"@};-", R.drawable.emo_38);
        addPattern(emoticons,"%%-", R.drawable.emo_39);
        addPattern(emoticons,"**==", R.drawable.emo_40);
        addPattern(emoticons,"(~~)", R.drawable.emo_41);
        addPattern(emoticons,"~O)", R.drawable.emo_42);
        addPattern(emoticons,"8-X", R.drawable.emo_43);
        addPattern(emoticons,">-)", R.drawable.emo_44);
        addPattern(emoticons,":-L", R.drawable.emo_45);
        addPattern(emoticons,"[-O<", R.drawable.emo_46);
        addPattern(emoticons,"$-)", R.drawable.emo_47);
        addPattern(emoticons,":-\"", R.drawable.emo_48);
        addPattern(emoticons,"[-X", R.drawable.emo_49);
        addPattern(emoticons,":D/", R.drawable.emo_50);
        addPattern(emoticons,">:/", R.drawable.emo_51);
        addPattern(emoticons,";))", R.drawable.emo_52);
        addPattern(emoticons,"O->", R.drawable.emo_53);
        addPattern(emoticons,"O=>", R.drawable.emo_54);
        addPattern(emoticons,"O-+", R.drawable.emo_55);
        addPattern(emoticons,"(%)", R.drawable.emo_56);
        addPattern(emoticons,":-@", R.drawable.emo_57);
        addPattern(emoticons,":-J", R.drawable.emo_58);
        addPattern(emoticons,":-C", R.drawable.emo_59);
        addPattern(emoticons,":-H", R.drawable.emo_60);
        addPattern(emoticons,":-T", R.drawable.emo_61);
        addPattern(emoticons,"8->", R.drawable.emo_62);
        addPattern(emoticons,":O3", R.drawable.emo_63);
        addPattern(emoticons,"M/", R.drawable.emo_64);
        addPattern(emoticons,":!!", R.drawable.emo_65);
        addPattern(emoticons,"X_X", R.drawable.emo_66);
        addPattern(emoticons,":-Q", R.drawable.emo_67);
        // ...
    }
    private static void addPattern(Map<Pattern, Integer> map, String smile,
                                   int resource) {
        map.put(Pattern.compile(Pattern.quote(smile)), resource);
    }

    public static boolean addSmiles(Context context, Spannable spannable) {
        boolean hasChanges = false;
        for (Map.Entry<Pattern, Integer> entry : emoticons.entrySet()) {
            Matcher matcher = entry.getKey().matcher(spannable);
            while (matcher.find()) {
                boolean set = true;
                for (ImageSpan span : spannable.getSpans(matcher.start(),
                        matcher.end(), ImageSpan.class))
                    if (spannable.getSpanStart(span) >= matcher.start()
                            && spannable.getSpanEnd(span) <= matcher.end())
                        spannable.removeSpan(span);
                    else {
                        set = false;
                        break;
                    }
                if (set) {
                    hasChanges = true;
                    spannable.setSpan(new ImageSpan(context, entry.getValue()),
                            matcher.start(), matcher.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return hasChanges;
    }

    public static Spannable getSmiledText(Context context, CharSequence text) {
        Spannable spannable = spannableFactory.newSpannable(text);
        addSmiles(context, spannable);
        return spannable;
    }

}