package com.ksfc.newfarmer.widget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.Selection;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ksfc.newfarmer.R;

@SuppressLint("WrongViewCast")
public class CustomDialogForShopCarCount extends Dialog {
    public  static EditText editText;

    public CustomDialogForShopCarCount(Context context) {
        super(context);
    }

    public CustomDialogForShopCarCount(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Context context;
        private String message;
        private String positiveButtonText;
        private String negativeButtonText;
        private String edit_Text;
        private View contentView;
        private OnClickListener positiveButtonClickListener;
        private OnClickListener negativeButtonClickListener;
        private OnClickListener imageLiftClickListener;
        private OnClickListener imageRightClickListener;


        public Builder(Context context) {
            this.context = context;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setEditText(String edit_Text) {
            this.edit_Text = edit_Text;
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


        /**
         * 购物车的三个按钮
         */

        public Builder SetLiftButton(OnClickListener listener) {
            this.imageLiftClickListener = listener;
            return this;
        }

        public Builder SetRightButton(OnClickListener listener) {
            this.imageRightClickListener = listener;
            return this;
        }


        public CustomDialogForShopCarCount create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final CustomDialogForShopCarCount dialog = new CustomDialogForShopCarCount(context,
                    R.style.MyAlertDialog);
            View layout = inflater.inflate(R.layout.dialog_new_layout_shop_car, null);
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
            } else if (contentView != null) {

            }

            // set the lift button
            if (imageLiftClickListener != null) {
                (layout.findViewById(R.id.dialog_item_jian))
                        .setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                imageLiftClickListener.onClick(dialog,
                                        DialogInterface.BUTTON_NEUTRAL);
                            }
                        });
            }
            // set the Right button
            if (imageRightClickListener != null) {
                (layout.findViewById(R.id.dialog_item_jia))
                        .setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                imageRightClickListener.onClick(dialog,
                                        DialogInterface.BUTTON_NEUTRAL);
                            }
                        });
            }

            // set the  editText
            if (edit_Text != null) {
                editText = ((EditText) layout.findViewById(R.id.dialog_item_geshu));
                editText.setText(edit_Text);
                // 光标移到最后
                Editable eText = editText.getText();
                Selection.setSelection(eText, eText.length());
            }


            dialog.setContentView(layout);
            return dialog;
        }
    }

    public EditText getEditButton() {
        return editText;
    }

}
