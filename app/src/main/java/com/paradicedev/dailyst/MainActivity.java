package com.paradicedev.dailyst;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTask(view);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // Return current tab
            switch (position) {
                case 0:
                    WeekDaysTab weekDaysTab = new WeekDaysTab();
                    return weekDaysTab;
                case 1:
                    GeneralTasksTab generalTasksTab = new GeneralTasksTab();
                    return generalTasksTab;
                default:
                    return null;
            }

        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "WEEK";
                case 1:
                    return "GENERAL";
            }
            return null;
        }
    }

    public void daySelector(View view) {

        Intent intent = new Intent(this, DailyTasksActivity.class);
        String dayLabel = new String();
        int dayOfWeek;

        switch (view.getId()) {
            case R.id.button_monday:
                dayLabel = "M  O  N  D  A  Y";
                dayOfWeek = 2;
                intent.putExtra("dayLabel",dayLabel);
                intent.putExtra("dayOfWeek",dayOfWeek);
                break;
            case R.id.button_tuesday:
                dayLabel = "T  U  E  S  D  A  Y";
                dayOfWeek = 3;
                intent.putExtra("dayLabel",dayLabel);
                intent.putExtra("dayOfWeek",dayOfWeek);
                break;
            case R.id.button_wednesday:
                dayLabel = "W  E  D  N  E  S  D  A  Y";
                dayOfWeek = 4;
                intent.putExtra("dayLabel",dayLabel);
                intent.putExtra("dayOfWeek",dayOfWeek);
                break;
            case R.id.button_thursday:
                dayLabel = "T  H  U  R  S  D  A  Y";
                dayOfWeek = 5;
                intent.putExtra("dayLabel",dayLabel);
                intent.putExtra("dayOfWeek",dayOfWeek);
                break;
            case R.id.button_friday:
                dayLabel = "F  R  I  D  A  Y";
                dayOfWeek = 6;
                intent.putExtra("dayLabel",dayLabel);
                intent.putExtra("dayOfWeek",dayOfWeek);
                break;
            case R.id.button_saturday:
                dayLabel = "S  A  T  U  R  D  A  Y";
                dayOfWeek = 7;
                intent.putExtra("dayLabel",dayLabel);
                intent.putExtra("dayOfWeek",dayOfWeek);
                break;
            case R.id.button_sunday:
                dayLabel = "S  U  N  D  A  Y";
                dayOfWeek = 1;
                intent.putExtra("dayLabel",dayLabel);
                intent.putExtra("dayOfWeek",dayOfWeek);
                break;
            default:
                break;
        }
        startActivity(intent);
    }

    public void addTask (View view) {

        Intent intent = new Intent(this, AddTaskActivity.class);
        startActivity(intent);
    }
}