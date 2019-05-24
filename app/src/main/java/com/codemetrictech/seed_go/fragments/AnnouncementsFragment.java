package com.codemetrictech.seed_go.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.ListView;
import android.widget.TextView;

import com.alespero.expandablecardview.ExpandableCardView;
import com.codemetrictech.seed_go.DatabaseHelper;
//import com.joestelmach.natty.*;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.Nullable;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;

import com.codemetrictech.seed_go.AnnouncementAdapter;
import com.codemetrictech.seed_go.R;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementsFragment extends Fragment {

    RecyclerView recyclerView;
    AnnouncementAdapter adapter;
    Context mContext;
    DatabaseHelper myDb;


    static List<Announcement> unreadannouncementList = new ArrayList<>();
    static List<Announcement> readannouncementList = new ArrayList<>();
    static ArrayList<Announcement> readannouncementID = new ArrayList<>();

    String url = "http://seed.gist-edu.cn/mod/forum/view.php?f=12&showall=1";
    String mBlogUploadDate;
    String mBlogTitle;
    String mBlogId;

    String anId;
    Integer anSeen;

    Integer seen = 0;
    public static Integer id = 0;


    final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36 OPR/58.0.3135.127";
    String loginFormUrl = "http://seed.gist-edu.cn/login/index.php";
    String loginActionUrl = "http://seed.gist-edu.cn/login/index.php";
    String username = "UWI180913";
    String password = "C1555480@G!C";

    HashMap<String, String> cookies = new HashMap<>();
    HashMap<String, String> formData = new HashMap<>();



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final String FragmentTitle = "ANNOUNCEMENTS";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AnnouncementsFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static AnnouncementsFragment newInstance() {
        AnnouncementsFragment fragment = new AnnouncementsFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        myDb = new DatabaseHelper(getContext());
        new JsoupAsyncTask().execute();

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_announcements_unreadvsread, container, false);


        Cursor result = myDb.getAllData();
        System.out.println("DB COUNT:" + result.getCount());
        if (result.getCount() ==  0){
            System.out.println("DB EMPTY.");
        }
        else{
            StringBuffer buffer = new StringBuffer();
            readannouncementID.clear();
            while (result.moveToNext()){
                anId = result.getString(0);
                System.out.println("DB HAS CONTENT.");
                System.out.println("ANNOUNCEMENT ID: " + anId);
                anSeen = result.getInt(1);
                System.out.println("ANNOUNCEMENT STATUS: " + anSeen);
                readannouncementID.add(new Announcement(anId,anSeen));
            }
        }

//        unreadannouncementList.add(new Announcement(
//                "Do Not Eat", "10/3/2000", 0, "."));
//
//        readannouncementList.add(new Announcement(
//                "Eat", "1/5/2000",  1, "."));


        recyclerView = (RecyclerView) view.findViewById(R.id.unreadrv);
        AnnouncementAdapter adapter = new AnnouncementAdapter(getContext(), unreadannouncementList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        //readannouncementList.add(new Announcement(mBlogTitle, mAuthorName, mBlogBody, seen, mBlogId));
        recyclerView = (RecyclerView) view.findViewById(R.id.readrv);
        AnnouncementAdapter adapter2 = new AnnouncementAdapter(getContext(), readannouncementList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter2);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public String getFragmentTitle(){
        return this.FragmentTitle;
    }


    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                System.out.println("BACKGROUND OP STARTED");
                Connection.Response loginForm = Jsoup.connect(loginFormUrl).method(Connection.Method.GET).userAgent(USER_AGENT).execute();

                cookies.putAll(loginForm.cookies());
                formData.put("username", username);
                formData.put("password", password);

                Connection.Response homepage = Jsoup.connect(loginActionUrl)
                        .cookies(cookies)
                        .data(formData)
                        .method(Connection.Method.POST)
                        .userAgent(USER_AGENT)
                        .execute();

                System.out.println("LOGGING IN");

                cookies.clear();
                cookies.putAll(homepage.cookies());


//              Get from db
//                Cursor result = myDb.getAllData();
//                if (result.getCount() ==  0){
//
//                }
//                else{
//                    StringBuffer buffer = new StringBuffer();
//                    while (result.moveToNext()){
//                        anId=result.getString(0);
//                        anSeen = result.getInt(1);
//                        readannouncementID.add(new Announcement(anId,anSeen));
//                    }
//                }

                // Create an array
                ArrayList arraylist = new ArrayList<HashMap<String, String>>();

                try {
                    // Connect to the Website URL
//                    Connection.Response mBlog = Jsoup.connect(url).cookies(cookies).method(Connection.Method.GET).userAgent(USER_AGENT).execute();
//                    System.out.println("MBLOG RESPONSE" + mBlog.parse().html());
//                    Document mBlogDocument = mBlog.parse();
                    Document doc = Jsoup.connect(url).cookies(cookies).get();
                    System.out.println("DOC VALUE" + doc);


                    // Identify Table Class "worldpopulation"
                    for (Element table : doc.select("table[class=forumheaderlist]")) {

                        // Identify all the table row's(tr)
                        for (Element row : table.select("tr:gt(0)")) {
                            HashMap<String, String> map = new HashMap<String, String>();

                            // Identify all the table cell's(td)
                            Elements tds = row.select("td");
                            String test = tds.select("td[class=topic starter]").select("a").toString();

                            // Retrive Jsoup Elements
                            // Get the first td
                            System.out.println("TEST VALUE" + tds.get(0).text());
                            if (tds.get(0).text().contains("[UWI]")) {
                                map.put("header topic", tds.get(0).text());
                                mBlogTitle = tds.get(0).text();
                                //}

                                // Get the second td
                                String test2 = tds.select("td[class=lastpost]").select("a").toString();
                                System.out.println("TEST2 VALUE" + tds.get(4).text());
                                map.put("header lastpost", tds.get(4).text());
                                mBlogUploadDate = tds.get(4).text();
                                mBlogUploadDate = mBlogUploadDate.substring(mBlogUploadDate.length() - 25);
                                System.out.println("UPLOAD DATE" + mBlogUploadDate);
                                // Get the third td
                                //no id tag from this url
                                //Elements mElementBlogId = tds.select("td[class=lastpost]").select("a[href]");
                                Elements mElementBlogId = tds.select("a[href]");
                                String link = tds.select("td[class=topic starter]").select("a").attr("href");
                                //String link = mElementBlogId.text().toString();
                                System.out.println("BLOG ID HREF" + link);
                                //gte last 6 values of string for the ID
                                mBlogId = link;
                                mBlogId = link.substring(link.length() - 4);
                                System.out.println("SHORTENED BLOG ID VALUE" + mBlogId);

                                arraylist.add(map);

                                for (int i = 0; i < readannouncementID.size(); i++) {
                                    if (readannouncementID.get(i).getId().contains(mBlogId)) {
                                        readannouncementList.add(new Announcement(mBlogTitle, mBlogUploadDate, seen, mBlogId, link));
                                    }
                                }
                                if (!readannouncementID.contains(mBlogId)) {
                                    unreadannouncementList.add(new Announcement(mBlogTitle, mBlogUploadDate, 1, mBlogId, link));
                                    boolean isInserted = myDb.insertData(mBlogId, seen);
                                    if (isInserted != true) {
                                        System.out.println("UNREAD ANNOUNCEMENT NOT ADDED TO DB");
                                    }
                                }

                                //}

                            }
                        }
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

    }
}
