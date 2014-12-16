package com.relhs.asianfinder.utils;

import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.List;

/**
 * Created by Admin on 8/26/2014.
 */
public class FragmentUtils {

    public interface ActivityForResultStarter
    {
        void startActivityForResultWhileSavingOrigin(int requestCode, Intent intent, int[] indices);
    }
    /**
     * Start an activity for result.
     * This method keeps track of all the fragment-indexes inside the active fragments.
     * @param fragment
     * @param requestCode
     * @param intent
     * @param indices
     */
    public final static void startActivityForResultWhileSavingOrigin(Fragment fragment, int requestCode, Intent intent, int[] indices)
    {
//get a list of all the active fragments
        final List<Fragment> fragments = fragment.getFragmentManager().getFragments();
        if (fragments != null)
        {
//find the index of this fragment inside the active list
            final int index = fragments.indexOf(fragment);

            if (indices == null)
            {
                indices = new int[1];
            }
            else
            {
//enlarge the array
                int[] newIndices = new int[indices.length + 1];
                System.arraycopy(indices, 0, newIndices, 0, indices.length);
                indices = newIndices;
            }
//save our index on the last part
            indices[indices.length - 1] = index;


            if (fragment.getParentFragment() != null)
            {
//if there is a parent fragment, we call this method again but with the parent fragment
                startActivityForResultWhileSavingOrigin(fragment.getParentFragment(), requestCode, intent, indices);
            }
            else if (fragment.getActivity() != null && fragment.getActivity() instanceof ActivityForResultStarter)
            {
//there is no parent fragment, it must mean we are at the activity-level, so start it from there!
                ((ActivityForResultStarter)fragment.getActivity()).startActivityForResultWhileSavingOrigin(requestCode, intent, indices);
            }
            else
            {
//nothing else is available, just start it normally
                fragment.startActivityForResult(intent, requestCode);
            }
        }
        else
        {
//there are no active fragments, just start it normally
            fragment.startActivityForResult(intent, requestCode);
        }
    }
}
