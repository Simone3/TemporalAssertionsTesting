package it.polimi.testing.temporalassertions.events;

import android.view.View;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import it.polimi.testing.temporalassertions.Utils;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.describedAs;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "{Text change '"+text+"' from "+Utils.describeView(view)+"}";
    }

    /**
     * Hamcrest matcher that matches a text change event with the given text and from the given view
     * @param view the view that created the text change event
     * @param matcher the description of the text linked with the event
     * @return the Hamcrest matcher
     */
    public static Matcher<TextChangeEvent> isTextChange(final View view, final Matcher<String> matcher)
    {
        return both(isTextChangeFrom(view)).and(isTextChangeWhereTextMatches(matcher));
    }

    /**
     * Hamcrest matcher that matches a text change event with any text from the given view
     * @param view the view that created the text change event
     * @return the Hamcrest matcher
     */
    public static Matcher<TextChangeEvent> isTextChangeFrom(final View view)
    {
        return new FeatureMatcher<TextChangeEvent, View>(describedAs(Utils.describeView(view), equalTo(view)), "is a text change event from", "view")
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
    public static Matcher<TextChangeEvent> isTextChangeWhereTextMatches(final Matcher<String> matcher)
    {
        return new FeatureMatcher<TextChangeEvent, String>(matcher, "is a text change event with text that is", "text")
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
    public static Matcher<TextChangeEvent> isTextChange()
    {
        return new FeatureMatcher<TextChangeEvent, Void>(anything(""), "is any text change event", "")
        {
            @Override
            protected Void featureValueOf(final TextChangeEvent actual)
            {
                return null;
            }
        };
    }
}
