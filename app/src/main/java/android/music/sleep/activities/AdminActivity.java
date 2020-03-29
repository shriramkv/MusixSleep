package android.music.sleep.activities;

import android.content.Intent;
import android.music.sleep.R;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {
    boolean doubleBackToExitPressedOnce = false;
    Button upload;
    EditText username, password;
    private String getUsername, getPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        upload = findViewById(R.id.uploadBtn);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUsername = username.getText().toString();
                getPassword = password.getText().toString();

                if (getUsername.equals(" ") || getUsername.length() == 0
                        || getPassword.equals(" ") || getPassword.length() == 0) {
                    Toast.makeText(AdminActivity.this,"Enter all credentials",Toast.LENGTH_LONG).show();
                } else if (getUsername.equals("aakash1956ragu@gmail.com") && getPassword.equals("123")){
                    startActivity(new Intent(AdminActivity.this,UploadActivity.class));
                } else {
                    Toast.makeText(AdminActivity.this,"Invalid username/password",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
