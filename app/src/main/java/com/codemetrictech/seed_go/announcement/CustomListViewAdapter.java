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
import com.rachum.amir.util.range.Range;

import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.DOWNLOAD_SERVICE;

public class CustomListViewAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList files = new ArrayList();
    private final ArrayList credentials = new ArrayList();

    CustomListViewAdapter(Context context, HashMap<Integer, List<String>> files, HashMap<String, String> credentials) {
        this.context = context;
        this.files.addAll(files.entrySet());
        this.credentials.addAll(credentials.entrySet());
    }

    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public Map.Entry<Integer, List<String>> getItem(int position) {
        return (Map.Entry) files.get(position);
    }

    private Map.Entry<String, String> getCredentials(int position) {
        return (Map.Entry) credentials.get(position);
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

        Map.Entry<Integer, List<String>> item = getItem(position);

        if (!this.files.isEmpty()) {
            TextView textView = rowView.findViewById(R.id.filename);
            String filename = item.getValue().get(0);
            textView.setText(filename);

            ImageView imageView = rowView.findViewById(R.id.download);
            imageView.setOnClickListener(v -> {
                if (hasStoragePermission()) {
                    String url = item.getValue().get(1);

                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                    // Manually adding the HTTP Authorization header
                    request.addRequestHeader("Authorization", generateAuthValue());

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

    private String generateAuthValue() {
        /* https://en.wikipedia.org/wiki/Basic_access_authentication
         * basic access authentication is a method for an HTTP user agent (e.g. a web browser)
         * to provide a user name and password when making a request.
         * In basic HTTP authentication, a request contains a header field of the form
         * Authorization: Basic <credentials>, where credentials is the base64 encoding of
         * id and password joined by a single colon (:).
         */
        String username = "";
        String password = "";

        for (int i : new Range(0, 2)) {
            Map.Entry<String, String> credentials = getCredentials(i);
            if (credentials.getKey().equals("username"))
                username = credentials.getValue();
            else if (credentials.getKey().equals("password"))
                password = credentials.getValue();
        }

        String credentials = new String(new StringBuilder().append(username).append(":").append(password));
        String encodedCredentials = new String(Base64.getEncoder().encode(credentials.getBytes()));

        return new String(new StringBuilder().append("Basic ").append(encodedCredentials));
    }


}
