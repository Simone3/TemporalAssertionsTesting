package it.polimi.testing.temporalassertions.events;


import android.app.Activity;
import android.app.Fragment;
import android.widget.TextView;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.Test;

import it.polimi.testing.temporalassertions.R;

import static it.polimi.testing.temporalassertions.events.ActivityLifecycleEvent.isActivityLifecycleEvent;
import static it.polimi.testing.temporalassertions.events.CallbackEvent.isCallbackEvent;
import static it.polimi.testing.temporalassertions.events.FragmentLifecycleEvent.ON_CREATE;
import static it.polimi.testing.temporalassertions.events.FragmentLifecycleEvent.isFragmentLifecycleEvent;
import static it.polimi.testing.temporalassertions.events.GenericEvent.isGenericEventWithObjects;
import static it.polimi.testing.temporalassertions.events.MenuClickEvent.isMenuClick;
import static it.polimi.testing.temporalassertions.events.TextChangeEvent.isTextChange;
import static it.polimi.testing.temporalassertions.events.TextChangeEvent.isTextChangeFrom;
import static it.polimi.testing.temporalassertions.events.TextChangeEvent.isTextChangeWhereTextMatches;
import static it.polimi.testing.temporalassertions.events.ToastEvent.isToastDisplay;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringStartsWith.startsWith;

public class MatchersTest
{
    @Test
    public void testTextChangeEventTextMatcher()
    {
        final TextChangeEvent textChangeEvent = new TextChangeEvent(null, "Text");
        Matcher<?> matcher = isTextChangeWhereTextMatches(is(equalTo("Text")));
        assertTrue(matcher.matches(textChangeEvent));

        matcher = isTextChange();
        assertTrue(matcher.matches(textChangeEvent));

        matcher = isTextChangeFrom(null);
        assertTrue(matcher.matches(textChangeEvent));

        matcher = isTextChange(null, startsWith("Te"));
        assertTrue(matcher.matches(textChangeEvent));
    }

    @Test
    public void testTextChangeEventTextMatcherDifferentEvent()
    {
        final Event genericEvent = new GenericEvent(1, 2, 3);
        Matcher<?> matcher = isTextChangeWhereTextMatches(is(equalTo("Text")));
        assertFalse(matcher.matches(genericEvent));

        matcher = isTextChange();
        assertFalse(matcher.matches(genericEvent));

        matcher = isTextChangeFrom(null);
        assertFalse(matcher.matches(genericEvent));

        matcher = isTextChange(null, startsWith("Te"));
        assertFalse(matcher.matches(genericEvent));
    }



    @Test
    public void testPrintMatchers()
    {
        // Not a real test, just print descriptions of matchers

        System.out.println(isActivityLifecycleEvent(A.class, ON_CREATE));
        System.out.println(isActivityLifecycleEvent(A.class));
        System.out.println(isActivityLifecycleEvent(ON_CREATE));
        System.out.println(isActivityLifecycleEvent());

        System.out.println(isFragmentLifecycleEvent(F.class, ON_CREATE));
        System.out.println(isFragmentLifecycleEvent(F.class));
        System.out.println(isFragmentLifecycleEvent(ON_CREATE));
        System.out.println(isFragmentLifecycleEvent());

        System.out.println(isCallbackEvent(ON_CREATE));
        System.out.println(isCallbackEvent());

        System.out.println(isGenericEventWithObjects("0", "1", "2"));
        System.out.println(GenericEvent.isGenericEventWithObjectsThatMatch(anything()));
        System.out.println(GenericEvent.isGenericEvent());

        System.out.println(isTextChange(textView, CoreMatchers.is(ON_CREATE)));
        System.out.println(isTextChangeWhereTextMatches(endsWith(ON_CREATE)));
        System.out.println(isTextChangeFrom(textView));
        System.out.println(isTextChange());

        System.out.println(isToastDisplay(equalTo("Message")));
        System.out.println(isToastDisplay(startsWith("Message")));
        System.out.println(isToastDisplay());

        System.out.println(isMenuClick(123));
        System.out.println(isMenuClick());
    }








    private static class A extends Activity
    {

    }

    public static class F extends Fragment
    {

    }

    private static TextView textView = null;
}
