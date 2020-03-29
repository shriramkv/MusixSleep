package android.music.sleep.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.music.sleep.R;
import android.music.sleep.model.BaseActivity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity {

    private CheckBox remember_me;
    private EditText name, age;
    private Button start, admin;
    private TextView welcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initViews();

        SharedPreferences sharedPreferences =
                getSharedPreferences("checkbox", MODE_PRIVATE);
        String r_check = sharedPreferences.getString("remember", "");

        if (r_check.equals("true")) {
            remember_me.setChecked(true);
            welcome.setText("Welcome Back, "+sharedPreferences.getString("name", ""));
            name.setText(sharedPreferences.getString("name", ""));
            age.setText(sharedPreferences.getString("age", ""));
        }

        if (!isConnected()) {
            AlertDialog alertDialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Please switch on your internet connection")
                    .setCancelable(true);
            alertDialog = builder.create();
            alertDialog.setTitle("Connectivity Issue");
            alertDialog.show();
        }

        remember_me.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (buttonView.isChecked()) {
                            SharedPreferences sharedPreferences =
                                    getSharedPreferences("checkbox", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("remember", "true");
                            editor.putString("name", name.getText().toString());
                            editor.putString("age", age.getText().toString());
                            editor.apply();
                        } else if (!buttonView.isChecked()) {
                            SharedPreferences sharedPreferences =
                                    getSharedPreferences("checkbox", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("remember", "false");
                            editor.apply();
                        }
                    }
                });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getName = name.getText().toString();
                String getAge = age.getText().toString();
                if (getName.equals(" ") || getName.length() == 0
                        || getAge.equals(" ") || getAge.length() == 0) {
                    Toast.makeText(MainActivity.this, "Enter the credentials", Toast.LENGTH_LONG).show();
                } else if (Integer.parseInt(getAge) > 150) {
                    Toast.makeText(MainActivity.this, "Enter proper age value", Toast.LENGTH_LONG).show();
                } else {
                    startActivity(new Intent(MainActivity.this, StartActivity.class));
                }
            }
        });

        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AdminActivity.class));
                finish();
            }
        });
    }

    private void initViews() {
        welcome = findViewById(R.id.welcome_text);
        remember_me = findViewById(R.id.remember_me);
        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        start = findViewById(R.id.startBtn);
        admin = findViewById(R.id.adminBtn);
    }

    public boolean isConnected() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
