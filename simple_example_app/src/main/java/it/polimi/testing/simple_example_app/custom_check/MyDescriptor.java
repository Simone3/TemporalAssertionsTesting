package it.polimi.testing.simple_example_app.custom_check;

import org.hamcrest.Matcher;

import it.polimi.testing.temporalassertions.core.AbstractEventDescriptor;
import it.polimi.testing.temporalassertions.core.Check;
import it.polimi.testing.temporalassertions.events.Event;

/**
 * A custom descriptor created inside the example app
 */
public class MyDescriptor extends AbstractEventDescriptor
{
    private MyDescriptor(Matcher<? extends Event> matcher)
    {
        super(matcher);
    }

    public static MyDescriptor myDescriptor()
    {
        return new MyDescriptor(null);
    }

    public Check myCheck()
    {
        return new MyCheck();
    }
}
