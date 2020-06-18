package it.unige.blackbird.newsreader;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class SiteSelectorFragment extends Fragment implements View.OnClickListener {

    FileIO io;

    private Button dontChangeButt;
    private Button changeButt;
    private EditText changeSiteEditText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.site_selector_layout,container, false);

        io = new FileIO(getActivity().getApplicationContext());

        dontChangeButt = view.findViewById(R.id.dontChangeButton);
        changeButt = view.findViewById(R.id.changeSiteButton);
        changeSiteEditText = view.findViewById(R.id.siteEditText);

        dontChangeButt.setOnClickListener(this);
        changeButt.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.dontChangeButton:
                getFragmentManager().beginTransaction().replace(R.id.itemsFragmentHolder, new ListNewsFragment()).commit();
                break;
            case R.id.changeSiteButton:
                String newSite = changeSiteEditText.getText().toString();
                io.setDefaultSite(newSite.isEmpty()?FileIO.URL_STRING:newSite);
                new DownloadFeed().execute();
        }
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
            getFragmentManager().beginTransaction().replace(R.id.itemsFragmentHolder, new ListNewsFragment()).commit();
        }
    }
}
