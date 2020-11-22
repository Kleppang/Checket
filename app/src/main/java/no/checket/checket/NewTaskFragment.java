package no.checket.checket;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.text.BreakIterator;
import java.util.Calendar;
import java.util.Date;

public class NewTaskFragment extends DialogFragment {

    // Interface for fragment communication
    // Includes callbacks to MainActivity
    public interface NewTaskDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, String header, String details, Date date, String icon);
        void onDialogNegativeClick(DialogFragment dialog);
    }
    NewTaskDialogListener listener;

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Log.i("Petter", "NewTaskFragment.onCreateDialog()");
        // Begin building a dialog, in the activity that called it.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = LayoutInflater.from(getContext());
        // Get handles to inputs
        final View view = inflater.inflate(R.layout.dialog_new_task, null);
        final Spinner mHeader = (Spinner) view.findViewById(R.id.category_spinner);
        final EditText mDetails = (EditText) view.findViewById(R.id.details_text);
        final Button mDate = (Button) view.findViewById(R.id.date_input);
        final Button mTime = (Button) view.findViewById(R.id.time_input);

        setCurrentDateTime(mDate, mTime);

        // Inflate and set buttons for dialog
        // Null represents the parent view, which is none for this dialog
        // The layout file referenced is that of a customized dialog
        builder.setView(view)
        // Adding buttons for saving or aborting
            .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    Log.i("Petter", "NewTaskFragment.onClick() positive button");
                    String header = mHeader.getSelectedItem().toString();
                    String details = mDetails.getText().toString();
                    // TODO: convert date and time to a single Date
                    String sDate = mDate.getText().toString();
                    // Separate string
                    // Into year...
                    String sYear = Character.toString(sDate.charAt(0));
                    sYear += Character.toString(sDate.charAt(1));
                    int year = Integer.parseInt(sYear);
                    // Month...
                    String sMonth = Character.toString(sDate.charAt(3));
                    sMonth += Character.toString(sDate.charAt(4));
                    int month = Integer.parseInt(sMonth);
                    // Day...
                    String sDay = Character.toString(sDate.charAt(6));
                    sDay += Character.toString(sDate.charAt(7));
                    int day = Integer.parseInt(sDay);
                    // Hour...
                    String sTime = mTime.getText().toString();
                    String sHour = Character.toString(sTime.charAt(0));
                    sHour += Character.toString(sTime.charAt(1));
                    int hour = Integer.parseInt(sHour);
                    // And minute
                    String sMinute = Character.toString(sTime.charAt(3));
                    sMinute += Character.toString(sTime.charAt(4));
                    int minute = Integer.parseInt(sMinute);
                    // Assemble to a date
                    Date date = new Date(year, month, day, hour, minute);

                    Log.i("Petter", sDate + ", " + sTime);
                    // The naming convention of the icons is ic_CATEGORY
                    String icon = "ic_" + header;
                    listener.onDialogPositiveClick(NewTaskFragment.this, header, details, date, icon);
                }
            })
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    Log.i("Petter", "NewTaskFragment.onClick() negative button");
                    listener.onDialogNegativeClick(NewTaskFragment.this);
                    NewTaskFragment.this.getDialog().cancel();
                }
            });
        return builder.create();
    }

    public void setCurrentDateTime(Button mDate, Button mTime) {
        // Strings to show current date and time on buttons
        Calendar cal = Calendar.getInstance();
        String day = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
        String month = Integer.toString(cal.get(Calendar.MONTH));
        String year = Integer.toString(cal.get(Calendar.YEAR));
        String hour = Integer.toString(cal.get(Calendar.HOUR_OF_DAY));
        String minute = Integer.toString(cal.get(Calendar.MINUTE));
        String dateButton = day + "/" + month + "/" + year;
        // concatenate a 0 to the beginning of single digit numbers
        if (minute.length() == 1) {
            minute = "0" + minute;
        }
        String timeButton = hour + ":" + minute;
        mDate.setText(dateButton);
        mTime.setText(timeButton);
    }

    @Override
    public void onAttach(Context context) {
        Log.i("Petter", "NewTaskFragment.onAttach()");
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (NewTaskDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException("Must implement NoticeDialogListener");
        }
    }

    // Inner class to handle the time picker
    public static class TimePickerFragment extends DialogFragment
            implements android.app.TimePickerDialog.OnTimeSetListener {

        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Setting the default values to the current time
            final Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            // Create a new instance of TimePickerDialog and return it to the custom AlertDialog
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker View, int hour, int minute) {
            // TODO: Set the buttons text to this string
            String newTime = hour + ":" + minute;
        }
    }

    // Inner class to similarly handle the date picker
    public static class DatePickerFragment extends DialogFragment
            implements android.app.DatePickerDialog.OnDateSetListener {

        public interface DateListener {
            void onDateSet(DialogFragment dialog, String newDate);
        }
        DateListener listener;

        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use current date as default
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            // Create a new instance of the date picker
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onAttach(Context context) {
            Log.i("Petter", "NewTaskFragment.onAttach()");
            super.onAttach(context);
            // Verify that the host activity implements the callback interface
            try {
                // Instantiate the NoticeDialogListener so we can send events to the host
                listener = (DateListener) context;
            } catch (ClassCastException e) {
                // The activity doesn't implement the interface, throw exception
                throw new ClassCastException("Must implement NoticeDialogListener");
            }
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            // TODO: Set the buttons text to this string
            String newDate = year + "/" + month + "/" + day;
            listener.onDateSet(NewTaskFragment.DatePickerFragment.this, newDate);
        }
    }
}
