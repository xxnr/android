package com.ksfc.newfarmer.utils;

import java.io.UnsupportedEncodingException;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

/* 
 * 监听输入内容是否超出最大长度，并设置光标位置
 * 限制数字字符数，1汉字相当于2字母 或2数字
 * */
public class MaxLengthWatcher implements TextWatcher {

    private int maxLen = 0;
    private EditText editText = null;
    private Context mContext;


    public MaxLengthWatcher(int maxLen, EditText editText, Context context) {
        this.maxLen = maxLen;
        this.editText = editText;
        this.mContext = context;
    }

    public void afterTextChanged(Editable s) {
        // TODO Auto-generated method stub  
        if (!TextUtils.isEmpty(s.toString())) {
            String limitSubstring = getLimitSubstring(s.toString());
            if (!TextUtils.isEmpty(limitSubstring)) {
                if (!limitSubstring.equals(s.toString())) {
                    editText.setText(limitSubstring);
                    editText.setSelection(limitSubstring.length());
                }
            }
        }
    }

    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                  int arg3) {
        // TODO Auto-generated method stub  

    }

    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub  

    }

    private String getLimitSubstring(String inputStr) {
        int orignLen = inputStr.length();
        int resultLen = 0;
        String temp = null;
        for (int i = 0; i < orignLen; i++) {
            temp = inputStr.substring(i, i + 1);
            try {
                // 3 bytes to indicate chinese word,1 byte to indicate english
                // word ,in utf-8 encode
                if (temp.getBytes("utf-8").length == 3) {
                    resultLen += 2;
                } else {
                    resultLen++;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (resultLen > maxLen) {
                Toast toast = Toast.makeText(mContext, "您的输入超过限制", Toast.LENGTH_SHORT);
                toast.show();
                return inputStr.substring(0, i);
            }
        }
        return inputStr;
    }

}  