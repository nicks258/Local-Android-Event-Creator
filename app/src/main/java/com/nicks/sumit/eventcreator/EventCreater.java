package com.nicks.sumit.eventcreator;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.nicks.sumit.eventcreator.DbHelper.DatabaseHelper;
import com.nicks.sumit.eventcreator.adapter.SpinnerListAdapter;
import com.nicks.sumit.eventcreator.data.FeedItem;
import com.orhanobut.logger.Logger;
import com.spark.submitbutton.SubmitButton;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import gr.escsoft.michaelprimez.searchablespinner.SearchableSpinner;
import gr.escsoft.michaelprimez.searchablespinner.interfaces.IStatusListener;
import gr.escsoft.michaelprimez.searchablespinner.interfaces.OnItemSelectedListener;

public class EventCreater extends AppCompatActivity {
    EditText title, description;
    ImageView preview;
    DatabaseHelper dbHelper;
    private SearchableSpinner mSearchableSpinner;
    SubmitButton submit;
    Button datePicker;
    private SpinnerListAdapter mSimpleListAdapter;
    FloatingActionButton imageSelector;
    private String category = "";
    private byte[] byteArray;
    private static final String TAG_DATETIME_FRAGMENT = "TAG_DATETIME_FRAGMENT";
    String dateSelected="";
    private static final String STATE_TEXTVIEW = "STATE_TEXTVIEW";


    private SwitchDateTimeDialogFragment dateTimeFragment;
    private final ArrayList<String> mStrings = new ArrayList<>();
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_creater);
        dbHelper = new DatabaseHelper(this);
        initListValues();
        title = (EditText) findViewById(R.id.titleEt);
        description = (EditText) findViewById(R.id.descriptionEt);
        datePicker = (Button) findViewById(R.id.date_picker);
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTimeFragment.show(getSupportFragmentManager(), TAG_DATETIME_FRAGMENT);
            }
        });
        mSimpleListAdapter = new SpinnerListAdapter(this, mStrings);
        mSearchableSpinner = (SearchableSpinner) findViewById(R.id.SearchableSpinner);
        mSearchableSpinner.setAdapter(mSimpleListAdapter);
        mSearchableSpinner.setOnItemSelectedListener(mOnItemSelectedListener);
        mSearchableSpinner.setStatusListener(new IStatusListener() {
            @Override
            public void spinnerIsOpening() {
                View view = EventCreater.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }

            @Override
            public void spinnerIsClosing() {

            }
        });

        preview = (ImageView) findViewById(R.id.preview);
        submit = (SubmitButton) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onSubmit();
                    }
                }, 3400);

            }
        });
        imageSelector = (FloatingActionButton) findViewById(R.id.image_selector);
        imageSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sdintent = new Intent(Intent.ACTION_PICK);
                sdintent.setType("image/*");
                startActivityForResult(sdintent, 0);
            }
        });

