package sg.bigo.common.view;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

public class MyFragmentAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> list;
    private List<String> titles;

    public MyFragmentAdapter(FragmentManager fm, List<Fragment> list, List<String> titles) {
        super(fm);
        this.list = list;
        this.titles = titles;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Fragment getItem(int i) {
        return list.get(i);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
