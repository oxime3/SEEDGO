package com.codemetrictech.seed_go;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.codemetrictech.seed_go.utils.*;
import com.codemetrictech.seed_go.utils.Preferences.PrefController;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.HashMap;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class LoginActivity extends Activity {
    private EditText username;
    private EditText password;
    private CheckBox checkBox;
    private ProgressBar progressBar;
    private Button btn_login;

    private boolean isValidUsername = true;
    private Boolean isValidPassword = true;
    private boolean isSigningIn;
    public static Session session;
    private PrefController prefController = Preferences.PrefController;

    private final String TAG = "--- LOGIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        initWidgets();
        checkUserPreferences();

    }

    private void initWidgets() {
        username = findViewById(R.id.username);
        username.setOnFocusChangeListener((view, focused) -> {
            InputMethodManager keyboard = (InputMethodManager) LoginActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (focused)
                keyboard.showSoftInput(username, 0);
            else
                keyboard.hideSoftInputFromWindow(username.getWindowToken(), 0);

            String inputName = username.getText().toString();
            if (!inputName.isEmpty()) {
                isValidUsername = InputValidator.Companion.validateUsername(inputName);
                if (!isValidUsername) {
                    username.setError("Invalid username.");
                }
            }
        });

        password = findViewById(R.id.password);
        password.setOnFocusChangeListener((view, focused) -> {
            InputMethodManager keyboard = (InputMethodManager) LoginActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (focused)
                keyboard.showSoftInput(password, 0);
            else
                keyboard.hideSoftInputFromWindow(password.getWindowToken(), 0);

            String pass = password.getText().toString();
            if (!pass.isEmpty()) {
                isValidPassword = InputValidator.Companion.validatePassword(pass);
                if (!isValidPassword) {
                    password.setError("Invalid Password.");
                }
            }

        });

        checkBox = findViewById(R.id.cb_remember_me);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                prefController.setRememberMe(getApplicationContext(), true );
            else
                prefController.setRememberMe(getApplicationContext(),false );
        });

        progressBar = findViewById(R.id.progress_circular);

        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(view -> {
            btn_login.setClickable(false);
            password.clearFocus();
            btn_login.requestFocus();

            InputMethodManager keyboard = (InputMethodManager) LoginActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.hideSoftInputFromWindow(btn_login.getWindowToken(), 0);

            if (isValidUsername && isValidPassword) {
                isSigningIn = true;
                updateUI();
                new Login().execute();
            } else {
                Snackbar.make(view, "Please check your form.", Snackbar.LENGTH_LONG)
                        .setDuration(3000)
                        .show();

                if (!isValidUsername)
                    username.requestFocus();
                else
                    password.requestFocus();

                updateUI();
            }
        });
    }

    private void checkUserPreferences() {
        if (prefController.getRememberMe(getApplicationContext())) {
            String pref_username = prefController.getUserName(getApplicationContext());
            username.setText(pref_username);
            checkBox.setChecked(true);
        }
    }

    public void setUserPreferences() {
        if(checkBox.isChecked()){
            prefController.setUserName(getApplicationContext(), username.getText().toString());
            username.clearFocus();
            password.requestFocus();
        } else {
            prefController.setUserName(getApplicationContext(), "");
        }
    }

    public void updateUI() {
        if (isSigningIn) {
            btn_login.setClickable(false);
            btn_login.setText("PLEASE WAIT...");
            progressBar.setVisibility(View.VISIBLE);
        } else {
            btn_login.setClickable(true);
            btn_login.setText("LOGIN");
            progressBar.setVisibility(View.GONE);
        }

    }

    private class Login extends AsyncTask<Void, Void, Void> {
        private String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36 OPR/58.0.3135.127";
        private String url_login = "http://seed.gist-edu.cn/login/index.php";

        private HashMap<String, String> cookies = new HashMap<>();
        private HashMap<String, String> credentials = new HashMap<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            session = new Session();
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

                credentials.put("username", username.getText().toString());
                credentials.put("password", password.getText().toString());

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
                    Snackbar.make(findViewById(R.id.form_login), "Logged in Successfully", Snackbar.LENGTH_LONG)
                            .setDuration(3000)
                            .show();

                    isSigningIn = false;
                    session.setCookies(cookies);
                }
                else {
                    Snackbar.make(findViewById(R.id.form_login), getString(R.string.erroneous_password_error_msg), Snackbar.LENGTH_LONG)
                            .setDuration(3000)
                            .show();
                }
            }
            catch (IOException ioe) {
                isSigningIn = false;

                Snackbar.make(findViewById(R.id.form_login), "Connection Error.", Snackbar.LENGTH_LONG)
                        .setDuration(3000)
                        .show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateUI();
            setUserPreferences();

            if (!session.getCookies().isEmpty()) {
                finish();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        }


    }
}