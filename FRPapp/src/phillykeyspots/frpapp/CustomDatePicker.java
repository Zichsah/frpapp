package phillykeyspots.frpapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.DatePicker;
/**
 * This is a subclass that creates a custom date picker,
 * that prevents the ScrollView from intercepting 
 * the Calendar's scrolling motions on the events filter activity
 * 
 * @author btopportal
 */
public class CustomDatePicker extends DatePicker{
	public CustomDatePicker(Context context, AttributeSet attrs, int defStyle)
	{
		super(context,attrs,defStyle);
	}
	
	public CustomDatePicker(Context context, AttributeSet attrs)
	{
		super(context,attrs);
	}
	
	public CustomDatePicker(Context context)
	{
		super(context);
	}
	
	@SuppressLint("NewApi")
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event){
		/**Prevent parent controls from stealing our events once we've gotten a touch down */
		if(event.getActionMasked()==MotionEvent.ACTION_DOWN)
		{
			ViewParent p = getParent();
			if(p != null)
				p.requestDisallowInterceptTouchEvent(true);		
		}
		return false;
	}
}
