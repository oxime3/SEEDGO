package com.codemetrictech.seed_go.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import android.app.Activity;
import android.os.AsyncTask;
import com.codemetrictech.seed_go.DatabaseHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.Nullable;

import com.codemetrictech.seed_go.AnnouncementAdapter;
import com.codemetrictech.seed_go.MainActivity;
import com.codemetrictech.seed_go.R;
import com.codemetrictech.seed_go.announcement.AnnouncementFragment;

import java.util.ArrayList;

import static com.codemetrictech.seed_go.DatabaseHelper.col_1;
import static com.codemetrictech.seed_go.LoginActivity.session;


public class AnnouncementsFragment extends Fragment {
    private Activity activity;

    RecyclerView unread;
    RecyclerView read;
    AnnouncementAdapter adapter;
    DatabaseHelper myDb;

    ArrayList<Announcement> allAnnouncements = new ArrayList<>();
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
        return new AnnouncementsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        myDb = new DatabaseHelper(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_announcements_unreadvsread, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //initDatabase();
        readannouncementList.clear();
        unreadannouncementList.clear();
        initWidgets(view);
        new JsoupAsyncTask().execute();
    }

    private void initDatabase() {
        Cursor result = myDb.getAllData();
        System.out.println("DB COUNT ON START:" + result.getCount());
        if (result.getCount() ==  0){
            System.out.println("DB EMPTY.");
        }
        else{
            StringBuffer buffer = new StringBuffer();
            readannouncementID.clear();
            readannouncementList.clear();
            unreadannouncementList.clear();
            while (result.moveToNext()){
                anId = result.getString(0);
                System.out.println("DB HAS CONTENT.");
                System.out.println("ANNOUNCEMENT ID: " + anId);
                //readannouncementID.add(new Announcement(anId));
            }
        }
    }

    private void initWidgets(View view) {
        unread = (RecyclerView) view.findViewById(R.id.unreadrv);
        unread.setLayoutManager(new LinearLayoutManager(getContext()));

        //readannouncementList.add(new Announcement(mBlogTitle, mAuthorName, mBlogBody, seen, mBlogId));
        read = (RecyclerView) view.findViewById(R.id.readrv);
        read.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
/*
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
*/

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity)
            activity = (Activity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void viewAnnouncement(String url) {
        LinearLayout layout = getView().findViewById(R.id.unreadfirst);
        layout.removeAllViews();

        Bundle bundle = new Bundle();
        bundle.putString("url", url);

        Fragment fragment = AnnouncementFragment.newInstance();
        fragment.setArguments(bundle);

        String tag = "Announcement Fragment";

        ((MainActivity) activity).getSupportFragmentManager().beginTransaction()
                .replace(R.id.unreadfirst, fragment, tag)
                .addToBackStack(tag)
                .commit();
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

            // Create an array
            ArrayList arraylist = new ArrayList<HashMap<String, String>>();

            try {
                // Connect to the Website URL
                Document doc = Jsoup
                        .connect(url)
                        .cookies(session.getCookies())
                        .get();


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
                            String link = tds.select("td[class=topic starter]").select("a").attr("href");
                            System.out.println("BLOG ID HREF" + link);
                            //get last 6 values of string for the ID
                            mBlogId = link;
                            mBlogId = link.substring(link.length() - 4);
                            System.out.println("SHORTENED BLOG ID VALUE" + mBlogId);
                            System.out.println(link);

                            arraylist.add(map);
                            allAnnouncements.add(new Announcement(mBlogTitle, mBlogUploadDate, mBlogId, link));

                        }
                    }
                }
                System.out.println(allAnnouncements.size());
                allAnnouncements.forEach(System.out::println);
                sortAnnouncements();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            int i = 0;
            System.out.println("UNREAD ANNOUNCEMENTS: " + unreadannouncementList);
            System.out.println("READ ANNOUNCEMENTS: " + readannouncementList);
            if(readannouncementList.size() > 0){
                System.out.println("READ ANNOUNCEMENT ID: " + readannouncementList.get(i).getId());
            }

            return null;
        }

        private void sortAnnouncements() {
            Cursor result = myDb.getAllData();
            System.out.println("DB COUNT:" + result.getCount());
            if (result.getCount() ==  0){
                System.out.println("DB EMPTY.");
                unreadannouncementList.addAll(allAnnouncements);
                //System.out.println("UNREAD ANNOUNCEMENT: " + mBlogId);
                boolean isInserted = myDb.insertData(mBlogId);
                if (isInserted != true) {
                    System.out.println("UNREAD ANNOUNCEMENT NOT ADDED TO DB");
                }
            }
            else {
                for (Announcement announcement: allAnnouncements) {
                    String id = announcement.getId();

                    if (recordExists(result, id))
                        readannouncementList.add(announcement);
                    else
                        unreadannouncementList.add(announcement);
                }
            }
        }

        public boolean recordExists(Cursor cursor, String id) {
            boolean status = true;
            while (cursor.moveToNext()){
                String seen_id = cursor.getString(cursor.getColumnIndex(col_1));

                if (id.equals(seen_id))
                    status = true;
                return status;
            }
            status = false;

            return status;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            AnnouncementAdapter adapter = new AnnouncementAdapter(getContext(), AnnouncementsFragment.this, unreadannouncementList);
            unread.setAdapter(adapter);

            AnnouncementAdapter adapter2 = new AnnouncementAdapter(getContext(), AnnouncementsFragment.this, readannouncementList);
            read.setAdapter(adapter2);

        }


    }
    
    public void announcementsServer(){
        //convert lists into JSON string
        Gson gson = new Gson();
        String dataArrayRead = gson.toJson(readannouncementList);
        String dataArrayUnread = gson.toJson(unreadannouncementList);

        //send arrays to server using volley
        final String server_url= "http://127.0.0.1:5000";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        final String result = response.toString();
                        System.out.println("response: " + result);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        error.getMessage();
                    }
                })
        {
            @Override
            protected Map<String,String> getParams() throws AuthFailureError{
                Map<String,String> param = new HashMap<String,String>();
                param.put("readAnnouncements", dataArrayRead); //readAnnouncements is key for server side
                param.put("unreadAnnouncements", dataArrayUnread); //unreadAnnouncements is key for server side

                return param;
            }
        };
        VolleyConnection.getInstance(App.getContext()).addRequestQue(stringRequest);

    }
}
