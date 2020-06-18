package it.unige.blackbird.newsreader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class ItemsActivity extends Activity {

    private NewsReaderApp app;
    private long feedPubDateMillis;
    private FileIO io;
    private NewFeedReceiver newFeedReceiver;


    private IntentFilter newFeedFilter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.items_main_holder_layout);

        // get references to Application and FileIO objects
        app = (NewsReaderApp) getApplication();
        io = new FileIO(getApplicationContext());

        getFragmentManager().beginTransaction().add(R.id.itemsFragmentHolder,new LoadingFragment()).commit();

        // create intent filter and receiver
        newFeedFilter = new IntentFilter(RSSFeed.ACTION_UPDATE_AVAILABLE);
        newFeedReceiver = new NewFeedReceiver();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
        // get feed from app object
        feedPubDateMillis = app.getFeedMillis();
        
        if (feedPubDateMillis == -1) {
            new DownloadFeed().execute();   // download, read, and display
        } else getFragmentManager().beginTransaction().replace(R.id.itemsFragmentHolder, new SiteSelectorFragment()).commit();

        // register receiver for filter
        registerReceiver(newFeedReceiver, newFeedFilter);
    }
    
    @Override
    public void onPause() {
        unregisterReceiver(newFeedReceiver);
        super.onPause();
    }
    
    class DownloadFeed extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            io.downloadFile();
            return null;
        }
        
        @Override
        protected void onPostExecute(Void result) {
            Log.d("News reader", "Feed downloaded");
            getFragmentManager().beginTransaction().replace(R.id.itemsFragmentHolder, new SiteSelectorFragment()).commit();
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_items, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                new DownloadFeed().execute();
                Toast.makeText(this, "Feed refreshed!", 
                        Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class NewFeedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("News reader", "New items broadcast received");

            String test = intent.getStringExtra("test");
            Log.d("News reader", "test: " + test);

            //updateDisplay();
        }
    }
}