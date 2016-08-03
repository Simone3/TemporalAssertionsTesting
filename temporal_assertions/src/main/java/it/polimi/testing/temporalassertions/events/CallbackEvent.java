package it.polimi.testing.temporalassertions.events;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.equalTo;

/**
 * A custom event that represents a Java callback
 */
public class CallbackEvent extends Event
{
    private final String callbackName;

    /**
     * Constructor
     * @param callbackName the name of the called method
     */
    public CallbackEvent(String callbackName)
    {
        this.callbackName = callbackName;
    }

    /**
     * Getter
     * @return the name of the called method
     */
    public String getCallbackName()
    {
        return callbackName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "{Callback '"+callbackName+"'}";
    }

    /**
     * Hamcrest matcher that matches a callback event with the given name
     * @param callbackName the name of the callback
     * @return the Hamcrest matcher
     */
    public static Matcher<CallbackEvent> isCallbackEvent(String callbackName)
    {
        return new FeatureMatcher<CallbackEvent, String>(equalTo(callbackName), "is the callback event", "callback name")
        {
            @Override
            protected String featureValueOf(final CallbackEvent actual)
            {
                return actual.getCallbackName();
            }
        };
    }

    /**
     * Hamcrest matcher that matches any callback event (any name)
     * @return the Hamcrest matcher
     */
    public static Matcher<CallbackEvent> isCallbackEvent()
    {
        return new FeatureMatcher<CallbackEvent, Void>(anything(""), "is any callback event", "")
        {
            @Override
            protected Void featureValueOf(final CallbackEvent actual)
            {
                return null;
            }
        };
    }
}
