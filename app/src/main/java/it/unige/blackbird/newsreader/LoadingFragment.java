package it.unige.blackbird.newsreader;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.Timer;
import java.util.TimerTask;

public class LoadingFragment extends Fragment {
    private Timer timer;
    private ProgressBar progressBar;

    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.loading_fragment_layout,container,false);

        this.progressBar = (ProgressBar) view.findViewById(R.id.startingProgressBar);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        startLoadingEffect();
    }

    @Override
    public void onPause() {
        stopLoadingEffect();
        super.onPause();
    }

    private void startLoadingEffect() {
        timer = new Timer();

        TimerTask loading = new TimerTask() {
            int progress = 0;
            @Override
            public void run() {
                if(progress<100)progress++;
                else progress = 0;
                progressBar.setProgress(progress);
            }
        };

        timer.schedule(loading,0,5);
    }

    private void stopLoadingEffect()
    {
        timer.cancel();
        timer = null;
    }
}
