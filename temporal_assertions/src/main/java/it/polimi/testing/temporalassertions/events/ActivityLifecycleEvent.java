package it.polimi.testing.temporalassertions.events;

import android.app.Activity;

public class ActivityLifecycleEvent extends LifecycleEvent
{
    private final Class<? extends Activity> activityClass;

    public ActivityLifecycleEvent(Class<? extends Activity> activityClass, String callbackName)
    {
        super("Activity", callbackName);
        this.activityClass = activityClass;
    }

    public Class<? extends Activity> getActivityClass()
    {
        return activityClass;
    }
}
