package com.relhs.asianfinder.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;
import com.relhs.asianfinder.R;

public class CustomEditTextView extends EditText {

    public CustomEditTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!isInEditMode()) {
            init(attrs);
        }

    }

    public CustomEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            init(attrs);
        }
    }

    public CustomEditTextView(Context context) {
        super(context);
        if (!isInEditMode()) {
            init(null);
        }
    }

    private void init(AttributeSet attrs) {
        if (attrs!=null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MyTextView);

            String fontName = a.getString(R.styleable.MyTextView_fontName);
            String fontStyle = a.getString(R.styleable.MyTextView_fontStyle);
            int fontColor = a.getColor(R.styleable.MyTextView_fontColor, Color.BLACK);

            Boolean isEnabled = a.getBoolean(R.styleable.MyTextView_isEnabled, true);

            if (fontName!=null) {
                Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/"+fontName);

                if(fontStyle!=null) {
                    if(fontStyle.equalsIgnoreCase("normal")) {
                        setTypeface(myTypeface, Typeface.NORMAL);
                    } else if(fontStyle.equalsIgnoreCase("bold")) {
                        setTypeface(myTypeface, Typeface.BOLD);
                    } else if(fontStyle.equalsIgnoreCase("italics")) {
                        setTypeface(myTypeface, Typeface.ITALIC);
                    } else if(fontStyle.equalsIgnoreCase("bold_italics")) {
                        setTypeface(myTypeface, Typeface.BOLD_ITALIC);
                    } else {
                        setTypeface(myTypeface, Typeface.NORMAL);
                    }
                } else {
                    setTypeface(myTypeface, Typeface.NORMAL);
                }
            }
            setTextColor(fontColor);
            setEnabled(isEnabled);
            a.recycle();
        }
    }

}