package kenhoang.dev.app.livewallpaper.adapter;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import kenhoang.dev.app.livewallpaper.fragment.CategoryFragment;
import kenhoang.dev.app.livewallpaper.fragment.TrendingFragment;
import kenhoang.dev.app.livewallpaper.fragment.RecentFragment;

public class MyFragmentAdapter extends FragmentPagerAdapter {

    private Context context;

    public MyFragmentAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0)
            return CategoryFragment.getInstance();
        else if (position == 1)
            return TrendingFragment.getInstance();
        else if (position == 2)
            return RecentFragment.getInstance(context);
        else
            return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Category";
            case 1:
                return "Trending";
            case 2:
                return "Recents";
        }
        return "";
    }
}
