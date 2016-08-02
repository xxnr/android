package com.ksfc.newfarmer.widget.dialog;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ksfc.newfarmer.R;
import com.ksfc.newfarmer.widget.ClearEditText;

@SuppressLint("WrongViewCast")
public class CustomDialogForSms extends Dialog {
    public static ClearEditText editText;
    public static ImageView sms_auth_code_iv;
    public static ImageView sms_auth_code_refresh_iv;
    public static TextView code_error;
    public static LinearLayout sms_auth_code_bg;



    public CustomDialogForSms(Context context) {
        super(context);
    }

    public CustomDialogForSms(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Context context;
        private String message;
        private String positiveButtonText;
        private String negativeButtonText;
        private OnClickListener positiveButtonClickListener;
        private OnClickListener negativeButtonClickListener;


        public Builder(Context context) {
            this.context = context;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }


        public Builder setPositiveButton(String positiveButtonText,
                                         OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }


        public Builder setNegativeButton(String negativeButtonText,
                                         OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        public CustomDialogForSms create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final CustomDialogForSms dialog = new CustomDialogForSms(context,
                    R.style.MyAlertDialog);
            View layout = inflater.inflate(R.layout.dialog_new_layout_sms, null);
            dialog.addContentView(layout, new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            // set the dialog title
            // set the confirm button
            if (positiveButtonText != null) {
                ((Button) layout.findViewById(R.id.btn_sure))
                        .setText(positiveButtonText);
                if (positiveButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.btn_sure))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    positiveButtonClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_POSITIVE);
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.btn_sure).setVisibility(View.GONE);
            }
            // set the cancel button
            if (negativeButtonText != null) {
                ((Button) layout.findViewById(R.id.btn_cancel))
                        .setText(negativeButtonText);
                if (negativeButtonClickListener != null) {
                    (layout.findViewById(R.id.btn_cancel))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    negativeButtonClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_NEGATIVE);
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
            }
            // set the content message
            if (message != null) {
                ((TextView) layout.findViewById(R.id.alert_message))
                        .setText(message);
            }
            editText = ((ClearEditText) layout.findViewById(R.id.sms_auth_code_et));
            sms_auth_code_iv = ((ImageView) layout.findViewById(R.id.sms_auth_code_iv));
            sms_auth_code_refresh_iv = ((ImageView) layout.findViewById(R.id.sms_auth_code_refresh_iv));
            code_error=((TextView) layout.findViewById(R.id.code_error));
            sms_auth_code_bg=(LinearLayout)layout.findViewById(R.id.sms_auth_code_bg);
            dialog.setContentView(layout);
            return dialog;
        }
    }

}