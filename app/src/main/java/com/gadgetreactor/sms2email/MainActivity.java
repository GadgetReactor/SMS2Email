package com.gadgetreactor.sms2email;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.IntentFilter;
import android.widget.Toast;
import android.content.SharedPreferences;

// TODO: Add Switch to Receiver On/Off Toggle
// TODO: Change Fragment to Mail Server Settings (username, password, SMTP server, port)
// TODO: Add email counter, log, and data usage to settings
// TODO: Single page navigation; checklist boxes; final button switch

public class MainActivity extends ActionBarActivity {

    SMSReceiver broadCastReceiver = new SMSReceiver();
    boolean receiverOn = false;

    public static final String PREFS_NAME = "MyPrefsFile";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * sections. We use a {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will
     * keep every loaded fragment in memory. If this becomes too memory intensive, it may be best
     * to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }





    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override

        public Fragment getItem(int i) {
            switch(i){
                case 0:
                    Fragment1 fragment1 = new Fragment1();
                    return fragment1;

                case 1:
                    Fragment2 fragment2 = new Fragment2();
                    return fragment2;

                case 2:
                    Fragment fragment3 = new Fragment3();
                    return fragment3;

            }
            return null;
        }


        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return getString(R.string.title_section1).toUpperCase();
                case 1: return getString(R.string.title_section2).toUpperCase();
                case 2: return getString(R.string.title_section3).toUpperCase();
            }
            return null;
        }
    }

    /**
     * A dummy fragment representing a section of the app, but that simply displays dummy text.
     */
    public static class Fragment3 extends Fragment {
        public Fragment3(){
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.email_recipient, container, false);

            String savedfwdemail = LoadPreferences (getActivity(), "fwdemail", "");
            String saveddispname = LoadPreferences (getActivity(), "dispname", "");
            String saveddispemail = LoadPreferences (getActivity(), "dispemail", "");
            TextView fwdemail = (TextView) view.findViewById(R.id.fwdemail);
            TextView dispname = (TextView) view.findViewById(R.id.dispname);
            TextView dispmail = (TextView) view.findViewById(R.id.dispemail);
            fwdemail.setText(savedfwdemail);
            dispname.setText(saveddispname);
            dispmail.setText(saveddispemail);
            return view;
        }

        private String LoadPreferences(Context context, String key, String value){
            SharedPreferences sharedPreferences = context.getSharedPreferences("Default", MODE_PRIVATE);
            String response = sharedPreferences.getString(key, value);
            return response;
        }
    }

    public class Fragment1 extends Fragment {
        public Fragment1(){
        }
        SMSReceiver broadCastReceiver = new SMSReceiver();
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.main, container, false);
            final Button testButton = (Button) view.findViewById(R.id.button2);
            if(receiverOn) {
                testButton.setText("Stop");
            } else {
                testButton.setText("Start");
            }
            return view;
        }
    }

    public static class Fragment2 extends Fragment {
        public Fragment2(){
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.settings_main, container, false);

            String saveduser = LoadPreferences (getActivity(), "Username", "");
            String savedpassword = LoadPreferences (getActivity(), "Password", "");
            TextView username = (TextView) view.findViewById(R.id.userentry);
            TextView password = (TextView) view.findViewById(R.id.pwentry);
            username.setText(saveduser);
            password.setText(savedpassword);
            return view;
        }

        private String LoadPreferences(Context context, String key, String value){
            SharedPreferences sharedPreferences = context.getSharedPreferences("Default", MODE_PRIVATE);
            String response = sharedPreferences.getString(key, value);
            return response;
        }

    }

    public void onClick2(View view) {
        final Button testButton = (Button) findViewById(R.id.button2);
        if(!receiverOn) {
            this.registerReceiver(broadCastReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
            receiverOn = true;
            testButton.setText("Stop");
        } else {
            this.unregisterReceiver(broadCastReceiver);
            Toast.makeText(this, "unregistered", Toast.LENGTH_SHORT).show();
            receiverOn = false;
            testButton.setText("Start");
        }
    }

    public void onClick3(View view) {

        final EditText fwdemail1 = (EditText) findViewById(R.id.fwdemail);
        final EditText dispname1 = (EditText) findViewById(R.id.dispname);
        final EditText dispmail1 = (EditText) findViewById(R.id.dispemail);

        String fwdemailt = fwdemail1.getText().toString();
        String dispnamet = dispname1.getText().toString();
        String dispmailt = dispmail1.getText().toString();

        SavePreferences("fwdemail", fwdemailt);
        SavePreferences("dispname", dispnamet);
        SavePreferences("dispemail", dispmailt);
    }

    public void onClick1(View view) {

        final EditText pwEntry = (EditText) findViewById(R.id.pwentry);
        final EditText unEntry = (EditText) findViewById(R.id.userentry);

        String username = unEntry.getText().toString();
        String passwd = pwEntry.getText().toString();

        SavePreferences("Username", username);
        SavePreferences("Password", passwd);
    }


    public void SavePreferences(String key, String value){
        SharedPreferences sharedPreferences = getSharedPreferences("Default", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

}
