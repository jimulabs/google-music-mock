package com.jimulabs.googlemusicmock;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
import android.widget.TextView;

import com.jimulabs.googlemusicmock.transition.RevealTransition;

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
    @InjectView(R.id.title_container)
    ViewGroup titleContainer;
    @InjectView(R.id.info_container)
    ViewGroup infoContainer;
    @InjectView(R.id.title)
    TextView title;
    @InjectView(R.id.subtitle)
    TextView subtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);
        ButterKnife.inject(this);
        initTransitions();
        populate();
        setViewsVisibility(false);
    }

    @OnClick(R.id.album_art)
    public void onAlbumArtClicked() {
        toggleTransitViews(null);
    }

    private void toggleTransitViews(Transition.TransitionListener listener) {
        final ViewGroup root = (ViewGroup) getWindow().getDecorView();
        TransitionInflater inflater = TransitionInflater.from(this);
        boolean toShow = viewsToAnimate[0].getVisibility() == View.INVISIBLE;
        Transition transition = inflater.inflateTransition(
                toShow ? R.transition.album_detail_enter : R.transition.album_detail_return);
        if (listener!=null) {
            transition.addListener(listener);
        }
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
        reveal.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                playOtherEnterAnimations();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
        window.setEnterTransition(reveal);
    }

    class SetVisibleAnimatorListener implements Animator.AnimatorListener {
        private final View target;

        SetVisibleAnimatorListener(View target) {
            this.target = target;
        }
        @Override
        public void onAnimationStart(Animator animation) {
            target.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    };

    private void playOtherEnterAnimations() {
        Animator scaleX = ObjectAnimator.ofFloat(fab, "scaleX", 0, 1);
        Animator scaleY = ObjectAnimator.ofFloat(fab, "scaleY", 0, 1);
        Animator scaleFab = together(scaleX, scaleY);
        scaleFab.setTarget(fab);
        scaleFab.addListener(new SetVisibleAnimatorListener(fab));

        Animator unfoldTitleContainer = createUnfoldAnimator(titleContainer);
        Animator unfoldInfoContainer = createUnfoldAnimator(infoContainer);

        Animator fadeInTitle = createFadeInAnimator(title);
        Animator fadeInSubtitle = createFadeInAnimator(subtitle);

        Animator transition = together(sequence(together(unfoldTitleContainer, fadeInTitle, fadeInSubtitle),
                unfoldInfoContainer), scaleFab);

        transition.start();
    }

    private Animator createFadeInAnimator(View view) {
        Animator animator = ObjectAnimator.ofFloat(view, "alpha", 0, 1);
        animator.addListener(new SetVisibleAnimatorListener(view));
        return animator;
    }

    private AnimatorSet sequence(Animator... animators) {
        AnimatorSet set = new AnimatorSet();
        set.playSequentially(animators);
        return set;
    }

    private Animator createUnfoldAnimator(ViewGroup view) {
        ObjectAnimator animator = ObjectAnimator.ofInt(view, "bottom", view.getTop(),
                view.getTop() + view.getHeight());
        animator.addListener(new SetVisibleAnimatorListener(view));
        return animator;
    }

    private Animator together(Animator... animators) {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(animators);
        return set;
    }

    @Override
    public void finishAfterTransition() {
        toggleTransitViews(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                superFinishAfterTransition();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
    }

    private void superFinishAfterTransition() {
        super.finishAfterTransition();
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
        reveal.addTarget(R.id.detail_container);
        reveal.addTarget(android.R.id.statusBarBackground);
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
