package com.lenovo.mat.emmcstresstest;

import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.Random;


@EActivity(R.layout.activity_emmc_stess_test)
public class EmmcStessTest extends AppCompatActivity {
    String TAG="EmmcStessTest";
    @ViewById
    TextView text_info;

    long totalWrite=0;
    long startTime=0;

    Random ra =new Random();

    @AfterViews
    void afterViews()
    {
        long totalWrite=0;
        startTime=System.currentTimeMillis();
        updateTextBackground();
        background_write();
    }

    @UiThread
    void updateText(){
        long time=(System.currentTimeMillis()-startTime)/1000;
        float speed=(float)totalWrite/1024/1024/time;

        if(text_info.isShown()) {
            text_info.setText("Time:" + time + "S\n\nTotalWrite:" + totalWrite / 1024 / 1024 + "MB\n\nAvgSpeed:" + speed + " MB/S");
        }
    }

    @Background(delay = 5000)
    void updateTextBackground(){
        updateText();
        updateTextBackground();
    }

    @Background(delay = 500)
    void background_write(){

        int size=0;
        while (size<100) {
             size = ra.nextInt(5 * 1024 * 1024);
        }

        File dir=new File(this.getFilesDir(),"ss_out");

        if(dir.exists() && dir.getFreeSpace()< 1024*1024*1024){
            deleteDir(dir);
        }
        if(!dir.exists()){
            dir.mkdir();
        }

        File f= new File(dir,"ss"+size);
        Log.d(TAG,"Writing "+size+" to "+f.getAbsolutePath());

        try {
            f.deleteOnExit();
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f);
            Random r = new Random();
            byte buff[]=new byte[size];
            r.nextBytes( buff);
            fos.write(buff);
            fos.close();
            this.totalWrite+=size;
        }catch (Exception e){
            Log.e(TAG, "error",e);
        }

        background_write();
    }



    public  boolean deleteDir(File dir) {
        Log.d(TAG, "deleteing "+dir);
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir
                        (new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();

    }
}
