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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

public class NewTaskFragment extends DialogFragment {
    // Interface for fragment communication
    // Includes callbacks to MainActivity
    public interface NewTaskDialogListener {
        abstract void onDialogPositiveClick(DialogFragment dialog);
        abstract void onDialogNegativeClick(DialogFragment dialog);
    }

    NewTaskDialogListener listener;

    @Override
    public void onAttach(Context context) {
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

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Begin building a dialog, in the activity that called it.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate and set buttons for dialog
        // Null represents the parent view, which is none for this dialog
        // The layout file referenced is that of a customized dialog
        builder.setView(inflater.inflate(R.layout.dialog_new_task, null))
        // Adding buttons for saving or aborting
            .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    // TODO: save as an object, add to data structure
                }
            })
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    NewTaskFragment.this.getDialog().cancel();
                }
            });
        return builder.create();
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
            // TODO: something useful. What with the returning and such
            String mHour = Integer.toString(hour);
            String mMinute = Integer.toString(minute);

        }
    }

    // Inner class to similarly handle the date picker
    public static class DatePickerFragment extends DialogFragment
            implements android.app.DatePickerDialog.OnDateSetListener {

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
        public void onDateSet(DatePicker view, int year, int month, int day) {
            // TODO: Do something useful. What with the returning and such
        }
    }
}
