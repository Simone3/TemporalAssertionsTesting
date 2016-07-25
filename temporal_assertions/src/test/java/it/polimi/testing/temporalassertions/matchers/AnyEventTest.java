package it.polimi.testing.temporalassertions.matchers;


import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.junit.Test;

import it.polimi.testing.temporalassertions.events.Event;

import static it.polimi.testing.temporalassertions.matchers.AnyEvent.anyEvent;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.anything;

public class AnyEventTest
{
    @Test
    public void testA_Match()
    {
        EventA e = new EventA();
        assertTrue(anyEvent(isEventA(), isEventB(), isEventAA()).matches(e));
    }

    @Test
    public void testA_NoMatch()
    {
        EventA e = new EventA();
        assertFalse(anyEvent(isEventB(), isEventAA()).matches(e));
    }

    @Test
    public void testB_Match()
    {
        EventB e = new EventB();
        assertTrue(anyEvent(isEventA(), isEventB(), isEventAA()).matches(e));
    }

    @Test
    public void testB_NoMatch()
    {
        EventB e = new EventB();
        assertFalse(anyEvent(isEventA(), isEventAA()).matches(e));
    }

    @Test
    public void testAA_Match1()
    {
        EventAA e = new EventAA();
        assertTrue(anyEvent(isEventA(), isEventB(), isEventAA()).matches(e));
    }

    @Test
    public void testAA_Match2()
    {
        EventAA e = new EventAA();
        assertTrue(anyEvent(isEventA(), isEventB()).matches(e));
    }

    @Test
    public void testAA_NoMatch()
    {
        EventAA e = new EventAA();
        assertFalse(anyEvent(isEventB(), isEventB()).matches(e));
    }

    @Test
    public void testOtherEvent()
    {
        OtherEvent e = new OtherEvent();
        assertFalse(anyEvent(isEventA(), isEventB(), isEventAA()).matches(e));
    }







    private static class EventA extends Event{}
    private static class EventB extends Event{}
    private static class EventAA extends EventA{}
    private static class OtherEvent extends Event{}

    private static Matcher<EventA> isEventA()
    {
        return new FeatureMatcher<EventA, Void>(anything(""), "is event A", "")
        {
            @Override
            protected Void featureValueOf(final EventA actual)
            {
                return null;
            }
        };
    }

    private static Matcher<EventB> isEventB()
    {
        return new FeatureMatcher<EventB, Void>(anything(""), "is event B", "")
        {
            @Override
            protected Void featureValueOf(final EventB actual)
            {
                return null;
            }
        };
    }

    private static Matcher<EventAA> isEventAA()
    {
        return new FeatureMatcher<EventAA, Void>(anything(""), "is event AA", "")
        {
            @Override
            protected Void featureValueOf(final EventAA actual)
            {
                return null;
            }
        };
    }
}