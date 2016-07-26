package it.polimi.testing.temporalassertions.events;

import android.support.v4.app.Fragment;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.equalTo;

/**
 * Lifecycle change event for fragments (support library)
 */
public class SupportFragmentLifecycleEvent extends CallbackEvent
{
    private final Class<? extends Fragment> fragmentClass;

    /**
     * Constructor
     * @param fragmentClass the class of the fragment
     * @param callbackName the name of the lifecycle callback
     */
    public SupportFragmentLifecycleEvent(Class<? extends Fragment> fragmentClass, String callbackName)
    {
        super(callbackName);
        this.fragmentClass = fragmentClass;
    }

    /**
     * Getter
     * @return the class of the fragment
     */
    public Class<? extends Fragment> getFragmentClass()
    {
        return fragmentClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "{"+fragmentClass.getSimpleName()+" Lifecycle '"+getCallbackName()+"'}";
    }

    /**
     * Hamcrest matcher that matches a lifecycle event from the given fragment with the given callback name
     * @param fragmentClass the fragment linked with the event
     * @param callbackName the name of the lifecycle callback
     * @return the Hamcrest matcher
     */
    public static Matcher<SupportFragmentLifecycleEvent> isSupportFragmentLifecycleEvent(final Class<? extends Fragment> fragmentClass, final String callbackName)
    {
        return both(isSupportFragmentLifecycleEvent(fragmentClass)).and(isSupportFragmentLifecycleEvent(callbackName));
    }

    /**
     * Hamcrest matcher that matches a lifecycle event with the given callback name from any fragment
     * @param callbackName the name of the lifecycle callback
     * @return the Hamcrest matcher
     */
    public static Matcher<SupportFragmentLifecycleEvent> isSupportFragmentLifecycleEvent(final String callbackName)
    {
        return new FeatureMatcher<SupportFragmentLifecycleEvent, String>(equalTo(callbackName), "is a fragment lifecycle event", "callback name")
        {
            @Override
            protected String featureValueOf(final SupportFragmentLifecycleEvent actual)
            {
                return actual.getCallbackName();
            }
        };
    }

    /**
     * Hamcrest matcher that matches a lifecycle event from the given fragment with any callback name
     * @param fragmentClass the fragment linked with the event
     * @return the Hamcrest matcher
     */
    public static Matcher<SupportFragmentLifecycleEvent> isSupportFragmentLifecycleEvent(final Class<? extends Fragment> fragmentClass)
    {
        return new FeatureMatcher<SupportFragmentLifecycleEvent, Class<? extends Fragment>>(IsEqual.<Class<? extends Fragment>>equalTo(fragmentClass), "is a fragment lifecycle event from", "fragment class")
        {
            @Override
            protected Class<? extends Fragment> featureValueOf(final SupportFragmentLifecycleEvent actual)
            {
                return actual.getFragmentClass();
            }
        };
    }

    /**
     * Hamcrest matcher that matches any fragment lifecycle event (any fragment and any callback name)
     * @return the Hamcrest matcher
     */
    public static Matcher<SupportFragmentLifecycleEvent> isSupportFragmentLifecycleEvent()
    {
        return new FeatureMatcher<SupportFragmentLifecycleEvent, String>(anything(""), "is any fragment lifecycle event", "")
        {
            @Override
            protected String featureValueOf(final SupportFragmentLifecycleEvent actual)
            {
                return null;
            }
        };
    }
}
