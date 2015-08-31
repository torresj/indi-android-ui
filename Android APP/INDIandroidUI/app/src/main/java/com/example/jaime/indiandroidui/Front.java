package com.example.jaime.indiandroidui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;


public class Front extends AppCompatActivity {

    ProgressBar progress;
    static boolean init=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front);

        progress = (ProgressBar) findViewById(R.id.progressBar);

    }

    @Override public void onResume(){
        super.onResume();
        if(init) {
            new UpdateProgress().execute();
            init=false;
        }else{
            init=true;
            finish();
        }
    }

    @Override
    public void onBackPressed(){

    }


    class UpdateProgress extends AsyncTask<Void, Integer, Void> {

        @Override protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            for(int i=0;i<100;i+=5){
                SystemClock.sleep(400);
                publishProgress(i);
            }

            return null;
        }

        @Override protected void onProgressUpdate(Integer... prog) {

            Integer i=prog[0];
            progress.setProgress(i);

        }

        @Override protected void onPostExecute(Void result) {
            Intent i = new Intent(Front.this, MainActivity.class);
            startActivity(i);
        }
    }
}
