package it.unige.blackbird.newsreader;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;

public class FileIO {

    private SharedPreferences preferences;
    public static final String URL_STRING = "https://www.gazzetta.it/rss/home.xml";
    private final String FILENAME = "news_feed.xml";
    private Context context = null;
    
    public FileIO (Context context) {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
    
    public void downloadFile() {
        try{
            // get the URL
            String rawUrl = preferences.getString("site",URL_STRING);
            URL url = new URL(rawUrl);

            // get the input stream
            InputStream in = url.openStream();
            
            // get the output stream
            FileOutputStream out = 
                context.openFileOutput(FILENAME, Context.MODE_PRIVATE);

            // read input and write output
            byte[] buffer = new byte[1024];
            int bytesRead = in.read(buffer);
            while (bytesRead != -1)
            {
                out.write(buffer, 0, bytesRead);
                bytesRead = in.read(buffer);
            }
            out.close();
            in.close();
        } 
        catch (IOException e) {
            Log.e("News reader", e.toString());
        }
    }
    
    public RSSFeed readFile() {
        try {
            // get the XML reader
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader xmlreader = parser.getXMLReader();

            // set content handler
            RSSFeedHandler theRssHandler = new RSSFeedHandler();
            xmlreader.setContentHandler(theRssHandler);

            // read the file from internal storage
            FileInputStream in = context.openFileInput(FILENAME);

            // parse the data
            InputSource is = new InputSource(in);
            xmlreader.parse(is);

            // set the feed in the activity
            RSSFeed feed = theRssHandler.getFeed();
            return feed;
        } 
        catch (Exception e) {
            Log.e("News reader", e.toString());
            return null;
        }
    }

    public void setDefaultSite(String string)
    {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("site",string);
        editor.commit();
    }
}