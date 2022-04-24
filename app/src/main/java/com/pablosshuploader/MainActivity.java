package com.pablosshuploader;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUtils;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;

import static android.provider.ContactsContract.CommonDataKinds.StructuredName.PREFIX;
import static android.provider.ContactsContract.CommonDataKinds.StructuredName.SUFFIX;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ask user permission to save database to external dir
        boolean isUserPermissionGranted = true;
        if(Build.VERSION.SDK_INT >= 23) {
            isUserPermissionGranted = ExternalStoragePermissions.verifyStoragePermissions(this);
        }
        //show required API level msg
        if(!SSHExporter.wasInitInfoShown) {
            SSHExporter.wasInitInfoShown = true;
            Toast.makeText(this, "App Req API: " + Build.VERSION_CODES.KITKAT + " MY API: " +
                    Build.VERSION.SDK_INT + "\nUser permission: " + isUserPermissionGranted, Toast.LENGTH_LONG).show();
        }


        appText = findViewById(R.id.pathText);
        //choose file
        final Button chooseButton = findViewById(R.id.chooseButton);
        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenFileChooser();
            }
        });
        //try to upload
        uploadButton = findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                uploadButton.setVisibility(View.INVISIBLE);

                //load shared prefs
                SharedPreferences sharedPref = getSharedPreferences(SSHExporter.sharedPrefsName,Context.MODE_PRIVATE);
                String host = sharedPref.getString(SSHExporter.prefHost, null);
                String servPath = sharedPref.getString(SSHExporter.prefPath,null);
                String user = sharedPref.getString(SSHExporter.prefUser,null);
                String pass = sharedPref.getString(SSHExporter.prefPass,null);

                String[] connData = new String[]{user, pass, host, servPath};

                SSHExporter.tryUploadFile(appText,MainActivity.this,connData,tempFileToUpload,nameOfFileToUpload);
            }
        });
        //set credentials of server
        final Button settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoveToSettingsMenu();
            }
        });
    }
    int _requestCode = 1;

    TextView appText;
    Button uploadButton;
    File tempFileToUpload;
    String nameOfFileToUpload;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == _requestCode && resultCode == Activity.RESULT_OK){
            if(data == null){
                uploadButton.setVisibility(View.INVISIBLE);
                return;
            }
            Uri selectedFileUri = data.getData();
            DocumentFile df = DocumentFile.fromSingleUri(this,selectedFileUri);
            nameOfFileToUpload = df.getName();
            try {
                tempFileToUpload = stream2file(getContentResolver().openInputStream(selectedFileUri));
                uploadButton.setVisibility(View.VISIBLE);
                appText.setText(selectedFileUri.getPath());
            }catch (Exception ex){
                appText.setText("ERROR: "+ex.toString());
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private File stream2file (InputStream in) throws IOException {
        final File tempFile = File.createTempFile(PREFIX,SUFFIX);
        tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(in, out);
        }
        return tempFile;
    }

    private void OpenFileChooser(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent,_requestCode);
    }

    private void MoveToSettingsMenu(){
        Intent myIntent = new Intent(this, UploadActivity.class);
        startActivity(myIntent);
        finish();
    }

}
