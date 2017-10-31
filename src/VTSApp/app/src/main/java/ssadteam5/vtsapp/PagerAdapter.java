package ssadteam5.vtsapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    private Bundle bun;

    public PagerAdapter(FragmentManager fm, int NumOfTabs, Bundle bundle) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        bun=bundle;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
               TripReport tab1= new TripReport();
               tab1.setArguments(bun);
               return tab1;
            case 1:
                IdleReport tab2 = new IdleReport();
                tab2.setArguments(bun);
                return tab2;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


}