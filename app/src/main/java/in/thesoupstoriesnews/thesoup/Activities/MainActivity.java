package in.thesoupstoriesnews.thesoup.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.w3c.dom.Text;

import in.thesoupstoriesnews.thesoup.Adapters.FeedFragmentPagerAdapter;
import in.thesoupstoriesnews.thesoup.App.Config;
import in.thesoupstoriesnews.thesoup.Fragments.DiscoverFragment;
import in.thesoupstoriesnews.thesoup.Fragments.MyFeedFragment;
import in.thesoupstoriesnews.thesoup.PreferencesFbAuth.PrefUtil;
import in.thesoupstoriesnews.thesoup.R;
import in.thesoupstoriesnews.thesoup.SoupContract;
import me.toptas.fancyshowcase.FancyShowCaseView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.R.id.text1;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, MyFeedFragment.Badgeonfilter {

    private ViewPager viewPager;
    private FeedFragmentPagerAdapter adapter;
    private ImageView mimageView, tickImage;
    private TextView mTextView;
    private LinearLayout mlinearlayout;
    private SharedPreferences pref;
    private TabLayout tabLayout;
    private FirebaseAnalytics mFirebaseAnalytics;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

         Window window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
         window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
          window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);


            window.setStatusBarColor(Color.parseColor("#222222"));
        }


        pref = PreferenceManager.getDefaultSharedPreferences(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // View decorView = getWindow().getDecorView();
// Hide the status bar.
        //int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        //decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_main);

        String fragmentPosition = "";

        Intent startingIntent = getIntent();

        if (startingIntent != null) {
            fragmentPosition = startingIntent.getStringExtra("fragmentPosition");
        }


        PrefUtil prefUtil = new PrefUtil(this);

        prefUtil.saveTotalRefresh("0");

        // Toolbar toolbar = (Toolbar)findViewById(R.id.toolbarfilter);
        //setSupportActionBar(toolbar);


        mTextView = (TextView) findViewById(R.id.filter);
        //mimageView = (ImageView)findViewById(R.id.filter_img);
        tickImage = (ImageView) findViewById(R.id.filter_tick);
        tickImage.setVisibility(View.GONE);
        // mimageView.setOnClickListener(this);
        mTextView.setOnClickListener(this);
        mlinearlayout = (LinearLayout) findViewById(R.id.filtertab);
        mlinearlayout.setOnClickListener(this);

        int filter_count = 0;

        if (pref.getString("count", null) != null && !pref.getString("count", null).isEmpty()) {
            int count = Integer.valueOf(pref.getString("count", null));
            Log.d("filter_count", ':' + String.valueOf(count));

            if (pref.getString("filter_count", null) != null && !pref.getString("filter_count", null).isEmpty()) {
                filter_count = Integer.valueOf(pref.getString("filter_count", null));
            }
            //  filter_count = getIntent().getIntExtra("filter_count", 0);
            Log.d("filtercount", String.valueOf(filter_count));
            if (filter_count > 0 && filter_count < count) {

                tickImage.setVisibility(View.VISIBLE);
            }

        }

        viewPager = (ViewPager) findViewById(R.id.viewpager);


        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/montserrat-light.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );


        // Create an adapter that knows which fragment should be shown on each page
        adapter = new FeedFragmentPagerAdapter(getSupportFragmentManager(), this);


        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < adapter.getCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(R.layout.tab_text_new);
                ImageView imageView = (ImageView) tab.getCustomView().findViewById(R.id.globe1);
                ImageView imageView1 = (ImageView)tab.getCustomView().findViewById(R.id.notificationbell);
                imageView.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);



                TextView textView = (TextView) tab.getCustomView().findViewById(R.id.tabtext);

                textView.setText(adapter.getPageTitle(i));
                Typeface face = Typeface.createFromAsset(this.getAssets(),
                        "fonts/montserrat-bold.ttf");
                textView.setTypeface(face);

                ColorStateList colors;
                if (Build.VERSION.SDK_INT >= 23) {
                    colors = getResources().getColorStateList(R.color.tab_icon_color, getTheme());
                }
                else {
                    colors = getResources().getColorStateList(R.color.tab_icon_color);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    DrawableCompat.setTintList(getDrawable(R.drawable.globe),colors);
                    DrawableCompat.setTintList(getDrawable(R.drawable.icon_notification),colors);
                }else{
                    DrawableCompat.setTintList(getResources().getDrawable(R.drawable.globe),colors);
                    DrawableCompat.setTintList(getResources().getDrawable(R.drawable.icon_notification),colors);
                }


                if(i==0){
                    imageView.setVisibility(View.VISIBLE);

                }else if(i==1){
                   imageView1.setVisibility(View.VISIBLE);
                   // int color = Color.parseColor("#b3ffffff");
                  //  imageView1.setColorFilter(R.drawable.tab_icon_color);
                    /*
                    textView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.icon_attention, 0, 0);
                    tabLayout.getTabAt(1).setCustomView(textView);*/

                }
            }
        }



        if (fragmentPosition != null && !fragmentPosition.isEmpty()) {
            viewPager.setCurrentItem(Integer.valueOf(fragmentPosition));
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        Bundle mparams = new Bundle();
                        mparams.putString("screen_name", "myfeed_screen");
                        mparams.putString("category", "tap"); //(only if possible)
                        mFirebaseAnalytics.logEvent("tap_discover", mparams);
                    case 1:
                        Bundle nparams = new Bundle();
                        nparams.putString("screen_name", "discover_screen");
                        nparams.putString("category", "tap"); //(only if possible)
                        mFirebaseAnalytics.logEvent("tap_myfeed", nparams);


                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString(SoupContract.FIREBASEID, null);

        //CVIPUL Analytics
        // TODO : put user's name/id/email/gender/age/etc details
        mFirebaseAnalytics.setUserId("");
        mFirebaseAnalytics.setUserProperty("user_name", "");
        mFirebaseAnalytics.setUserProperty("user_email", "");
        mFirebaseAnalytics.setUserProperty("user_gender", "");
        mFirebaseAnalytics.setUserProperty("user_dob", "");

        // TODO : Temperarily here, move to specific pager with its name
        // discover_screen / myfeed_screen / filters_screen
        Bundle mparams = new Bundle();
        mparams.putString("screen_name", "home_screen");
        mparams.putString("category", "screen_view");
        mFirebaseAnalytics.logEvent("viewed_screen_home", mparams);
        // End Analytics

        Log.e("MAIN_ACTIVITY", "Firebase reg id: " + regId);
    }


    public Fragment getFragment(int position) {

        Fragment fragment = (Fragment) viewPager.getAdapter().instantiateItem(viewPager, position);
        if (fragment instanceof DiscoverFragment) {
            DiscoverFragment object = (DiscoverFragment) fragment;

            return object;
        } else if (fragment instanceof MyFeedFragment) {
            MyFeedFragment object = (MyFeedFragment) fragment;
            return object;

        }

        return null;

    }


    @Override
    public void onClick(View view) {

        /*Intent intent = new Intent(MainActivity.this, FilterActivity.class);

        //CVIPUL Analytics
        // Assuming this is called on when somebody taps on "FILTER"
        // TODO : verify, put the name of originating 'pager' if possible.
        Bundle mparams = new Bundle();
        mparams.putString("screen_name", "home_screen");
        mparams.putString("count_filter_number", ""); //(only if possible)
        mFirebaseAnalytics.logEvent("tap_filter", mparams);
        // End Analytics

        startActivity(intent);*/

    }


    @Override
    public void NumberofUnread(String num_unread) {

        if (num_unread != null || !num_unread.isEmpty()) {

            if (num_unread.equals("0")) {
                View view = tabLayout.getTabAt(1).getCustomView();
                ImageView imageView= (ImageView)view.findViewById(R.id.notificationcircle);
                imageView.setVisibility(View.GONE);

            } else {
                View view = tabLayout.getTabAt(1).getCustomView();
                ImageView imageView= (ImageView)view.findViewById(R.id.notificationcircle);
                imageView.setVisibility(View.VISIBLE);
            }

        }


    }




    @Override
    public void onBackPressed() {

        Bundle mparams = new Bundle();
        mparams.putString("screen_name", "home_screen");
        mparams.putString("category", "tap"); //(only if possible)
        mFirebaseAnalytics.logEvent("tap_back", mparams);


        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 3000);
    }

    public void onclickFeed(View v){
        viewPager.setCurrentItem(0);
    }
}
