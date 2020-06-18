package it.unige.blackbird.newsreader;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListNewsFragment extends Fragment implements OnItemClickListener {

    private NewsReaderApp app;
    private RSSFeed feed;
    private long feedPubDateMillis;
    private FileIO io;
    
    private TextView titleTextView;
    private ListView itemsListView;
    
    private NewFeedReceiver newFeedReceiver;
    private IntentFilter newFeedFilter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_items, container, false);

        titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        itemsListView = (ListView) view.findViewById(R.id.itemsListView);

        itemsListView.setOnItemClickListener(this);

        // get references to Application and FileIO objects
        io = new FileIO(getActivity().getApplicationContext());

        app = (NewsReaderApp) getActivity().getApplication();

        // create intent filter and receiver
        newFeedFilter = new IntentFilter(RSSFeed.ACTION_UPDATE_AVAILABLE);
        newFeedReceiver = new NewFeedReceiver();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        
        // get feed from app object
        feedPubDateMillis = app.getFeedMillis();
        
        if (feed == null) {
            new ReadFeed().execute();       // read and display
        }
        else {
            updateDisplay();                // just display
        }
    }
    
    @Override
    public void onPause() {
        super.onPause();
    }
    
    class ReadFeed extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            feed = io.readFile(); 
            return null;
        }
        
        @Override
        protected void onPostExecute(Void result) {
            Log.d("News reader", "Feed read");
            app.setFeedMillis(feed.getPubDateMillis());
            ListNewsFragment.this.updateDisplay();
        }
    }
    
    public void updateDisplay()
    {
        if (feed == null) {
            titleTextView.setText("Unable to get RSS feed");
            return;
        }

        // set the title for the feed
        titleTextView.setText(feed.getTitle());
        
        // get the items for the feed
        List<RSSItem> items = feed.getAllItems();

        // create a List of Map<String, ?> objects
        ArrayList<HashMap<String, String>> data = 
                new ArrayList<HashMap<String, String>>();
        for (RSSItem item : items) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("date", item.getPubDateFormatted());
            map.put("title", item.getTitle());
            data.add(map);
        }
        
        // create the resource, from, and to variables 
        int resource = R.layout.listview_item;
        String[] from = {"date", "title"};
        int[] to = { R.id.pubDateTextView, R.id.titleTextView};

        // create and set the adapter
        SimpleAdapter adapter = 
                new SimpleAdapter(getActivity(), data, resource, from, to);
        itemsListView.setAdapter(adapter);
        
        Log.d("News reader", "Feed displayed");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, 
            int position, long id) {
        RSSItem item = feed.getItem(position);
        
        Intent intent = new Intent(getActivity().getApplicationContext(), ItemActivity.class);
        intent.putExtra("pubdate", item.getPubDate());
        intent.putExtra("title", item.getTitle());
        intent.putExtra("description", item.getDescription());
        intent.putExtra("link", item.getLink());

        this.startActivity(intent);
    }
    
    class NewFeedReceiver extends BroadcastReceiver {
        
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("News reader", "New items broadcast received");
            
            String test = intent.getStringExtra("test");
            Log.d("News reader", "test: " + test);
            
            updateDisplay();
        }
    }
}