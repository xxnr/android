package com.ksfc.newfarmer.widget;

import com.ksfc.newfarmer.R;

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

@SuppressLint("WrongViewCast")
public class CustomDialog extends Dialog {

	public CustomDialog(Context context) {
		super(context);
	}

	public CustomDialog(Context context, int theme) {
		super(context, theme);
	}

	public static class Builder {
		private Context context;
		private String message;
        private int messageImageRes;
		private String positiveButtonText;
		private String negativeButtonText;
		private View contentView;
		private DialogInterface.OnClickListener positiveButtonClickListener;
		private DialogInterface.OnClickListener negativeButtonClickListener;

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


		public Builder setMessage(int message) {
			this.message = (String) context.getText(message);
			return this;
		}

		public Builder setContentView(View v) {
			this.contentView = v;
			return this;
		}

        public Builder setMessageImage(View v) {
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
				DialogInterface.OnClickListener listener) {
			this.positiveButtonText = (String) context
					.getText(positiveButtonText);
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setPositiveButton(String positiveButtonText,
				DialogInterface.OnClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(int negativeButtonText,
				DialogInterface.OnClickListener listener) {
			this.negativeButtonText = (String) context
					.getText(negativeButtonText);
			this.negativeButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(String negativeButtonText,
				DialogInterface.OnClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			this.negativeButtonClickListener = listener;
			return this;
		}

		@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
		public CustomDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final CustomDialog dialog = new CustomDialog(context,
					R.style.MyAlertDialog);
			View layout = inflater.inflate(R.layout.dialog_new_layout, null);
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
					( layout.findViewById(R.id.btn_cancel))
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
                if (messageImageRes != 0) {
                    Drawable drawable = context.getResources().getDrawable(messageImageRes);
                    ((TextView) layout.findViewById(R.id.alert_message)).
                            setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null);

                }
            }
			dialog.setContentView(layout);
			return dialog;
		}
	}

}
