package com.jimulabs.googlemusicmock;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionSet;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.jimulabs.googlemusicmock.transition.RevealTransition;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class AlbumDetailActivity extends Activity {

    public static final String EXTRA_ALBUM_ART_RESID = "EXTRA_ALBUM_ART_RESID";
    public static final String EXTRA_EPICENTER = "EXTRA_EPICENTER";

    @InjectView(R.id.album_art)
    ImageView albumArtView;
    @InjectView(R.id.title_container)
    View titleContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);
        ButterKnife.inject(this);
        initTransitions();
        populate();
    }

    private void initTransitions() {
        TransitionInflater inflater = TransitionInflater.from(this);
        Window window = getWindow();
        RevealTransition reveal = createRevealTransition();
        Transition otherEnterTransition = inflater.inflateTransition(R.transition.album_detail_enter);
        window.setEnterTransition(sequence(reveal, otherEnterTransition));

        Transition otherReturnTransition = inflater.inflateTransition(R.transition.album_detail_return);
        window.setReturnTransition(sequence(otherReturnTransition, reveal.clone()));

        Transition shareTransitionClone = window.getSharedElementReturnTransition().clone();
        shareTransitionClone.setStartDelay(800);
        window.setSharedElementReturnTransition(shareTransitionClone);
    }

    private TransitionSet sequence(Transition... transitions) {
        TransitionSet enterTransition = new TransitionSet();
        enterTransition.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);
        for (Transition t:transitions) {
            enterTransition.addTransition(t);
        }
        return enterTransition;
    }

    private RevealTransition createRevealTransition() {
        Point epicenter = getIntent().getParcelableExtra(EXTRA_EPICENTER);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int bigRadius = Math.max(displayMetrics.widthPixels, displayMetrics.heightPixels);
        RevealTransition reveal = new RevealTransition(epicenter, 0, bigRadius, 500);
        return reveal;
    }

    private void populate() {
        int albumArtResId = getIntent().getIntExtra(EXTRA_ALBUM_ART_RESID, 0);
        albumArtView.setImageResource(albumArtResId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_album_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
