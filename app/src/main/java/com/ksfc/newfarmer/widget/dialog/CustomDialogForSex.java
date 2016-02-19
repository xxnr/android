package com.ksfc.newfarmer.widget.dialog;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.ksfc.newfarmer.R;

@SuppressLint("WrongViewCast")
public class CustomDialogForSex extends Dialog {

    public CustomDialogForSex(Context context) {
        super(context);
    }

    public CustomDialogForSex(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Context context;
        private String message;
        private String positiveButtonText;
        private String negativeButtonText;
        private int positiveButtonImage=0;
        private int negativeButtonImage=0;

        private String normalButtonText;
        private View contentView;
        private OnClickListener positiveButtonClickListener;
        private OnClickListener negativeButtonClickListener;
        private OnClickListener normalButtonClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }


        public Builder setPositiveButtonImage(int res) {
            this.positiveButtonImage = res;
            return this;
        }

        public Builder setNegativeButtonImage(int res) {
            this.negativeButtonImage = res;
            return this;
        }

        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }


        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         *
         * @param positiveButtonText
         * @return
         */
        public Builder setPositiveButton(int positiveButtonText,
                                         OnClickListener listener) {
            this.positiveButtonText = (String) context
                    .getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText,
                                         OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(int negativeButtonText,
                                         OnClickListener listener) {
            this.negativeButtonText = (String) context
                    .getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText,
                                         OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setNormalButton(int normalButtonText,
                                       OnClickListener listener) {
            this.normalButtonText = (String) context
                    .getText(normalButtonText);
            this.normalButtonClickListener = listener;
            return this;
        }

        public Builder setNormalButton(String normalButtonText,
                                       OnClickListener listener) {
            this.normalButtonText = normalButtonText;
            this.normalButtonClickListener = listener;
            return this;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        public CustomDialogForSex create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final CustomDialogForSex dialog = new CustomDialogForSex(context,
                    R.style.MyAlertDialog);
            View layout = inflater.inflate(R.layout.dialog_new_layout_sex , null);
            dialog.addContentView(layout, new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            // set the dialog title
            // set the confirm button
            if (positiveButtonText != null) {
                ((Button) layout.findViewById(R.id.btn_sure))
                        .setText(positiveButtonText);

                if (positiveButtonImage != 0) {
                    Drawable drawable = context.getResources().getDrawable(positiveButtonImage);
                    ((Button) layout.findViewById(R.id.btn_sure))
                            .setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null);
                }

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

            // set the cancel button
            if (normalButtonText != null) {
                ((Button) layout.findViewById(R.id.btn_normal))
                        .setText(normalButtonText);
                if (negativeButtonImage != 0) {
                    Drawable drawable = context.getResources().getDrawable(negativeButtonImage);
                    ((Button) layout.findViewById(R.id.btn_normal))
                            .setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null);
                }
                if (normalButtonClickListener != null) {
                    (layout.findViewById(R.id.btn_normal))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    normalButtonClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_NEUTRAL);
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.btn_normal).setVisibility(View.GONE);
            }
            // set the content message
            if (message != null) {
                ((TextView) layout.findViewById(R.id.alert_message))
                        .setText(message);
            } else if (contentView != null) {

            }
            dialog.setContentView(layout);
            return dialog;
        }
    }

}
