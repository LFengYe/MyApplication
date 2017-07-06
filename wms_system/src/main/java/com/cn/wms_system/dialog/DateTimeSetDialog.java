package com.cn.wms_system.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.cn.wms_system_new.R;


public class DateTimeSetDialog extends Dialog {

	private Button cancelButton;
	private Button confirmButton;
	private DatePicker datePicker;
	private TimePicker timePicker;
	private GetDialogData dialogData;
	
	private View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View paramView) {
			if (confirmButton.getId() == paramView.getId()) {
				dialogData.getDate(datePicker);
				dialogData.getTime(timePicker);
				dismiss();
			}
			if (cancelButton.getId() == paramView.getId()) {
				dismiss();
			}
		}
	};

	public DateTimeSetDialog(Context context) {
		super(context);
	}
	
	public DateTimeSetDialog(Context context, GetDialogData dialogData) {
		super(context);
		this.dialogData = dialogData;
	}
	
	public DateTimeSetDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.layout_date_time);
		initComponents();
		setComponents();
		super.onCreate(savedInstanceState);
	}
	
	private void initComponents() {
		datePicker = ((DatePicker) findViewById(R.id.date_picker));
		timePicker = ((TimePicker) findViewById(R.id.time_picker));
		confirmButton = ((Button) findViewById(R.id.date_time_confirm));
		cancelButton = ((Button) findViewById(R.id.date_time_cancel));
	}

	private void setComponents() {
		this.timePicker.setIs24HourView(Boolean.valueOf(true));
		this.confirmButton.setOnClickListener(this.onClickListener);
		this.cancelButton.setOnClickListener(this.onClickListener);
	}

	public interface GetDialogData {
		public abstract void getDate(DatePicker paramDatePicker);

		public abstract void getTime(TimePicker paramTimePicker);
	}
}
