package com.example.thanhcong.map.CaculatorModules.DirectionModules;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

import java.util.HashMap;

@SuppressLint("AppCompatCustomView")
public class CustomAutoComplateTextView extends AutoCompleteTextView{
    public CustomAutoComplateTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected CharSequence convertSelectionToString(Object selectedItem) {
        HashMap<String, String> hm = (HashMap<String, String>) selectedItem;
        return hm.get("description");
    }

}
