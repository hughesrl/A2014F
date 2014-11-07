package com.relhs.asianfinder.adapter;//package com.fourello.icare.adapters;
//
//import android.content.Context;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentStatePagerAdapter;
//import android.view.View;
//
//import com.fourello.icare.fragments.CheckInFragment;
//
///**
// * Created by Admin on 8/19/2014.
// */
//public class ViewPagerAdapter extends FragmentStatePagerAdapter {
//    private Context _context;
//
//    public ViewPagerAdapter(Context context, FragmentManager fm) {
//        super(fm);
//        _context=context;
//
//    }
//    @Override
//    public Fragment getItem(int position) {
//        Fragment f = new Fragment();
//        switch(position){
//            case 0:
//                f= CheckInFragment.newInstance(_context);
//                break;
//            case 1:
////                f=LayoutTwo.newInstance(_context);
//                break;
//        }
//        return f;
//    }
//    @Override
//    public int getCount() {
//        return 2;
//    }
//    @Override
//    public boolean isViewFromObject(View arg0, Object arg1) {
//        // TODO Auto-generated method stub
//        return false;
//    }
//
//
//}