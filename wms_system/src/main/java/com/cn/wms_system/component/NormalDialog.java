package com.cn.wms_system.component;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.cn.wms_system_new.R;


public class NormalDialog extends Dialog {

	public NormalDialog(Context context, int theme) {
		super(context, theme);
	}

	public NormalDialog(Context context) {
		super(context);
	}
	 public static class Builder {  
	        private Context context;
	        private String title;
	        private String message;
	        private View contentView;
	        private String pbText;
	        private String nbText;
	        private OnClickListener pbClickListener;
	        private OnClickListener nbClickListener;

	        public Builder(Context context) {
	            this.context = context;
	        }

	        public Builder setMessage(String message) {
	            this.message = message;
	            return this;
	        }

	        /**
	         * Set the Dialog message from resource
	         *
	         * @return
	         */
	        public Builder setMessage(int message) {
	            this.message = (String) context.getText(message);
	            return this;
	        }

	        /**
	         * Set the Dialog title from resource
	         *
	         * @param title
	         * @return
	         */
	        public Builder setTitle(int title) {
	            this.title = (String) context.getText(title);
	            return this;
	        }

	        /**
	         * Set the Dialog title from String
	         *
	         * @param title
	         * @return
	         */

	        public Builder setTitle(String title) {
	            this.title = title;
	            return this;
	        }

	        public Builder setContentView(View v) {
	            this.contentView = v;
	            return this;
	        }

	        /**
	         * Set the positive button resource and it's listener
	         *
	         * @return
	         */
	        public Builder setPositiveButton(String pbText,OnClickListener listener) {
	            this.pbText=pbText;
	            this.pbClickListener = listener;
	            return this;
	        }


	        public Builder setNegativeButton(String nbText,OnClickListener listener) {
	          this.nbText=nbText;
	            this.nbClickListener = listener;  
	            return this;  
	        }  
	  
	     
	  
	        public NormalDialog create() {  
	            LayoutInflater inflater = (LayoutInflater) context
	                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            // instantiate the dialog with the custom Theme  
	            final NormalDialog dialog = new NormalDialog(context, R.style.Dialog);
	            View layout = inflater.inflate(R.layout.normal_dialog, null);
	            
	            if(TextUtils.isEmpty(title)){
	            	//没有表态内容
	            	((TextView)layout.findViewById(R.id.title)).setVisibility(View.GONE);
	            }else{
	            	  ((TextView)layout.findViewById(R.id.title)).setText(title);
	            }
	            dialog.addContentView(layout, new LayoutParams(
	                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
	            // set the dialog title  
	            
	            // set the confirm button  
	              ((Button) layout.findViewById(R.id.positiveButton))
                  .setText(pbText);  
	              ((Button) layout.findViewById(R.id.negativeButton))
                  .setText(nbText);  
	                if (pbClickListener != null) {  
	                	((Button)layout.findViewById(R.id.positiveButton)).setOnClickListener(new View.OnClickListener() {
							
							@Override
							public void onClick(View v) {
								 pbClickListener.onClick(dialog,
                                         DialogInterface.BUTTON_NEGATIVE);
							}
						});
	                   

	            // set the cancel button  
	                if (nbClickListener != null) {  
	                    ((Button) layout.findViewById(R.id.negativeButton))
	                            .setOnClickListener(new View.OnClickListener() {

									@Override
									public void onClick(View v) {
										nbClickListener.onClick(dialog,
												DialogInterface.BUTTON_NEGATIVE);
									}


								});
	                }  
	            }
	        
	            // set the content message  
	            if (message != null) {  
	                ((TextView) layout.findViewById(R.id.message)).setText(message);
	            }
	            dialog.setContentView(layout);  
	            return dialog;  
	        }  
	    }  
}
