package it.polimi.testing.temporalassertions.events;


import android.app.Activity;
import android.app.Fragment;
import android.widget.TextView;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.Test;

import static it.polimi.testing.temporalassertions.events.FragmentLifecycleEvent.ON_CREATE;
import static it.polimi.testing.temporalassertions.events.TextChangeEvent.isTextChangeFrom;
import static it.polimi.testing.temporalassertions.events.TextChangeEvent.isTextChangeWhereTextMatches;
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

        matcher = TextChangeEvent.isTextChange();
        assertTrue(matcher.matches(textChangeEvent));

        matcher = isTextChangeFrom(null);
        assertTrue(matcher.matches(textChangeEvent));

        matcher = TextChangeEvent.isTextChange(null, startsWith("Te"));
        assertTrue(matcher.matches(textChangeEvent));
    }

    @Test
    public void testTextChangeEventTextMatcherDifferentEvent()
    {
        final Event genericEvent = new GenericEvent(1, 2, 3);
        Matcher<?> matcher = isTextChangeWhereTextMatches(is(equalTo("Text")));
        assertFalse(matcher.matches(genericEvent));

        matcher = TextChangeEvent.isTextChange();
        assertFalse(matcher.matches(genericEvent));

        matcher = isTextChangeFrom(null);
        assertFalse(matcher.matches(genericEvent));

        matcher = TextChangeEvent.isTextChange(null, startsWith("Te"));
        assertFalse(matcher.matches(genericEvent));
    }



    @Test
    public void testPrintMatchers()
    {
        // Not a real test, just print descriptions of matchers

        System.out.println(ActivityLifecycleEvent.isActivityLifecycleEvent(A.class, ON_CREATE));
        System.out.println(ActivityLifecycleEvent.isActivityLifecycleEvent(A.class));
        System.out.println(ActivityLifecycleEvent.isActivityLifecycleEvent(ON_CREATE));
        System.out.println(ActivityLifecycleEvent.isActivityLifecycleEvent());

        System.out.println(FragmentLifecycleEvent.isFragmentLifecycleEvent(F.class, ON_CREATE));
        System.out.println(FragmentLifecycleEvent.isFragmentLifecycleEvent(F.class));
        System.out.println(FragmentLifecycleEvent.isFragmentLifecycleEvent(ON_CREATE));
        System.out.println(FragmentLifecycleEvent.isFragmentLifecycleEvent());

        System.out.println(CallbackEvent.isCallbackEvent(ON_CREATE));
        System.out.println(CallbackEvent.isCallbackEvent());

        System.out.println(GenericEvent.isGenericEventWithObjects("0", "1", "2"));
        System.out.println(GenericEvent.isGenericEventWithObjectsThatMatch(anything()));
        System.out.println(GenericEvent.isGenericEvent());

        System.out.println(TextChangeEvent.isTextChange(textView, CoreMatchers.is(ON_CREATE)));
        System.out.println(TextChangeEvent.isTextChangeWhereTextMatches(endsWith(ON_CREATE)));
        System.out.println(TextChangeEvent.isTextChangeFrom(textView));
        System.out.println(TextChangeEvent.isTextChange());
    }








    private static class A extends Activity
    {

    }

    public static class F extends Fragment
    {

    }

    private static TextView textView = null;
}
