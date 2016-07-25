package it.polimi.testing.temporalassertions.matchers;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import java.util.Arrays;

import it.polimi.testing.temporalassertions.events.Event;

/**
 * Similar to {@link org.hamcrest.core.AnyOf} for events. Instead of "? super T" generics, it accepts
 * "? extends Event" in order to express conditions on any subclass of {@link Event}
 */
public class AnyEvent extends EventShortcutCombination
{
    /**
     * Constructor
     * @param matchers list of event matchers
     */
    private AnyEvent(Iterable<Matcher<? extends Event>> matchers)
    {
        super(matchers);
    }

    /**
     * Similar to {@link org.hamcrest.core.AnyOf}: matches if at least one of the sub-matchers
     * matches the given event
     * @param matchers list of event matchers
     * @return the final matcher
     */
    @Factory
    public static AnyEvent anyEvent(Iterable<Matcher<? extends Event>> matchers)
    {
        return new AnyEvent(matchers);
    }

    /**
     * Similar to {@link org.hamcrest.core.AnyOf}: matches if at least one of the sub-matchers
     * matches the given event
     * @param matchers list of event matchers
     * @return the final matcher
     */
    @SafeVarargs
    @Factory
    public static AnyEvent anyEvent(Matcher<? extends Event>... matchers)
    {
        return anyEvent(Arrays.asList(matchers));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(Object o)
    {
        return matches(o, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void describeTo(Description description)
    {
        describeTo(description, "or");
    }
}
