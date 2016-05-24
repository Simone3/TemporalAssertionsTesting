package it.polimi.testing.temporalassertions;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import org.junit.Test;

import it.polimi.testing.temporalassertions.events.ActivityLifecycleEvent;
import it.polimi.testing.temporalassertions.events.CallbackEvent;
import it.polimi.testing.temporalassertions.events.FragmentLifecycleEvent;
import it.polimi.testing.temporalassertions.events.GenericEvent;
import it.polimi.testing.temporalassertions.events.TextChangeEvent;

import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;

public class MatchersTest
{
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

        System.out.println(TextChangeEvent.isTextChangeEvent(textView, is("asd")));
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
