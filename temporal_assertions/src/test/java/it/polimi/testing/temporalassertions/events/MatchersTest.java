package it.polimi.testing.temporalassertions.events;


import android.app.Activity;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.Test;

import static it.polimi.testing.temporalassertions.events.TextChangeEvent.isTextChangeEventFrom;
import static it.polimi.testing.temporalassertions.events.TextChangeEvent.isTextChangeEventWhereTextMatches;
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
        Matcher<?> matcher = isTextChangeEventWhereTextMatches(is(equalTo("Text")));
        assertTrue(matcher.matches(textChangeEvent));

        matcher = TextChangeEvent.isTextChangeEvent();
        assertTrue(matcher.matches(textChangeEvent));

        matcher = isTextChangeEventFrom(null);
        assertTrue(matcher.matches(textChangeEvent));

        matcher = TextChangeEvent.isTextChangeEvent(null, startsWith("Te"));
        assertTrue(matcher.matches(textChangeEvent));
    }

    @Test
    public void testTextChangeEventTextMatcherDifferentEvent()
    {
        final Event genericEvent = new GenericEvent(1, 2, 3);
        Matcher<?> matcher = isTextChangeEventWhereTextMatches(is(equalTo("Text")));
        assertFalse(matcher.matches(genericEvent));

        matcher = TextChangeEvent.isTextChangeEvent();
        assertFalse(matcher.matches(genericEvent));

        matcher = isTextChangeEventFrom(null);
        assertFalse(matcher.matches(genericEvent));

        matcher = TextChangeEvent.isTextChangeEvent(null, startsWith("Te"));
        assertFalse(matcher.matches(genericEvent));
    }



    @Test
    public void testPrintMatchers()
    {
        // Not a real test, just print descriptions of matchers

        System.out.println(ActivityLifecycleEvent.isActivityLifecycleEvent(A.class, "asd"));
        System.out.println(ActivityLifecycleEvent.isActivityLifecycleEvent(A.class));
        System.out.println(ActivityLifecycleEvent.isActivityLifecycleEvent("asd"));
        System.out.println(ActivityLifecycleEvent.isActivityLifecycleEvent());

        System.out.println(FragmentLifecycleEvent.isFragmentLifecycleEvent(F.class, "asd"));
        System.out.println(FragmentLifecycleEvent.isFragmentLifecycleEvent(F.class));
        System.out.println(FragmentLifecycleEvent.isFragmentLifecycleEvent("asd"));
        System.out.println(FragmentLifecycleEvent.isFragmentLifecycleEvent());

        System.out.println(CallbackEvent.isCallbackEvent("asd"));
        System.out.println(CallbackEvent.isCallbackEvent());

        System.out.println(GenericEvent.isGenericEventWithObjects("0", "1", "2"));
        System.out.println(GenericEvent.isGenericEventWithObjectsThatMatch(anything()));
        System.out.println(GenericEvent.isGenericEvent());

        System.out.println(TextChangeEvent.isTextChangeEvent(textView, CoreMatchers.is("asd")));
        System.out.println(TextChangeEvent.isTextChangeEventWhereTextMatches(endsWith("asd")));
        System.out.println(TextChangeEvent.isTextChangeEventFrom(textView));
        System.out.println(TextChangeEvent.isTextChangeEvent());
    }








    private static class A extends Activity
    {

    }

    public static class F extends Fragment
    {

    }

    private static TextView textView = null;
}
