package com.codemetrictech.seed_go;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codemetrictech.seed_go.utility.InputValidator;
import com.codemetrictech.seed_go.utility.preferences;
import com.codemetrictech.seed_go.utility.preferences.PrefController;
import com.google.android.material.snackbar.Snackbar;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;


public class LoginActivity extends Activity {
    private Session session;
    private EditText username;
    private EditText password;
    private CheckBox checkBox;
    private Button btn_login;
    private ProgressBar progressBar;
    private Boolean password_is_valid = false;
    private boolean username_is_valid = false;
    private boolean is_signing_in = false;
    private boolean network_is_down = false;
    private PrefController prefController = preferences.PrefController;

    private final String TAG = "--- LOGIN";
    final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36 OPR/58.0.3135.127";
    String loginFormUrl = "http://seed.gist-edu.cn/login/index.php";
    String loginActionUrl = "http://seed.gist-edu.cn/login/index.php";

    HashMap<String, String> cookies = new HashMap<>();
    HashMap<String, String> formData = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        initWidgets();
        checkUserPreferences();

    }

    private void checkUserPreferences(){
        if(prefController.getRememberMe(getApplicationContext()) == true){
            String pref_username = prefController.getUserName(getApplicationContext());
            username.setText(pref_username);

            checkBox.setChecked(true);
        }
    }

    public void setUserPreferences(){
        if(checkBox.isChecked()){
            prefController.setUserName(getApplicationContext(), username.getText().toString());
            username.clearFocus();
            password.requestFocus();
        } else {
            prefController.setUserName(getApplicationContext(), "");
        }
    }

    private void initWidgets() {
        session = new Session();
        progressBar = findViewById(R.id.progress_bar);

        checkBox = findViewById(R.id.remember_me_checkbox);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                prefController.setRememberMe(getApplicationContext(), true );
            } else {
                prefController.setRememberMe(getApplicationContext(),false );
            }
        });



        username = findViewById(R.id.username);
        username.setOnFocusChangeListener((view, focused) -> {
            InputMethodManager keyboard = (InputMethodManager) LoginActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (focused)
                keyboard.showSoftInput(username, 0);
            else
                keyboard.hideSoftInputFromWindow(username.getWindowToken(), 0);
            String inputName = username.getText().toString();
            if(!inputName.isEmpty()){
                username_is_valid = InputValidator.Companion.validateUsername(inputName);
                if(!username_is_valid){
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
            if(!pass.isEmpty()){
                password_is_valid = InputValidator.Companion.validatePassword(pass);
                if (!password_is_valid){
                    password.setError("Invalid Password.");
                }
            }

        });

        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(view -> {
            btn_login.setClickable(false);
            InputMethodManager keyboard = (InputMethodManager) LoginActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            password.clearFocus();
            btn_login.requestFocus();
            keyboard.hideSoftInputFromWindow(btn_login.getWindowToken(), 0);

            if (password_is_valid && username_is_valid){
                is_signing_in = true;
                network_is_down = false;
                updateUI();
                new Login().execute();
            } else {
                Snackbar.make(view, "Please check your form.", Snackbar.LENGTH_SHORT).show();
                if(!username_is_valid){
                    username.requestFocus();
                } else{
                    password.requestFocus();
                }
                updateUI();
            }
        });
    }

    public void updateUI(){
        if(is_signing_in){
            btn_login.setClickable(false);
            btn_login.setText("Please wait...");
            progressBar.setVisibility(View.VISIBLE);
        } else {
            btn_login.setClickable(true);
            btn_login.setText("LOGIN");
            progressBar.setVisibility(View.INVISIBLE);
        }

        Log.d(TAG, "updateUI");

    }


    private class Login extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            getCurrentFocus();
        }



        @Override
        protected Void doInBackground(Void... voids) {
            if (true){
                try {
                    Connection.Response loginForm = Jsoup.connect(loginFormUrl).method(Connection.Method.GET).userAgent(USER_AGENT).execute();

                    cookies.putAll(loginForm.cookies());

                    runOnUiThread(() ->
                            formData.put("username", username.getText().toString()));

                    runOnUiThread(() ->
                            formData.put("password", password.getText().toString()));

                    Connection.Response homePage = Jsoup.connect(loginActionUrl)
                            .cookies(cookies)
                            .data(formData)
                            .method(Connection.Method.POST)
                            .userAgent(USER_AGENT)
                            .execute();

                    Document document = homePage.parse();


                    if (!document.title().contains("Login to the site")) {
                        //runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Logged in Successfully", Toast.LENGTH_LONG).show());
//                        session.setLoggedin(true);
//                        runOnUiThread(()->
//                        session.setSavedUsername(username.getText().toString()));

                        is_signing_in = false;
                        updateUI();
                        goToHomePage();
                        Log.d(TAG, "SEED AUTH SUCCESS: ");
                    }
                    else {
                        runOnUiThread(() -> Toast
                                .makeText(LoginActivity.this, getString(R.string.erroneous_password_error_msg), Toast.LENGTH_LONG)
                                .show());
                        Log.d(TAG, "SEED AUTH FAIL");
                    }
                }
                catch (IOException ioe) {
                    is_signing_in = false;
                    updateUI();

                    Snackbar.make(findViewById(R.id.login_linear_layout), "Connection Error.", Snackbar.LENGTH_SHORT).show();
                    Log.d(TAG, ioe.getMessage());
                }
            } else {
                Log.d(TAG, "Session already exists.");

                is_signing_in = false;
                updateUI();
                goToHomePage();
                Log.d(TAG, "SEED AUTH SUCCESS: ");
            }
            return null;
        }

        private void goToHomePage(){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            finish();
            startActivity(intent);

            Log.d(TAG, "Go to homepage");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            setUserPreferences();
            super.onPostExecute(aVoid);
        }


    }
}
