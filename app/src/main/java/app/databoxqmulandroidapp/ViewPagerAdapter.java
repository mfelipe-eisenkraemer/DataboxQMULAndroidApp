package app.databoxqmulandroidapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by MateusFelipe on 18/06/2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm,CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        // first tab
        if(position == 0) {
            FragTabGeneralInfo tab1 = new FragTabGeneralInfo();
            return tab1;
        }else if( position == 1 ) {
            FragTabLocations tab2 = new FragTabLocations();
            return tab2;
        }else if( position == 2 ){
            FragTabSentiments tab3 = new FragTabSentiments();
            return tab3;
        }else if( position == 3 ){
            FragTabTrends tab4 = new FragTabTrends();
            return tab4;
        }else if( position == 4 ){
            FragTabSteps tab5 = new FragTabSteps();
            return tab5;
        }

        return null;
    }

    // This method return the titles for the Tabs in the Tab Strip
    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip
    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}
