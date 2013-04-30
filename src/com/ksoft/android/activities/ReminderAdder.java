package com.ksoft.android.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.ksoft.android.R;
import com.ksoft.android.dao.ReminderDatabase;
import com.ksoft.android.model.Reminder;
import com.ksoft.android.model.ReminderType;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Catalin Samarghitan EMail : catalin.samarghitan@gmail.com
 * Date: 4/30/13
 * Time: 1:24 PM
 */
public class ReminderAdder extends Activity {
    public static final String TAG = "ReminderAdder";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    // TODO add time of the reminder
    // TODO add location management

    private EditText mReminderEditText;
    private EditText mReminderTypeEditText;
    private Spinner mReminderTypeSpinner;
    private EditText mReminderDateEditText;
    private Button mReminderDatePicker;

    private Date reminderDate;

    private Button mReminderSaveButton;

    private static final int DATE_DIALOG_ID = 1;

    /**
     * Called when the activity is first created. Responsible for initializing the UI.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "Activity State: onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_adder);

        // Obtain handles to UI objects
        mReminderEditText = (EditText) findViewById(R.id.reminderEditText);
        mReminderTypeEditText = (EditText) findViewById(R.id.reminderTypeEditText);
        mReminderTypeSpinner = (Spinner) findViewById(R.id.reminderTypeSpinner);
        mReminderDateEditText = (EditText) findViewById(R.id.reminderDateEditText);
        // TODO add an icon to the date picker
        mReminderDatePicker = (Button) findViewById(R.id.reminderDatePicker);
        mReminderSaveButton = (Button) findViewById(R.id.reminderSaveButton);

        // Populate list of account types for phone
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapter.add(ReminderType.LOCATION.name());
        adapter.add(ReminderType.TIME.name());
        adapter.add(ReminderType.TIME_LOCATION.name());

        mReminderTypeSpinner.setAdapter(adapter);
        mReminderTypeSpinner.setPrompt(getString(R.string.app_name));

        mReminderSaveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onSaveButtonClicked();
            }
        });

        mReminderDatePicker.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
        final Calendar c = Calendar.getInstance();
        reminderDate = c.getTime();

        updateDisplay();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this,
                        mDateSetListener,
                        reminderDate.getYear(), reminderDate.getMonth(), reminderDate.getDay());
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    reminderDate.setYear(year);
                    reminderDate.setMonth(monthOfYear);
                    reminderDate.setDate(dayOfMonth);

                    updateDisplay();
                }
            };

    private void updateDisplay() {
        mReminderDateEditText.setText(
                sdf.format(reminderDate));
    }

    /**
     * Actions for when the Save button is clicked. Creates a contact entry and terminates the
     * activity.
     */
    private void onSaveButtonClicked() {
        Log.v(TAG, "Save button clicked");
        createReminderEntry();
        finish();
    }

    /**
     * Creates a contact entry from the current UI values in the account named by mSelectedAccount.
     */
    protected void createReminderEntry() {
        // Get values from UI
        String text = mReminderEditText.getText().toString();
//        String type = mReminderTypeEditText.getText().toString();
        // TODO get the actual type selected

        Reminder reminder = new Reminder();
        reminder.setText(text);
        reminder.setType(ReminderType.TIME);
        reminder.setReminderTime(reminderDate);

        // Ask the Contact provider to create a new contact
        Log.i(TAG, "Creating reminder : " + reminder);
        try {
            // create a reminder in the db
            new ReminderDatabase(this).createReminder(reminder);
            // reset services
        } catch (Exception e) {
            // Display warning
            Context ctx = getApplicationContext();
            CharSequence txt = getString(R.string.reminderCreationFailure);
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(ctx, txt, duration);
            toast.show();

            // Log exception
            Log.e(TAG, "Exceptoin encoutered while inserting contact: " + e);
        }
    }

    /**
     * Called when this activity is about to be destroyed by the system.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
