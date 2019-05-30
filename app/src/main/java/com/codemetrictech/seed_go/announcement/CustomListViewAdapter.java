package com.codemetrictech.seed_go.announcement;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.codemetrictech.seed_go.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import static android.content.Context.DOWNLOAD_SERVICE;
import static com.codemetrictech.seed_go.LoginActivity.session;

public class CustomListViewAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList files = new ArrayList();

    CustomListViewAdapter(Context context, HashMap<Integer, List<String>> files) {
        this.context = context;
        this.files.addAll(files.entrySet());
    }

    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public Entry<Integer, List<String>> getItem(int position) {
        return (Entry) files.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View rowView;

        if (convertView == null) {
            rowView = LayoutInflater.from(context).inflate(R.layout.custom_listview, parent, false);
        } else {
            rowView = convertView;
        }

        Entry<Integer, List<String>> item = getItem(position);

        if (!this.files.isEmpty()) {
            TextView textView = rowView.findViewById(R.id.filename);
            String filename = item.getValue().get(0);
            textView.setText(filename);

            ImageView imageView = rowView.findViewById(R.id.download);
            imageView.setOnClickListener(v -> {
                if (hasStoragePermission()) {
                    String url = item.getValue().get(1);

                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                    // Manually adding the HTTP Cookie header
                    request.addRequestHeader("Cookie", session.getCookies().toString());

                    // Location i.e. where to download file in external directory
                    File file = new File(Environment.DIRECTORY_DOWNLOADS + "/" + "SEED-GO");
                    if (!file.exists()) file.mkdirs();

                    // This puts the download in the project directory
                    request.setDestinationInExternalPublicDir(String.valueOf(file), filename);

                    // Notify user when download is completed
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                    // Start download
                    DownloadManager manager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
                    manager.enqueue(request);

                }
            });
        }

        return rowView;
    }

    private boolean hasStoragePermission() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return false;
        }
    }


}
