package com.ksfc.newfarmer.widget.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ksfc.newfarmer.R;

/**
 * Created by HePeng on 2016/3/29.
 */
public class CustomToast extends Toast {

    public CustomToast(Context context) {
        super(context);
    }

    public static class Builder {
        private Context context;
        private String message;
        private int messageImageRes;
        private ImageView toast_image;
        private TextView toast_text;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setMessageImage(int res) {
            this.messageImageRes = res;
            return this;
        }

        public CustomToast create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            CustomToast toast = new CustomToast(context);

            View layout = inflater.inflate(R.layout.toast_large_center, null);

            toast_image = ((ImageView) layout.findViewById(R.id.toast_image));
            toast_text = ((TextView) layout.findViewById(R.id.toast_text));

            if (messageImageRes != 0) {
                toast_image.setImageResource(messageImageRes);
            }
            if (message != null) {
                toast_text.setText(message);
            }
            toast.setView(layout);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            return toast;
        }
    }


}
