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
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.jimulabs.googlemusicmock.transition.Fold;
import com.jimulabs.googlemusicmock.transition.RevealTransition;
import com.jimulabs.googlemusicmock.transition.Scale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;


public class AlbumDetailActivity extends Activity {

    public static final String EXTRA_ALBUM_ART_RESID = "EXTRA_ALBUM_ART_RESID";
    public static final String EXTRA_EPICENTER = "EXTRA_EPICENTER";

    @InjectView(R.id.album_art)
    ImageView albumArtView;
    @InjectViews({R.id.fab, R.id.title_container, R.id.title, R.id.subtitle, R.id.info_container})
    View[] viewsToAnimate;
    @InjectView(R.id.fab)
    ImageButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);
        ButterKnife.inject(this);
        initTransitions();
        setViewsVisibility(false);
//        toggleTransitViews();
        populate();
    }

    @OnClick(R.id.album_art)
    public void onAlbumArtClicked() {
        toggleTransitViews();
    }

    private void toggleTransitViews() {
        final ViewGroup root = (ViewGroup) getWindow().getDecorView();
        TransitionInflater inflater = TransitionInflater.from(this);
        boolean toShow = viewsToAnimate[0].getVisibility() == View.INVISIBLE;
        Transition transition = inflater.inflateTransition(
                toShow ? R.transition.album_detail_enter : R.transition.album_detail_return);
        getContentTransitionManager().beginDelayedTransition(root, transition);
        setViewsVisibility(toShow);
    }

    private void setViewsVisibility(boolean toShow) {
        for (View v : viewsToAnimate) {
            v.setVisibility(toShow ? View.VISIBLE : View.INVISIBLE);
        }
        float scale = toShow ? 1 : 0;
        fab.setScaleX(scale);
        fab.setScaleY(scale);
    }

    private void initTransitions() {
        TransitionInflater inflater = TransitionInflater.from(this);
        Window window = getWindow();
        RevealTransition reveal = createRevealTransition();
        window.setEnterTransition(reveal);

        Transition otherReturnTransition = inflater.inflateTransition(R.transition.album_detail_return);
        window.setReturnTransition(sequence(otherReturnTransition, reveal.clone()));

        Transition shareTransitionClone = window.getSharedElementReturnTransition().clone();
//        shareTransitionClone.setStartDelay(800);
        window.setSharedElementReturnTransition(shareTransitionClone);
    }

    private TransitionSet sequence(Transition... transitions) {
        TransitionSet enterTransition = new TransitionSet();
        enterTransition.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);
        for (Transition t : transitions) {
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
