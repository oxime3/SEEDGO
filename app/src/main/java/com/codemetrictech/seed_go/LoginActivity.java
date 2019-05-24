package com.codemetrictech.seed_go;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class LoginActivity extends Activity {
    private Session session;
    private EditText username;
    private EditText password;
    private Button btn_login;

    final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36 OPR/58.0.3135.127";
    String url_login = "http://seed.gist-edu.cn/login/index.php";

    HashMap<String, String> cookies = new HashMap<>();
    HashMap<String, String> credentials = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initWidgets();
    }

    private void initWidgets() {
        session = new Session(LoginActivity.this);

        username = findViewById(R.id.username);
        username.setOnFocusChangeListener((view, focused) -> {
            InputMethodManager keyboard = (InputMethodManager) LoginActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (focused)
                keyboard.showSoftInput(username, 0);
            else
                keyboard.hideSoftInputFromWindow(username.getWindowToken(), 0);
        });

        password = findViewById(R.id.password);
        password.setOnFocusChangeListener((view, focused) -> {
            InputMethodManager keyboard = (InputMethodManager) LoginActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (focused)
                keyboard.showSoftInput(password, 0);
            else
                keyboard.hideSoftInputFromWindow(password.getWindowToken(), 0);
        });

        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(view -> new Login().execute());

    }

    private class Login extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            getCurrentFocus();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Connection.Response loginForm = Jsoup
                        .connect(url_login)
                        .method(Connection.Method.GET)
                        .userAgent(USER_AGENT)
                        .execute();


                cookies.putAll(loginForm.cookies());

                runOnUiThread(() -> credentials.put("username", username.getText().toString()));
                runOnUiThread(() -> credentials.put("password", password.getText().toString()));


                Connection.Response homepage = Jsoup
                        .connect(url_login)
                        .cookies(cookies)
                        .data(credentials)
                        .method(Connection.Method.POST)
                        .userAgent(USER_AGENT)
                        .execute();

                cookies.clear();
                cookies.putAll(homepage.cookies());

                Document document = homepage.parse();

                if (!document.title().contains("Login to the site")) {
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Logged in Successfully", Toast.LENGTH_LONG).show());
                    session.setLoggedin(true);
                    runOnUiThread(() -> session.setSavedUsername(username.getText().toString()));

                }
                else {
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Username or Password incorrect", Toast.LENGTH_LONG).show());
                }
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
