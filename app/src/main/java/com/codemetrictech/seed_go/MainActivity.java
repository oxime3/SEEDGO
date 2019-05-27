package com.codemetrictech.seed_go;

import android.net.Uri;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
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

public class MainActivity extends AppCompatActivity implements
        AnnouncementsFragment.OnFragmentInteractionListener,
        BarcodeReaderFragment.BarcodeReaderListener
{

    private TabsPagerAdapter mTabPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private List<Fragment> FragmentList;
    private int TotalFragments;
    FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);


        //set up tablayout
        mTabLayout = findViewById(R.id.tab_layout);
        initFragmentTabs();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mTabPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());


        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mTabPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

    }


    private void initFragmentTabs(){
        FragmentList = new ArrayList<>();
        Fragment frag1 = AnnouncementsFragment.newInstance();
//        Fragment frag2 = CoursesFragment_beta.newInstance();
        Fragment frag2 = new CoursesFragment();
        CourseContentGrabber courseContentGrabber = new CourseContentGrabber((CoursesFragment) frag2);

        FragmentList.add(frag1);
        FragmentList.add(frag2);
        TotalFragments = FragmentList.size();

        String tab1_title = ((AnnouncementsFragment)FragmentList.get(0)).getFragmentTitle();
        String tab2_title = ((CoursesFragment)FragmentList.get(1)).getTAG();
//        String tab2_title = ((CoursesFragment_beta)FragmentList.get(1)).getFragmentTitle();
        mTabLayout.addTab(mTabLayout.newTab().setText(tab1_title));
        mTabLayout.addTab(mTabLayout.newTab().setText(tab2_title));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition(), true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }


    public void switchFragment(Fragment fragment){
        this.manager.beginTransaction()
                .replace(R.id.container, fragment, "dunno")
                .addToBackStack(null)
                .commit(); }

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
            case R.id.action_settings :
                return true;

            case R.id.action_scan :
                BarcodeReaderFragment readerFragment = BarcodeReaderFragment.newInstance(true, false, View.VISIBLE);
                readerFragment.setListener(this);
                FragmentManager supportFragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_content, readerFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commitAllowingStateLoss();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
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


    public class TabsPagerAdapter extends FragmentPagerAdapter {

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return FragmentList.get(0);
                case 1:
                    return FragmentList.get(1);

                    default:
                        return null;
            }
        }

        @Override
        public int getCount() {
            return TotalFragments;
        }
    }
}
