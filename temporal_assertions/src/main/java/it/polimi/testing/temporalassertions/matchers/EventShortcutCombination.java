package it.polimi.testing.temporalassertions.matchers;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import it.polimi.testing.temporalassertions.events.Event;

/**
 * Similar to {@link org.hamcrest.core.ShortcutCombination}: utility for {@link AnyEvent}
 */
abstract class EventShortcutCombination extends BaseMatcher
{
    private final Iterable<Matcher<? extends Event>> matchers;

    /**
     * Constructor
     * @param matchers the list of the sub-matchers
     */
    public EventShortcutCombination(Iterable<Matcher<? extends Event>> matchers)
    {
        this.matchers = matchers;
    }

    /**
     * Similar to {@link BaseMatcher#matches(Object)}
     * @param o the object to match
     * @param shortcut boolean to "short-circuit" the computation
     * @return true if the set of matchers matches
     */
    protected boolean matches(Object o, boolean shortcut)
    {
        for(Matcher<? extends Event> matcher : matchers)
        {
            if(matcher.matches(o) == shortcut)
            {
                return shortcut;
            }
        }
        return !shortcut;
    }

    /**
     * Similar to {@link BaseMatcher#describeTo(Description)}
     * @param description the current description
     * @param operator the operator implemented by the subclasses
     */
    public void describeTo(Description description, String operator)
    {
        description.appendList("(", " " + operator + " ", ")", matchers);
    }
}
