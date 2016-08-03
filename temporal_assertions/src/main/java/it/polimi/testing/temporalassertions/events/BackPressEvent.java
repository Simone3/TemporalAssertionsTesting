package it.polimi.testing.temporalassertions.events;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.anything;

/**
 * An event representing a click on the "Back" button
 */
public class BackPressEvent extends Event
{
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "{Back pressed}";
    }

    /**
     * Constructor
     * @return the Hamcrest matcher
     */
    public static Matcher<BackPressEvent> isBackPressed()
    {
        return new FeatureMatcher<BackPressEvent, Void>(anything(""), "is back pressed", "")
        {
            @Override
            protected Void featureValueOf(final BackPressEvent actual)
            {
                return null;
            }
        };
    }
}
