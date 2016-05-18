package it.polimi.testing.matchers;


import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;

import java.util.Arrays;
import java.util.List;

import it.polimi.testing.temporalassertions.events.ActivityLifecycleEvent;
import it.polimi.testing.temporalassertions.events.CallbackEvent;
import it.polimi.testing.temporalassertions.events.FragmentLifecycleEvent;
import it.polimi.testing.temporalassertions.events.GenericEvent;
import it.polimi.testing.temporalassertions.events.TextChangeEvent;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Every.everyItem;

// TODO move them in events classes?
public class Matchers
{
    public static Matcher<TextChangeEvent> isTextChangeEvent(final View view, final Matcher<String> matcher)
    {
        return both(isTextChangeEventFrom(view)).and(isTextChangeEventWhereTextMatches(matcher));
    }

    public static Matcher<TextChangeEvent> isTextChangeEventFrom(final View view)
    {
        return new FeatureMatcher<TextChangeEvent, View>(equalTo(view), "is a text change event whose view", "view")
        {
            @Override
            protected View featureValueOf(final TextChangeEvent actual)
            {
                return actual.getView();
            }
        };
    }

    public static Matcher<TextChangeEvent> isTextChangeEventWhereTextMatches(final Matcher<String> matcher)
    {
        return new FeatureMatcher<TextChangeEvent, String>(matcher, "is a text change event whose text", "text")
        {
            @Override
            protected String featureValueOf(final TextChangeEvent actual)
            {
                return actual.getText();
            }
        };
    }

    public static Matcher<TextChangeEvent> isTextChangeEvent()
    {
        return new FeatureMatcher<TextChangeEvent, String>(anything(), "is a text change event", "")
        {
            @Override
            protected String featureValueOf(final TextChangeEvent actual)
            {
                return "";
            }
        };
    }





    public static Matcher<GenericEvent> isGenericEventWithObjects(final Object... objects)
    {
        return new FeatureMatcher<GenericEvent, Object[]>(equalTo(objects), "is a generic event with objects", "objects")
        {
            @Override
            protected Object[] featureValueOf(final GenericEvent actual)
            {
                return actual.getObjects();
            }
        };
    }

    public static Matcher<GenericEvent> isGenericEventWithObjectsThatMatch(final Matcher<Object> matcher)
    {
        return new FeatureMatcher<GenericEvent, List<Object>>(everyItem(matcher), "is a generic event with objects that match", "objects")
        {
            @Override
            protected List<Object> featureValueOf(final GenericEvent actual)
            {
                return Arrays.asList(actual.getObjects());
            }
        };
    }

    public static Matcher<GenericEvent> isGenericEvent()
    {
        return new FeatureMatcher<GenericEvent, Object[]>(anything(), "is a generic event", "")
        {
            @Override
            protected Object[] featureValueOf(final GenericEvent actual)
            {
                return null;
            }
        };
    }




    public static Matcher<CallbackEvent> isCallbackEvent()
    {
        return new FeatureMatcher<CallbackEvent, String>(anything(), "is a callback event", "")
        {
            @Override
            protected String featureValueOf(final CallbackEvent actual)
            {
                return null;
            }
        };
    }

    public static Matcher<CallbackEvent> isCallbackEvent(String callbackName)
    {
        return new FeatureMatcher<CallbackEvent, String>(equalTo(callbackName), "is the callback event", "")
        {
            @Override
            protected String featureValueOf(final CallbackEvent actual)
            {
                return actual.getCallbackName();
            }
        };
    }






    public static Matcher<ActivityLifecycleEvent> isActivityLifecycleEvent()
    {
        return new FeatureMatcher<ActivityLifecycleEvent, String>(anything(), "is an activity lifecycle event", "")
        {
            @Override
            protected String featureValueOf(final ActivityLifecycleEvent actual)
            {
                return null;
            }
        };
    }

    public static Matcher<ActivityLifecycleEvent> isActivityLifecycleEvent(final String callbackName)
    {
        return new FeatureMatcher<ActivityLifecycleEvent, String>(equalTo(callbackName), "is the activity lifecycle event", "")
        {
            @Override
            protected String featureValueOf(final ActivityLifecycleEvent actual)
            {
                return actual.getCallbackName();
            }
        };
    }

    public static Matcher<ActivityLifecycleEvent> isActivityLifecycleEvent(final Class<? extends Activity> activityClass)
    {
        return new FeatureMatcher<ActivityLifecycleEvent, Class<? extends Activity>>(IsEqual.<Class<? extends Activity>>equalTo(activityClass), "is the activity lifecycle event from", "")
        {
            @Override
            protected Class<? extends Activity> featureValueOf(final ActivityLifecycleEvent actual)
            {
                return actual.getActivityClass();
            }
        };
    }

    public static Matcher<ActivityLifecycleEvent> isActivityLifecycleEvent(final Class<? extends Activity> activityClass, final String callbackName)
    {
        return both(isActivityLifecycleEvent(activityClass)).and(isActivityLifecycleEvent(callbackName));
    }

    public static Matcher<FragmentLifecycleEvent> isFragmentLifecycleEvent()
    {
        return new FeatureMatcher<FragmentLifecycleEvent, String>(anything(), "is a fragment lifecycle event", "")
        {
            @Override
            protected String featureValueOf(final FragmentLifecycleEvent actual)
            {
                return null;
            }
        };
    }

    public static Matcher<FragmentLifecycleEvent> isFragmentLifecycleEvent(final String callbackName)
    {
        return new FeatureMatcher<FragmentLifecycleEvent, String>(equalTo(callbackName), "is the fragment lifecycle event", "")
        {
            @Override
            protected String featureValueOf(final FragmentLifecycleEvent actual)
            {
                return actual.getCallbackName();
            }
        };
    }

    public static Matcher<FragmentLifecycleEvent> isFragmentLifecycleEvent(final Class<? extends Fragment> fragmentClass)
    {
        return new FeatureMatcher<FragmentLifecycleEvent, Class<? extends Fragment>>(IsEqual.<Class<? extends Fragment>>equalTo(fragmentClass), "is the fragment lifecycle event from", "")
        {
            @Override
            protected Class<? extends Fragment> featureValueOf(final FragmentLifecycleEvent actual)
            {
                return actual.getFragmentClass();
            }
        };
    }

    public static Matcher<FragmentLifecycleEvent> isFragmentLifecycleEvent(final Class<? extends Fragment> fragmentClass, final String callbackName)
    {
        return both(isFragmentLifecycleEvent(fragmentClass)).and(isFragmentLifecycleEvent(callbackName));
    }
}
