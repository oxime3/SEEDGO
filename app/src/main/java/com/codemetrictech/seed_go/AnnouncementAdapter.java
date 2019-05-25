package com.codemetrictech.seed_go;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.codemetrictech.seed_go.announcement.AnnouncementFragment;
import com.codemetrictech.seed_go.fragments.Announcement;

import java.util.HashMap;
import java.util.List;

//import com.joestelmach.natty.*;

public class AnnouncementAdapter extends RecyclerView.Adapter <AnnouncementAdapter.AnnouncementViewHolder>{

    private Context mContext;
    private Announcement announcement;
    private List<Announcement> announcementList;
    MainActivity host_activity;

    String url = "http://seed.gist-edu.cn/mod/forum/view.php?f=12&showall=1";

    public static Integer id = 0;
    String status = "";

    final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36 OPR/58.0.3135.127";
    String loginFormUrl = "http://seed.gist-edu.cn/login/index.php";
    String loginActionUrl = "http://seed.gist-edu.cn/login/index.php";
    String username = "UWI180913";
    String password = "C1555480@G!C";

    HashMap<String, String> cookies = new HashMap<>();
    HashMap<String, String> formData = new HashMap<>();

    public AnnouncementAdapter(Context mContext, List<Announcement> announcementList) {
        this.mContext = mContext;
        this.announcementList = announcementList;
    }



    @Override
    public AnnouncementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.announcement_preview, null);
        AnnouncementViewHolder holder = new AnnouncementViewHolder(view);

        return new AnnouncementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AnnouncementViewHolder holder, int position) {
        Announcement announcement = announcementList.get(position);

        holder.post_title.setText((announcement.getPost_title()));
        holder.post_date.setText((announcement.getPost_date()));
        holder.readmore_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Fragment fragment = new AnnouncementFragment();
                Bundle bundle = new Bundle();
                bundle.putString("url", announcement.getLink());
                fragment.setArguments(bundle);
                host_activity.switchFragment(fragment);
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


}
