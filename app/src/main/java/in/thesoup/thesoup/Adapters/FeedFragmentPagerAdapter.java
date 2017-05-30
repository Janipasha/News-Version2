package in.thesoup.thesoup.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.SparseArray;
import android.view.ViewGroup;

import in.thesoup.thesoup.Activities.MainActivity;
import in.thesoup.thesoup.Fragments.DiscoverFragment;
import in.thesoup.thesoup.Fragments.FilterFragment;
import in.thesoup.thesoup.Fragments.MyFeedFragment;
import in.thesoup.thesoup.R;

/**
 * Created by Jani on 22-05-2017.
 */

public class FeedFragmentPagerAdapter extends FragmentStatePagerAdapter{

    private Context mcontext;
    SharedPreferences pref;
    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    public FeedFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mcontext = context;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0: // Fragment # 0 - This will show FirstFragment
                return new DiscoverFragment();
            case 1: // Fragment # 0 - This will show FirstFragment different title
                return new MyFeedFragment();
            case 2: // Fragment # 1 - This will show SecondFragment
                return new FilterFragment();
            default:
                return null;
        }}

     /*   if (position == 0) {
           Fragment discoverFragment =  new DiscoverFragment();
            //activity.getSupportFragmentManager().beginTransaction().add(discoverFragment,"discovertag").commit();
            return discoverFragment;
        } else if (position == 1){
            Fragment myFeedFragment =  new MyFeedFragment();
            return myFeedFragment;
        } else {
            Fragment filterFragment =  new FilterFragment();
            return filterFragment;
        }
    }*/

    /*@Override
    public int getItemPosition(Object object) {
        // POSITION_NONE makes it possible to reload the PagerAdapter
        return POSITION_NONE;
    }*/

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Discover";
        } else if (position == 1){
            return "Myfeed";
        } else {
            return "Filter";
        }
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }


}