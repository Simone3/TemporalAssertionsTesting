package it.polimi.testing.temporalassertions.events;

import android.app.Fragment;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.equalTo;

/**
 * Lifecycle change event for fragments
 */
public class FragmentLifecycleEvent extends CallbackEvent
{
    public final static String ON_ATTACH = "onAttach";
    public final static String ON_CREATE = "onCreate";
    public final static String ON_CREATE_VIEW = "onCreateView";
    public final static String ON_ACTIVITY_CREATED = "onActivityCreated";
    public final static String ON_START = "onStart";
    public final static String ON_RESTART = "onRestart";
    public final static String ON_RESUME = "onResume";
    public final static String ON_PAUSE = "onPause";
    public final static String ON_STOP = "onStop";
    public final static String ON_DESTROY_VIEW = "onDestroyView";
    public final static String ON_DESTROY = "onDestroy";
    public final static String ON_DETACH = "onDetach";
    public final static String ON_SAVE_INSTANCE_STATE = "onSaveInstanceState";

    private final Class<? extends Fragment> fragmentClass;

    /**
     * Constructor
     * @param fragmentClass the class of the fragment
     * @param callbackName the name of the lifecycle callback
     */
    public FragmentLifecycleEvent(Class<? extends Fragment> fragmentClass, String callbackName)
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
    public static Matcher<FragmentLifecycleEvent> isFragmentLifecycleEvent(final Class<? extends Fragment> fragmentClass, final String callbackName)
    {
        return both(isFragmentLifecycleEvent(fragmentClass)).and(isFragmentLifecycleEvent(callbackName));
    }

    /**
     * Hamcrest matcher that matches a lifecycle event with the given callback name from any fragment
     * @param callbackName the name of the lifecycle callback
     * @return the Hamcrest matcher
     */
    public static Matcher<FragmentLifecycleEvent> isFragmentLifecycleEvent(final String callbackName)
    {
        return new FeatureMatcher<FragmentLifecycleEvent, String>(equalTo(callbackName), "is a fragment lifecycle event", "callback name")
        {
            @Override
            protected String featureValueOf(final FragmentLifecycleEvent actual)
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
    public static Matcher<FragmentLifecycleEvent> isFragmentLifecycleEvent(final Class<? extends Fragment> fragmentClass)
    {
        return new FeatureMatcher<FragmentLifecycleEvent, Class<? extends Fragment>>(IsEqual.<Class<? extends Fragment>>equalTo(fragmentClass), "is a fragment lifecycle event from", "fragment class")
        {
            @Override
            protected Class<? extends Fragment> featureValueOf(final FragmentLifecycleEvent actual)
            {
                return actual.getFragmentClass();
            }
        };
    }

    /**
     * Hamcrest matcher that matches any fragment lifecycle event (any fragment and any callback name)
     * @return the Hamcrest matcher
     */
    public static Matcher<FragmentLifecycleEvent> isFragmentLifecycleEvent()
    {
        return new FeatureMatcher<FragmentLifecycleEvent, Void>(anything(""), "is any fragment lifecycle event", "")
        {
            @Override
            protected Void featureValueOf(final FragmentLifecycleEvent actual)
            {
                return null;
            }
        };
    }
}
