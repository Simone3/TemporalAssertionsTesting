package it.polimi.testing.temporalassertions.events;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

/**
 * TODO
 */
public class BackStackChangeEvent extends Event
{
    private final FragmentManager fragmentManager;

    @CheckResult
    @NonNull
    public static BackStackChangeEvent create(@NonNull FragmentManager fragmentManager)
    {
        return new BackStackChangeEvent(fragmentManager);
    }

    private BackStackChangeEvent(@NonNull FragmentManager fragmentManager)
    {
        this.fragmentManager = fragmentManager;
    }

    public @NonNull FragmentManager fragmentManager()
    {
        return fragmentManager;
    }
}
