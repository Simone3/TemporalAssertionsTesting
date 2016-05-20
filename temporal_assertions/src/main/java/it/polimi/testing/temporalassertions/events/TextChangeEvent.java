package it.polimi.testing.temporalassertions.events;

import android.view.View;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.equalTo;

/**
 * An event representing a text change
 */
public class TextChangeEvent extends Event
{
    private final View view;
    private final String text;

    /**
     * Constructor
     * @param view the view that contains the text
     * @param text the new text in the view
     */
    public TextChangeEvent(View view, String text)
    {
        this.view = view;
        this.text = text;
    }

    /**
     * Getter
     * @return the view that contains the text
     */
    public View getView()
    {
        return view;
    }

    /**
     * Getter
     * @return the new text in the view
     */
    public String getText()
    {
        return text;
    }

    @Override
    public String toString()
    {
        return "TC '"+text+"' from "+view;
    }

    /**
     * Hamcrest matcher that matches a text change event with the given text and from the given view
     * @param view the view that created the text change event
     * @param matcher the description of the text linked with the event
     * @return the Hamcrest matcher
     */
    public static Matcher<TextChangeEvent> isTextChangeEvent(final View view, final Matcher<String> matcher)
    {
        return both(isTextChangeEventFrom(view)).and(isTextChangeEventWhereTextMatches(matcher));
    }

    /**
     * Hamcrest matcher that matches a text change event with any text from the given view
     * @param view the view that created the text change event
     * @return the Hamcrest matcher
     */
    public static Matcher<TextChangeEvent> isTextChangeEventFrom(final View view)
    {
        return new FeatureMatcher<TextChangeEvent, View>(equalTo(view), "is a text change event whose view", "view")
        {
            @Override
            protected View featureValueOf(final TextChangeEvent actual)
            {
                return actual.getView();
            }
        };
    }

    /**
     * Hamcrest matcher that matches a text change event with the given text from any view
     * @param matcher the description of the text linked with the event
     * @return the Hamcrest matcher
     */
    public static Matcher<TextChangeEvent> isTextChangeEventWhereTextMatches(final Matcher<String> matcher)
    {
        return new FeatureMatcher<TextChangeEvent, String>(matcher, "is a text change event whose text", "text")
        {
            @Override
            protected String featureValueOf(final TextChangeEvent actual)
            {
                return actual.getText();
            }
        };
    }

    /**
     * Hamcrest matcher that matches any text change event (i.e. any text and any view)
     * @return the Hamcrest matcher
     */
    public static Matcher<TextChangeEvent> isTextChangeEvent()
    {
        return new FeatureMatcher<TextChangeEvent, String>(anything(), "is a text change event", "")
        {
            @Override
            protected String featureValueOf(final TextChangeEvent actual)
            {
                return "";
            }
        };
    }
}
