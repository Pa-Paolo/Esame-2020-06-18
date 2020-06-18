package it.unige.blackbird.newsreader;

import android.app.Application;
import android.preference.PreferenceManager;
import android.util.Log;

public class NewsReaderApp extends Application {


    private long feedMillis = -1;
    
    public void setFeedMillis(long feedMillis) {
        this.feedMillis = feedMillis;
    }
    
    public long getFeedMillis() {
        return feedMillis;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        PreferenceManager.setDefaultValues(this, R.xml.app_preference, false);
        Log.d("News reader", "App started");
    }
}