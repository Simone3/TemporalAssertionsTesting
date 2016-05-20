package it.polimi.testing.temporalassertions.descriptors;


import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;

import it.polimi.testing.temporalassertions.events.Event;
import it.polimi.testing.temporalassertions.events.GenericEvent;
import it.polimi.testing.temporalassertions.events.TextChangeEvent;

import static it.polimi.testing.temporalassertions.events.TextChangeEvent.isTextChangeEvent;
import static it.polimi.testing.temporalassertions.events.TextChangeEvent.isTextChangeEventFrom;
import static it.polimi.testing.temporalassertions.events.TextChangeEvent.isTextChangeEventWhereTextMatches;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringStartsWith.startsWith;

public class MatchersUnitTest
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





    private static String getDescription(String reason, Matcher<?> matcher, Event actual)
    {
        Description description = new StringDescription();
        description.appendText(reason)
                .appendText("\nExpected: ")
                .appendDescriptionOf(matcher)
                .appendText("\n     but: ");
        matcher.describeMismatch(actual, description);

        return description.toString();
    }
}
