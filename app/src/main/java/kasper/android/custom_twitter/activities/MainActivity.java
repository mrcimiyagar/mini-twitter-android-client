package kasper.android.custom_twitter.activities;

import android.graphics.Color;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import kasper.android.custom_twitter.R;
import kasper.android.custom_twitter.fragments.FeedFragment;
import kasper.android.custom_twitter.fragments.ProfileFragment;
import kasper.android.custom_twitter.fragments.SearchFragment;
import kasper.android.custom_twitter.fragments.TrendingFragment;

public class MainActivity extends AppCompatActivity {

    private FeedFragment feedFragment;
    private TrendingFragment trendingFragment;
    private SearchFragment searchFragment;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.feedFragment = new FeedFragment();
        this.trendingFragment = new TrendingFragment();
        this.searchFragment = new SearchFragment();
        this.profileFragment = new ProfileFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.activity_main_fragment_container,
                this.feedFragment, "feed-fragment").commit();

        this.selectView(findViewById(R.id.activity_main_feed_button));
    }

    public void onFeedBtnClicked(View view) {

        this.selectView(view);

        getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_fragment_container,
                this.feedFragment, "feed-fragment").commit();
    }

    public void onTrendingBtnClicked(View view) {

        this.selectView(view);

        getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_fragment_container,
                this.trendingFragment, "trending-fragment").commit();
    }

    public void onSearchBtnClicked(View view) {

        this.selectView(view);

        getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_fragment_container,
                this.searchFragment, "search-fragment").commit();
    }

    public void onProfileBtnClicked(View view) {

        this.selectView(view);

        getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_fragment_container,
                this.profileFragment, "profile-fragment").commit();
    }

    private void selectView(View view) {

        this.deselectAllButtons();

        DrawableCompat.setTint(((ImageButton)view).getDrawable(), getResources().getColor(R.color.colorPrimary));
    }

    private void deselectAllButtons() {

        DrawableCompat.setTint(((ImageButton) findViewById(R.id.activity_main_feed_button)).getDrawable(), Color.parseColor("#000000"));
        DrawableCompat.setTint(((ImageButton) findViewById(R.id.activity_main_trending_button)).getDrawable(), Color.parseColor("#000000"));
        DrawableCompat.setTint(((ImageButton) findViewById(R.id.activity_main_search_button)).getDrawable(), Color.parseColor("#000000"));
        DrawableCompat.setTint(((ImageButton) findViewById(R.id.activity_main_profile_button)).getDrawable(), Color.parseColor("#000000"));
    }
}