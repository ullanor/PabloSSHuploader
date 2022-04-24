package com.pablosshuploader;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UploadActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        //load shared prefs
        SharedPreferences sharedPref = getSharedPreferences(SSHExporter.sharedPrefsName,Context.MODE_PRIVATE);
        String host = sharedPref.getString(SSHExporter.prefHost, null);
        String path = sharedPref.getString(SSHExporter.prefPath,null);
        String user = sharedPref.getString(SSHExporter.prefUser,null);
        String pass = sharedPref.getString(SSHExporter.prefPass,null);

        //set EditTexts
        final EditText IPaddressText = findViewById(R.id.inputHost_FE);
        IPaddressText.setText(host);
        final EditText pathText = findViewById(R.id.inputPath_FE);
        pathText.setText(path);
        final EditText usernameText = findViewById(R.id.inputUser_FE);
        usernameText.setText(user);
        final EditText passwordText = findViewById(R.id.inputPass_FE);
        passwordText.setText(pass);

        //save BUTTON --- +++ ---
        final Button saveButton = findViewById(R.id.btnSave_FE);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getSharedPreferences(SSHExporter.sharedPrefsName,Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(SSHExporter.prefHost,IPaddressText.getText().toString());
                editor.putString(SSHExporter.prefPath,pathText.getText().toString());
                editor.putString(SSHExporter.prefUser,usernameText.getText().toString());
                editor.putString(SSHExporter.prefPass,passwordText.getText().toString());
                editor.apply();

                Toast.makeText(UploadActivity.this,"Prefs saved!",Toast.LENGTH_SHORT).show();
                MoveBack();
            }
        });

        //BACK BUTTON --- +++ ---
        final Button backButton = findViewById(R.id.btnBACK_FE);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoveBack();
            }
        });
    }

    private void MoveBack(){
        Intent myIntent = new Intent(this, MainActivity.class);
        startActivity(myIntent);
        finish();
    }
}
