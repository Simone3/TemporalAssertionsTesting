package it.polimi.testing.temporalassertions.events;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import java.util.Arrays;
import java.util.List;

import it.polimi.testing.temporalassertions.Utils;

import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.describedAs;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Every.everyItem;

/**
 * A generic event, useful for custom events
 */
public class GenericEvent extends Event
{
    private final Object[] objects;

    /**
     * Constructor
     * @param objects the parameters (anything)
     */
    public GenericEvent(Object... objects)
    {
        this.objects = objects;
    }

    /**
     * Getter
     * @return the parameters (anything)
     */
    public Object[] getObjects()
    {
        return objects;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "{Generic with objects "+Utils.arrayToString(objects)+"}";
    }



    /**
     * Hamcrest matcher that matches a generic event with the given object in order
     * @param objects the objects contained in the event
     * @return the Hamcrest matcher
     */
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

    /**
     * Hamcrest matcher that matches a generic event with all the objects matching {@code matcher}
     * @param matcher the matcher that matches every object contained in the event
     * @return the Hamcrest matcher
     */
    public static Matcher<GenericEvent> isGenericEventWithObjectsThatMatch(final Matcher<Object> matcher)
    {
        return new FeatureMatcher<GenericEvent, List<Object>>(describedAs("'"+matcher+"'", everyItem(matcher)), "is a generic event with objects that match", "objects")
        {
            @Override
            protected List<Object> featureValueOf(final GenericEvent actual)
            {
                return Arrays.asList(actual.getObjects());
            }
        };
    }

    /**
     * Hamcrest matcher that matches any generic event (any objects)
     * @return the Hamcrest matcher
     */
    public static Matcher<GenericEvent> isGenericEvent()
    {
        return new FeatureMatcher<GenericEvent, Void>(anything(""), "is any generic event", "")
        {
            @Override
            protected Void featureValueOf(final GenericEvent actual)
            {
                return null;
            }
        };
    }
}
