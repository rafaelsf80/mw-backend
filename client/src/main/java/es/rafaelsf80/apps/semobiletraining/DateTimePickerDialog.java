package es.rafaelsf80.apps.semobiletraining;


import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.google.api.client.util.DateTime;
import com.google.mw.backend.caseApi.model.CaseBean;

import java.util.Calendar;

//import caseApi.model.CaseBean;


public class DateTimePickerDialog extends Dialog {

    private final String TAG = getClass().getSimpleName();

    // DatePicker reference
    private DatePicker              datePicker;
    // TimePicker reference
    private TimePicker              timePicker;
    // Calendar reference
    private Calendar                mCalendar;

    Context mContext;

    Handler mHandler;

    public DateTimePickerDialog(Context ctx, Handler handler, CaseBean bean){

        super(ctx);

        this.setContentView(R.layout.date_time_dialog);
        setTitle("Closure Date for Case");

        mHandler = handler;

        Log.d(TAG, "Init DateTimePickerDialog");

        // Grab a Calendar instance
        mCalendar = Calendar.getInstance();

        // Init date picker
        datePicker = (DatePicker) findViewById(R.id.date_picker);
        datePicker.init(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH),
                onDateChangedListener);

        // Init time picker
        timePicker = (TimePicker) findViewById(R.id.time_picker);
        timePicker.setOnTimeChangedListener(onTimeChangedListener);


        // Update CaseBeanwhen the "OK" button is clicked
        ((Button) findViewById(R.id.bt_set_date_time)).setOnClickListener(new Button.OnClickListener() {

            public void onClick(View v) {

                Log.d(TAG, "OK: " + new DateTime(mCalendar.getTime()).toString());
                		
                		/* Send message back to CaseDetails activity (closedDate) */
                Message msg = new Message();
                msg.obj = new DateTime( mCalendar.getTime() );
                mHandler.sendMessage(msg);

//                        ((TextView) findViewById(R.id.Date)).setText(datePicker.get(Calendar.YEAR) + "/" + (mDateTimePicker.get(Calendar.MONTH)+1) + "/"
//                                        + mDateTimePicker.get(Calendar.DAY_OF_MONTH));
//                        if (mDateTimePicker.is24HourView()) {
//                                ((TextView) findViewById(R.id.Time)).setText(mDateTimePicker.get(Calendar.HOUR_OF_DAY) + ":" + mDateTimePicker.get(Calendar.MINUTE));
//                        } else {
//                                ((TextView) findViewById(R.id.Time)).setText(mDateTimePicker.get(Calendar.HOUR) + ":" + mDateTimePicker.get(Calendar.MINUTE) + " "
//                                                + (mDateTimePicker.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM"));
//                        }

                dismiss();
            }
        });

        // Cancel the dialog when the "Cancel" button is clicked
        ((Button) findViewById(R.id.bt_cancel)).setOnClickListener(new Button.OnClickListener() {

            public void onClick(View v) {
                dismiss();
            }
        });

        // Reset Date and Time pickers when the "Reset" button is clicked
        ((Button) findViewById(R.id.bt_reset_date_time)).setOnClickListener(new Button.OnClickListener() {

            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                datePicker.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
                timePicker.setCurrentMinute(c.get(Calendar.MINUTE));
            }
        });
    }

    private DatePicker.OnDateChangedListener onDateChangedListener = new DatePicker.OnDateChangedListener() {

        public void onDateChanged(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

            mCalendar.set(year, monthOfYear, dayOfMonth, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE));
        }
    };

    private TimePicker.OnTimeChangedListener onTimeChangedListener = new TimePicker.OnTimeChangedListener() {

        @Override
        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

            mCalendar.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
        }
    };

    // Convenience wrapper for internal Calendar instance
    public long getDateTimeMillis() {
        return mCalendar.getTimeInMillis();
    }

    // Convenience wrapper for internal TimePicker instance
    public void setIs24HourView(boolean is24HourView) {
        timePicker.setIs24HourView(is24HourView);
    }

    // Convenience wrapper for internal TimePicker instance
    public boolean is24HourView() {
        return timePicker.is24HourView();
    }

    public interface EditNameDialogListener {
        void onFinishEditDialog(String inputText);
    }
}