//        DATE and TIME
        dateTimeFragment = (SwitchDateTimeDialogFragment) getSupportFragmentManager().findFragmentByTag(TAG_DATETIME_FRAGMENT);
        if(dateTimeFragment == null) {
            dateTimeFragment = SwitchDateTimeDialogFragment.newInstance(
                    getString(R.string.label_datetime_dialog),
                    getString(R.string.positive_button_datetime_picker),
                    getString(R.string.negative_button_datetime_picker)
            );
        }

        // Assign values we want
        final SimpleDateFormat myDateFormat = new SimpleDateFormat("d MMM yyyy HH:mm", java.util.Locale.getDefault());
        dateTimeFragment.startAtCalendarView();
        dateTimeFragment.set24HoursMode(false);
        dateTimeFragment.setMinimumDateTime(new GregorianCalendar(2015, Calendar.JANUARY, 1).getTime());
        dateTimeFragment.setMaximumDateTime(new GregorianCalendar(2025, Calendar.DECEMBER, 31).getTime());
        dateTimeFragment.setDefaultDateTime(new GregorianCalendar(2017, Calendar.MARCH, 4, 15, 20).getTime());
        // Or assign each element, default element is the current moment
        // dateTimeFragment.setDefaultHourOfDay(15);
        // dateTimeFragment.setDefaultMinute(20);
        // dateTimeFragment.setDefaultDay(4);
        // dateTimeFragment.setDefaultMonth(Calendar.MARCH);
        // dateTimeFragment.setDefaultYear(2017);

        // Define new day and month format
        try {
            dateTimeFragment.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("MMMM dd", Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
            Log.e("EVENT", e.getMessage());
        }

        // Set listener for date
        dateTimeFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {
               dateSelected = myDateFormat.format(date);
               Logger.i("Date->"+myDateFormat.format(date));
            }

            @Override
            public void onNegativeButtonClick(Date date) {
//                textView.setText("");
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);

            preview.setImageBitmap(yourSelectedImage);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            yourSelectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byteArray = stream.toByteArray();

            Logger.i("Its Awesome");
        }
    }
    private void onSubmit(){
        FeedItem feedItem = new FeedItem();
        feedItem.setEventName(title.getText().toString());
        feedItem.setEventDescription(description.getText().toString());
        Logger.i("Imagepath->" + filePath);
        feedItem.setImagePath(filePath);
        feedItem.setCategory(category);
        feedItem.setTimeStamp(dateSelected);
        dbHelper.addEvent(feedItem);
        Intent mainActivityIntent = new Intent(EventCreater.this,MainActivity.class);
        startActivity(mainActivityIntent);
        EventCreater.this.finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mSearchableSpinner.isInsideSearchEditText(event)) {
            mSearchableSpinner.hideEdit();
        }


        return super.onTouchEvent(event);
    }


    private OnItemSelectedListener mOnItemSelectedListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(View view, int position, long id) {
            category = mSimpleListAdapter.getItem(position).toString();
//            Logger.i("Category->" + category);
            Toast.makeText(EventCreater.this, "Item on position " + position + " : " + mSimpleListAdapter.getItem(position) + " Selected", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected() {
            Toast.makeText(EventCreater.this, "Nothing Selected", Toast.LENGTH_SHORT).show();
        }
    };

    private void initListValues() {
        mStrings.add("Conferences");
        mStrings.add("Seminars");
        mStrings.add("Meetings");
        mStrings.add("Team Building Events");
        mStrings.add("Trade Shows");
        mStrings.add("Business Dinners");
        mStrings.add("Golf Events ");
        mStrings.add("Press Conferences");
        mStrings.add("Networking Events");
        mStrings.add("Incentive Travel");
        mStrings.add("Opening Ceremonies");
        mStrings.add("Product Launches");
        mStrings.add("Theme Parties ");
        mStrings.add("VIP Events");
        mStrings.add("Trade Fairs");
        mStrings.add("Shareholder Meetings");
        mStrings.add("Award Ceremonies");
        mStrings.add("Incentive Events");
        mStrings.add("Board Meetings");
        mStrings.add("Executive Retreats");
        mStrings.add("Weddings");
        mStrings.add("Birthdays");
        mStrings.add("Wedding Anniversaries");
        mStrings.add("Family Events");
        mStrings.add("Executive Retreats");
        mStrings.add("Incentive Programs");
        mStrings.add("Appreciation Events");
        mStrings.add("Organization Milestones");
        mStrings.add("Company Milestones");
        mStrings.add("Talks & Discussions");
        mStrings.add("Social");
        mStrings.add("HansMorford");
        mStrings.add("Sumit Mehra");
        mStrings.add("Thomasina Naron");
        mStrings.add("Melba Massi");
        mStrings.add("Sal Mangano");
        mStrings.add("Mika Weitzel");
        mStrings.add("Phylis France");
    }
}
