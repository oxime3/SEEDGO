package com.codemetrictech.seed_go.announcement;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.codemetrictech.seed_go.R;
import com.rachum.amir.util.range.Range;

//import org.jetbrains.annotations.NotNull;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class AnnouncementFragment extends Fragment {
    private TextView announcement_title;
    private TextView announcement_author_datetime;
    private LinearLayout announcement_body;
    private TextView body_text;
    private ListView attachment_list;

    //private String url;
    private String author;
    private String datetime;
    private String title;
    private String body;
    private HashMap<Integer, List<Object>> table_values = new HashMap<>();
    private HashMap<Integer, List<String>> files = new HashMap<>();


    //String url = "http://seed.gist-edu.cn/mod/forum/discuss.php?d=3275";
    String url = "http://seed.gist-edu.cn/mod/forum/discuss.php?d=3310";

    final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36 OPR/58.0.3135.127";
    String url_login = "http://seed.gist-edu.cn/login/index.php";
    String username = "UWI180908";
    String password = "A4034445@G!C";

    HashMap<String, String> cookies = new HashMap<>();
    HashMap<String, String> credentials = new HashMap<>();

    static Fragment newInstance() { return new AnnouncementFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_announcement, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //url = getArguments().getString("url");
        new JsoupAsyncTask().execute();
    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            announcement_title = getView().findViewById(R.id.announcement_title);
            announcement_author_datetime = getView().findViewById(R.id.announcement_author_datetime);
            announcement_body = getView().findViewById(R.id.announcement_body);
            body_text = getView().findViewById(R.id.body_text);
            attachment_list = getView().findViewById(R.id.attachment_list);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                Connection.Response loginForm = Jsoup
                        .connect(url_login)
                        .method(Connection.Method.GET)
                        .userAgent(USER_AGENT)
                        .execute();

                cookies.putAll(loginForm.cookies());
                credentials.put("username", username);
                credentials.put("password", password);

                Connection.Response homepage = Jsoup
                        .connect(url_login)
                        .cookies(cookies)
                        .data(credentials)
                        .method(Connection.Method.POST)
                        .userAgent(USER_AGENT)
                        .execute();

                cookies.clear();
                cookies.putAll(homepage.cookies());

                // Connect to the website
                Document announcement = Jsoup
                        .connect(url)
                        .cookies(cookies)
                        .get();

                // Using Elements to get the Meta data
                Elements mElementDataSize = announcement.select("div[class=topic firstpost starter]");
                // Locate the content attribute
                int mElementSize = mElementDataSize.size();

                for (int i = 0; i < mElementSize; i++) {
                    Elements mElementBlogTitle = announcement.select("div[class=subject]");
                    title = mElementBlogTitle.text().trim();
                    if (title.charAt(0) == '(')
                        title = title.replaceAll("\\(UWI\\) ","");
                    if (title.charAt(0) == '[')
                        title = title.replaceAll("\\[UWI] ","");


                    Elements mElementAuthorDateTime = announcement.select("div[class=author]");
                    String author_datetime = mElementAuthorDateTime.text();
                    String[] arrOfStr = author_datetime.split(" - ", 2);
                    author = arrOfStr[0].replaceAll("by","");
                    datetime = arrOfStr[1];

                    Elements mElementPostBody = announcement.select("div[class=posting fullpost]");

                    // Checking for a table
                    Elements mElementTable = mElementPostBody.select("table");
                    if (mElementTable.isEmpty())
                        body = mElementPostBody.text();
                    else
                        body = mElementPostBody.select("p").first().text();
                    getActivity().runOnUiThread(() -> populateTable(mElementTable));

                    // Checking for attachments
                    Elements mElementAttachments = announcement.select("div[class=attachments]");
                    if (!mElementAttachments.isEmpty()) retrieveAttachments(mElementAttachments.first());


                }

            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private void populateTable(Elements table_elements) {
            boolean hasRowSpan = false;

            TableLayout table = new TableLayout(getActivity());
            table.setLayoutParams(new TableLayout.LayoutParams());

            TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams();
            tableRowParams.setMargins(1, 1, 1, 1);
            tableRowParams.weight = 1;

            Elements table_rows = table_elements.select("tr");
            for (Element table_row : table_rows) {
                TableRow row = new TableRow(getActivity());
                row.setBackgroundColor(Color.LTGRAY);

                Elements table_cols = table_row.select("td");
                for (Element table_col : table_cols) {

                    // check for colspan attribute
                    if (table_col.hasAttr("colspan")) {
                        tableRowParams.span = Integer.valueOf(table_col.attr("colspan"));
                    }

                    // check for rowspan attribute
                    if (table_col.hasAttr("rowspan")) {
                        hasRowSpan = true;
                        int rowspan = Integer.valueOf(table_col.attr("rowspan"));
                        int i = table_rows.indexOf(table_row);
                        int j = table_cols.indexOf(table_col);
                        String text = table_col.text();

                        /*
                         * Reason for start value:
                         * rowspan would be applied to at least the next cell in the column which is 1 place down
                         *
                         * Reason for condition:
                         * We are only interested in the number of times the value should be duplicated
                         */
                        for (int x = 1; x < rowspan; x++) {
                            table_values.put(i+x, new ArrayList<>(Arrays.asList(text, j)));
                        }
                    }

                    row.addView(createCell(table_col.text(), tableRowParams));
                }

                table.addView(row);
            }

            announcement_body.addView(table, announcement_body.getChildCount());

            if (hasRowSpan) applyRowSpan(table, tableRowParams);
        }

        private TextView createCell (String text, TableRow.LayoutParams params) {
            TextView cell = new TextView(getActivity());
            cell.setText(text);
            cell.setTextIsSelectable(true);
            cell.setBackgroundColor(Color.parseColor("#FAFAFA"));
            cell.setGravity(Gravity.CENTER);
            cell.setLayoutParams(params);

            return cell;
        }

        private void applyRowSpan(TableLayout table, TableRow.LayoutParams params) {
            final int rows = table.getChildCount();

            TableRow header = (TableRow) table.getChildAt(0);
            final int cols = header.getChildCount();

            /* If rowspan should be applied, then the earliest possible point it could happen would
             *  be where the row index = 2
             */
            for (int n : new Range(2, rows)) {
                TableRow row = (TableRow) table.getChildAt(n);
                int cells = row.getChildCount();

                if (cells < cols) {
                    List<Object> object = table_values.get(n);
                    String value = (String) object.get(0);
                    int y = (int) object.get(1);
                    row.addView(createCell(value, params), y);
                }
            }
/*
            IntStream.range(2, rows).forEach(n -> {
                TableRow row = (TableRow) table.getChildAt(n);
                int cells = row.getChildCount();

                if (cells < cols) {
                    List<Object> object = table_values.get(n);
                    String value = (String) object.get(0);
                    int y = (int) object.get(1);
                    row.addView(createCell(value, params), y);
                }
            });
*/
        }

        private void retrieveAttachments(Element element) {
            ArrayList<Element> children = new ArrayList(element.children());

            int stop = children.size();
            int iterations = 0;

            for (int i : new Range(0, stop,3)) {
                Element e = children.get(i+1);

                String file_name = e.text();
                String file_src = e.attr("href");  // Gets the link

                files.put(iterations, new ArrayList<>(Arrays.asList(file_name, file_src)));
                iterations++;
            }
/*
            for (Element e : element.children().select("a:eq(1)")) {
                String file_name = e.text();
                String file_src = e.attr("href");  // Gets the link

                files.put(iterations, new ArrayList<>(Arrays.asList(file_name, file_src)));
                iterations++;
            }
*/


        }



        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            announcement_title.setText(title);

            announcement_author_datetime.setText(new StringBuilder()
                    .append("On ")
                    .append(datetime)
                    .append(", ")
                    .append(author)
                    .append(" wrote:"));

            body_text.setText(body);

            attachment_list.setAdapter(new CustomListViewAdapter(getActivity(), files, credentials));

        }

    }


}

