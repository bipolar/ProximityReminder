package com.ksoft.android;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.ksoft.android.activities.ReminderAdder;
import com.ksoft.android.dao.ReminderDatabase;
import com.ksoft.android.model.Reminder;

public class ReminderActivity extends Activity
{
    // TODO clean up, comments and some logs
    private ReminderDatabase dbHelper;

    private Button mAddReminderButton;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mAddReminderButton = (Button) findViewById(R.id.addReminder);
        // Register handler for UI elements
        mAddReminderButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("ReminderActivity", "mAddAccountButton clicked");
                launchReminderAdder();
            }
        });

        dbHelper = new ReminderDatabase(this);

        //Generate ListView from SQLite Database
        displayListView();
    }

    private void displayListView() {


        Cursor cursor = dbHelper.selectReminders();

        // The desired columns to be bound
        String[] columns = new String[] {
                Reminder.REMINDER_TIME,
                Reminder.REMINDER_TEXT,
                Reminder.LOCATION_ID
        };

        // the XML defined views which the data will be bound to
        int[] to = new int[] {
                R.id.date,
                R.id.reminder,
                R.id.location,
        };

        // create the adapter using the cursor pointing to the desired data
        //as well as the layout information
        SimpleCursorAdapter dataAdapter = new SimpleCursorAdapter(
                this,
                R.layout.reminderrow,
                cursor,
                columns,
                to);

        ListView listView = (ListView) findViewById(R.id.reminders);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {
                // Get the cursor, positioned to the corresponding row in the result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);

                // Get the state's capital from this row in the database.
                String reminderText =
                        cursor.getString(cursor.getColumnIndexOrThrow(Reminder.REMINDER_TEXT));
                Toast.makeText(getApplicationContext(),
                        reminderText, Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void launchReminderAdder() {
        Intent i = new Intent(this, ReminderAdder.class);

        startActivity(i);
    }
}
