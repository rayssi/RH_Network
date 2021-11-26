package com.a.rh_wifi;

import android.os.AsyncTask;


class LongRunningTask extends AsyncTask<Void, Boolean, Boolean> {


    protected void onPreExecute() {

    }

    @Override
    protected Boolean doInBackground(Void... params) {


        return true;
    }

    protected void onPostExecute(Boolean result) {
        if(result) {

        }
    }

}