package com.codemetrictech.seed_go;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.codemetrictech.seed_go.courses.CourseContentGrabber;
import com.codemetrictech.seed_go.fragments.AnnouncementsFragment;
import com.codemetrictech.seed_go.fragments.CoursesFragment;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.tabs.TabLayout;
import com.notbytes.barcode_reader.BarcodeReaderFragment;

import java.util.ArrayList;
import java.util.List;

import static com.codemetrictech.seed_go.LoginActivity.session;

public class MainActivity extends AppCompatActivity implements
        AnnouncementsFragment.OnFragmentInteractionListener,
        BarcodeReaderFragment.BarcodeReaderListener {

    private TabsPagerAdapter mTabPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private List<Fragment> FragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //set up tablayout
        mTabLayout = findViewById(R.id.tab_layout);
        initFragmentTabs();

        // Create the adapter that will return a fragment_host for each of the three
        // primary sections of the activity.
        mTabPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());


        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mTabPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

    }


    private void initFragmentTabs(){
        FragmentList = new ArrayList<>();

        FragmentList.add(AnnouncementsFragment.newInstance());
        FragmentList.add(new CoursesFragment());
        CourseContentGrabber courseContentGrabber = new CourseContentGrabber(this, (CoursesFragment)FragmentList.get(1));

        String tab1_title = ((AnnouncementsFragment)FragmentList.get(0)).getFragmentTitle();
        String tab2_title = ((CoursesFragment)FragmentList.get(1)).getTAG();

        getSupportFragmentManager().beginTransaction().addToBackStack("Announcements Fragment").commit();

        mTabLayout.addTab(mTabLayout.newTab().setText(tab1_title));
        mTabLayout.addTab(mTabLayout.newTab().setText(tab2_title));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition(), true);
                mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

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

        switch (id) {
            case R.id.action_scan :
                BarcodeReaderFragment readerFragment = BarcodeReaderFragment.newInstance(true, false, View.VISIBLE);
                readerFragment.setListener(this);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_content, readerFragment, "Barcode Reader Fragment");
                fragmentTransaction.addToBackStack("Barcode Reader Fragment");
                fragmentTransaction.commitAllowingStateLoss();
                break;

            case R.id.action_logout:
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Logout Confirmation")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            session.deleteSession();
                            Intent intent = new Intent(this, LoginActivity.class);
                            startActivityForResult(intent, 200);
                            finish();
                        })
                        .setNegativeButton(android.R.string.no, (dialog, which) -> {
                            // do nothing
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();

        if (backStackEntryCount == 0) {
            moveTaskToBack(false);

        } else if (getCurrentFragment(backStackEntryCount).equals("Announcement Fragment")) {
            getSupportFragmentManager().popBackStackImmediate("Announcements Fragment", 0);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.unreadfirst, AnnouncementsFragment.newInstance(), "Announcements Fragment")
                    .commit();

        } else if (getCurrentFragment(backStackEntryCount).equals("Announcements Fragment")) {
            moveTaskToBack(false);
        }
/*
        } else {
            getSupportFragmentManager().popBackStackImmediate();
        }
*/
    }

    public String getCurrentFragment(int count) {
        return getSupportFragmentManager().getBackStackEntryAt(count - 1).getName();
    }

    public class TabsPagerAdapter extends FragmentPagerAdapter {

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return FragmentList.get(position);
        }

        @Override
        public int getCount() {
            return FragmentList.size();
        }
    }


    /**
     * A placeholder fragment_host containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment_host argument representing the section number for this
         * fragment_host.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment_host for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }


    /**********************************************************************************************
     * BARCODE READER LISTENER METHODS                                                            *
     **********************************************************************************************/
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onScanned(Barcode barcode) {

    }

    @Override
    public void onScannedMultiple(List<Barcode> barcodes) {

    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onScanError(String errorMessage) {

    }

    @Override
    public void onCameraPermissionDenied() {

    }

}
