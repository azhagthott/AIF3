package cl.exec.dev.android.aif.connection;

import android.os.AsyncTask;

/**
 * Created by fran on 04-05-15.
 */
public class GetDataAsyncTask extends AsyncTask<String, Void, String[]> {

    private final String LOG_TAG = GetDataAsyncTask.class.getSimpleName();
    private static final String URL_RESTFUL_API = "http://localhost:3000/";

    @Override
    protected String[] doInBackground(String... params) {

        ServicesHandler servicesHandler = new ServicesHandler();
        servicesHandler.makeServiceCall(URL_RESTFUL_API, 2);
        String response = servicesHandler.makeServiceCall(URL_RESTFUL_API, 2);

        return new String[0];
    }

    protected void onPostExecute(String[] result) {

    }

}
