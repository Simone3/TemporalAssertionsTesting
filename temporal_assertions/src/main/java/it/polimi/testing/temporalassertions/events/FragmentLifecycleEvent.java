package it.polimi.testing.temporalassertions.events;

import android.support.v4.app.Fragment;

public class FragmentLifecycleEvent extends LifecycleEvent
{
    private final Class<? extends Fragment> fragmentClass;

    public FragmentLifecycleEvent(Class<? extends Fragment> fragmentClass, String callbackName)
    {
        super("Fragment", callbackName);
        this.fragmentClass = fragmentClass;
    }

    public Class<? extends Fragment> getFragmentClass()
    {
        return fragmentClass;
    }
}
