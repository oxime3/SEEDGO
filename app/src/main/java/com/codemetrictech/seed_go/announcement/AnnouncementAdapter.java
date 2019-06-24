package com.codemetrictech.seed_go.announcement;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.codemetrictech.seed_go.DatabaseHelper;
import com.codemetrictech.seed_go.R;
import com.codemetrictech.seed_go.fragments.Announcement;
import com.codemetrictech.seed_go.fragments.AnnouncementsFragment;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

import static com.codemetrictech.seed_go.DatabaseHelper.col_1;

public class AnnouncementAdapter extends RecyclerView.Adapter <AnnouncementAdapter.AnnouncementViewHolder>{

    private Context mContext;
    private List<Announcement> announcementList;
    private DatabaseHelper dbhelper;
    private AnnouncementsFragment fragment_host;

    public static Integer id = 0;
    String status = "";

    public AnnouncementAdapter(Context mContext, AnnouncementsFragment fragment, List<Announcement> announcementList) {
        this.mContext = mContext;
        this.fragment_host = fragment;
        this.announcementList = announcementList;

    }


    @Override
    public AnnouncementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.announcement_preview, null);
        AnnouncementViewHolder holder = new AnnouncementViewHolder(view);
        dbhelper = new DatabaseHelper(mContext);

        return new AnnouncementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AnnouncementViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        Announcement announcement = announcementList.get(position);

        holder.post_title.setText((announcement.getPost_title()));
        holder.post_date.setText((announcement.getPost_date()));
        holder.readmore_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                //add announcement's id and seen status to database.
                System.out.println("ADAPTER: READ ANNOUNCEMENT ID: " + announcement.getId());
                Cursor result = dbhelper.getAllData();
                System.out.println("DB COUNT IN ADAPTER:" + result.getCount());
                boolean seen = true;
                while (result.moveToNext()){
                    String seen_id = result.getString(result.getColumnIndex(col_1));
                    System.out.println("LOOP THROUGH DB VALUE: " + seen_id);
                    if (announcement.getId().equals(seen_id)) {
                        //don't add
                        System.out.println("LOOP THROUGH VALUE ALREADY IN DB: " + seen_id);
                    }else
                        seen = false;
                }
                if (seen = false) {
                    boolean isInserted = dbhelper.insertData(announcement.getId());
                    if (isInserted != true) {
                        System.out.println("ADAPTER: READ ANNOUNCEMENT NOT ADDED TO DB" + announcement.getId());
                    }
                }

                fragment_host.viewAnnouncement(announcement.getLink());
            }
        });


    }


    class AnnouncementViewHolder extends RecyclerView.ViewHolder{

        TextView post_title, post_date;
        Button readmore_button;

        public AnnouncementViewHolder(View itemView) {
            super(itemView);

            post_title = itemView.findViewById(R.id.post_title);
            post_date = itemView.findViewById(R.id.post_date);
            readmore_button = itemView.findViewById(R.id.readmore_button);
        }
    }

    @Override
    public int getItemCount() {
        return announcementList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public void refreshUnread(List<Announcement> announcementList) {
        this.announcementList.clear();
        this.announcementList.addAll(announcementList);
        notifyDataSetChanged();
    }


}
