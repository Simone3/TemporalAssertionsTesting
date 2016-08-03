package it.polimi.testing.temporalassertions.events;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.anything;

/**
 * An event representing a toast message display
 */
public class ToastEvent extends Event
{
    private String text;

    /**
     * Constructor
     * @param text the displayed text
     */
    public ToastEvent(String text)
    {
        this.text = text;
    }

    /**
     * Getter
     * @return the displayed text
     */
    public String getText()
    {
        return text;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "{Display toast '"+text+"'}";
    }

    /**
     * Hamcrest matcher that matches any toast being displayed
     * @return the Hamcrest matcher
     */
    public static Matcher<ToastEvent> isToastDisplay()
    {
        return new FeatureMatcher<ToastEvent, Void>(anything(""), "is any toast display", "")
        {
            @Override
            protected Void featureValueOf(final ToastEvent actual)
            {
                return null;
            }
        };
    }

    /**
     * Hamcrest matcher that matches a displayed toast whose text matches the given matcher
     * @param textMatcher the matcher for the toast text
     * @return the Hamcrest matcher
     */
    public static Matcher<ToastEvent> isToastDisplay(Matcher<String> textMatcher)
    {
        return new FeatureMatcher<ToastEvent, String>(is(textMatcher), "is display of toast with text that "+textMatcher, "")
        {
            @Override
            protected String featureValueOf(final ToastEvent actual)
            {
                return actual.text;
            }
        };
    }
}
