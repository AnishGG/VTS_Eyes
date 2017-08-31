package com.android.daksh.basiclogin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static EditText username;
    private static EditText password;
    private static Button login_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LoginButton();
    }

    public void LoginButton() {
        username = (EditText) findViewById(R.id.editText_user);
        password = (EditText) findViewById(R.id.editText_password);
        login_button = (Button)findViewById(R.id.button_login);
    }

//    login_button.setOnClickListener(
//            new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            if (username.getText().toString().equals("user") &amp;amp;amp;&amp;amp;amp;
//            password.getText().toString().equals("pass")){
//                Toast.makeText(Login.this,"Username and password is correct",
//                        Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent("com.abhinavhackpundit.loginapp.User");
//                startActivity(intent);
//
//            }
//            else {
//                Toast.makeText(Login.this,"Username and password is NOT correct",
//                        Toast.LENGTH_SHORT).show();
//                attempt_counter--;
//                attempt.setText(Integer.toString(attempt_counter));
//                if(attempt_counter==0)
//                    login_button.setEnabled(false);
//            }
//        }
//    }
//        );
//}
}
