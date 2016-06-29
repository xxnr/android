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
public class CustomDialogForInviter extends Dialog {

	public CustomDialogForInviter(Context context) {
		super(context);
	}

	public CustomDialogForInviter(Context context, int theme) {
		super(context, theme);
	}

	public static class Builder {
		private Context context;
		private String message;
		private String message2;
		private String title;
        private int messageImageRes;
		private String positiveButtonText;
		private String negativeButtonText;
		private View contentView;
		private OnClickListener positiveButtonClickListener;
		private OnClickListener negativeButtonClickListener;

		public Builder(Context context) {
			this.context = context;
		}

		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}
		public Builder setMessage2(String message2) {
			this.message2 = message2;
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

		public Builder setTitle(String title) {
			this.title = title;
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

		@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
		public CustomDialogForInviter create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final CustomDialogForInviter dialog = new CustomDialogForInviter(context,
					R.style.MyAlertDialog);
			View layout = inflater.inflate(R.layout.dialog_add_inviter, null);
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
			if (message2!=null){
				((TextView) layout.findViewById(R.id.alert_message2))
						.setText(message2);
			}

			if (title!=null){
				((TextView) layout.findViewById(R.id.alert_title))
						.setText(title);
			}
			dialog.setContentView(layout);
			return dialog;
		}
	}

}
