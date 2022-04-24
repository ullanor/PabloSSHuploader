package com.pablosshuploader;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class SSHExporter {
    public static String sharedPrefsName = "SFTP_CONN",prefHost = "HOST",prefPath="PATH",prefUser="USER",prefPass="PASS";
    public static boolean wasInitInfoShown = false;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void tryUploadFile(final TextView expInfo, final Activity activity, final String[] connData, final File tempFile, final String fileName) {
        expInfo.setText("Trying to export file...");

        new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... params) {
                String infoText;
                try {
                    executeRemoteCommand(connData[0], connData[1], connData[2], 22,connData[3], tempFile, fileName);
                    infoText ="File uploaded!";
                } catch (Exception e) {
                    infoText = "ERROR: "+e.toString();
                }
                final String finalInfoText = infoText;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        expInfo.setText(finalInfoText);
                    }
                });
                return null;
            }
        }.execute(1);
    }

    private static void executeRemoteCommand(final String username, final String password, final String hostname, final int port, final String path, final File temp, final String fileName) throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, hostname, port);
        session.setPassword(password);

        // Avoid asking for key confirmation
        Properties prop = new Properties();
        prop.put("StrictHostKeyChecking", "no");
        session.setConfig(prop);

        //connection timeout
        session.setTimeout(3000);
        session.connect();

        //SFTP setup
        Channel channel = session.openChannel("sftp");
        channel.connect();

        ChannelSftp channelsftp = (ChannelSftp) channel;

        channelsftp.cd(path);
        channelsftp.put(new FileInputStream(temp), fileName);

        channel.disconnect();
    }
}

