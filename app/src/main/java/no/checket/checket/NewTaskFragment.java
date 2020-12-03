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

public class NewTaskFragment extends DialogFragment {

    // Interface for fragment communication
    // Includes callbacks to MainActivity
    public interface NewTaskDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, String header, String details, long date, String icon, Boolean completed);
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
        final DatePicker mDate = (DatePicker) view.findViewById(R.id.date_input);
        final TimePicker mTime = (TimePicker) view.findViewById(R.id.time_input);
        mTime.setIs24HourView(true);

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
                    int month = mDate.getMonth();
                    int day = mDate.getDayOfMonth();
                    int year = mDate.getYear();
                    int hour = mTime.getCurrentHour()-1;
                    int minute = mTime.getCurrentMinute()-1;
                    Calendar date = Calendar.getInstance();
                    date.set(year, month, day, hour, minute);
                    // The naming convention of the icons is ic_CATEGORY
                    String icon = "R.id.ic_" + header;
                    listener.onDialogPositiveClick(NewTaskFragment.this, header, details, date.getTimeInMillis(), icon, false);
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
}
