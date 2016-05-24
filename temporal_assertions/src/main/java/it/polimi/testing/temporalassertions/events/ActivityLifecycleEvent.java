package it.polimi.testing.temporalassertions.events;

import android.app.Activity;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.equalTo;

/**
 * Lifecycle change event for activities
 */
public class ActivityLifecycleEvent extends CallbackEvent
{
    private final Class<? extends Activity> activityClass;

    /**
     * Constructor
     * @param activityClass the class of the activity
     * @param callbackName the name of the lifecycle callback
     */
    public ActivityLifecycleEvent(Class<? extends Activity> activityClass, String callbackName)
    {
        super(callbackName);
        this.activityClass = activityClass;
    }

    /**
     * Getter
     * @return the class of the activity
     */
    public Class<? extends Activity> getActivityClass()
    {
        return activityClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "{"+activityClass.getSimpleName()+" Lifecycle '"+getCallbackName()+"'}";
    }

    /**
     * Hamcrest matcher that matches a lifecycle event from the given activity with the given callback name
     * @param activityClass the activity linked with the event
     * @param callbackName the name of the lifecycle callback
     * @return the Hamcrest matcher
     */
    public static Matcher<ActivityLifecycleEvent> isActivityLifecycleEvent(final Class<? extends Activity> activityClass, final String callbackName)
    {
        return both(isActivityLifecycleEvent(activityClass)).and(isActivityLifecycleEvent(callbackName));
    }

    /**
     * Hamcrest matcher that matches a lifecycle event with the given callback name from any activity
     * @param callbackName the name of the lifecycle callback
     * @return the Hamcrest matcher
     */
    public static Matcher<ActivityLifecycleEvent> isActivityLifecycleEvent(final String callbackName)
    {
        return new FeatureMatcher<ActivityLifecycleEvent, String>(equalTo(callbackName), "is an activity lifecycle event", "callback name")
        {
            @Override
            protected String featureValueOf(final ActivityLifecycleEvent actual)
            {
                return actual.getCallbackName();
            }
        };
    }

    /**
     * Hamcrest matcher that matches a lifecycle event from the given activity with any callback name
     * @param activityClass the activity linked with the event
     * @return the Hamcrest matcher
     */
    public static Matcher<ActivityLifecycleEvent> isActivityLifecycleEvent(final Class<? extends Activity> activityClass)
    {
        return new FeatureMatcher<ActivityLifecycleEvent, Class<? extends Activity>>(IsEqual.<Class<? extends Activity>>equalTo(activityClass), "is an activity lifecycle event from", "activity class")
        {
            @Override
            protected Class<? extends Activity> featureValueOf(final ActivityLifecycleEvent actual)
            {
                return actual.getActivityClass();
            }
        };
    }

    /**
     * Hamcrest matcher that matches any activity lifecycle event (any activity and any callback name)
     * @return the Hamcrest matcher
     */
    public static Matcher<ActivityLifecycleEvent> isActivityLifecycleEvent()
    {
        return new FeatureMatcher<ActivityLifecycleEvent, String>(anything(""), "is any activity lifecycle event", "")
        {
            @Override
            protected String featureValueOf(final ActivityLifecycleEvent actual)
            {
                return null;
            }
        };
    }
}
