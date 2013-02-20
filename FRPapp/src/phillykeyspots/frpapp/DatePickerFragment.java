package phillykeyspots.frpapp;

import java.util.Calendar;
import java.util.Locale;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{
	DatePicker picker;
	//public final static String EXTRA_MESSAGE = "com.example.kapp.QUERY";
	
	protected DialogInterface.OnCancelListener btn_cancel_listener = new DialogInterface.OnCancelListener() {
		public void onCancel(DialogInterface dialog) {
			dialog.dismiss();
		}
	};
	
	protected DialogInterface.OnClickListener btn_set_listener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			picker.clearFocus();
			onDateSet(picker,picker.getYear(),picker.getMonth(),picker.getDayOfMonth());
		}
	};
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		//Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		
		//Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}
	
	public void onDateSet(DatePicker view, int year, int month, int day){
		String querydate = "https://www.phillykeyspots.org/events.xml" + String.format (Locale.US,"/%d-%02d-%02d",year,month,day);
		Intent intent = new Intent();
		intent.setClass(getActivity(),EventsActivity.class);
		intent.putExtra("EXTRA_MESSAGE", querydate);
		startActivity(intent);
	}
}
