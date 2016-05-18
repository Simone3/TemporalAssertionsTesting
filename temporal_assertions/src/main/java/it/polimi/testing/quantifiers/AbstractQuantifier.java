package it.polimi.testing.quantifiers;

import org.hamcrest.Matcher;

import it.polimi.testing.matchers.EventsWhereEach;
import it.polimi.testing.temporalassertions.events.Event;


public abstract class AbstractQuantifier
{
    private int counter;
    private final int desiredBound;

    AbstractQuantifier(int n)
    {
        this.desiredBound = n;
    }

    public EventsWhereEach eventsWhereEach(Matcher<? extends Event> matcher)
    {
        return new EventsWhereEach(this, matcher);
    }

    public void increaseCounter()
    {
        counter++;
    }

    public void resetCounter()
    {
        counter = 0;
    }

    public abstract boolean isConditionMet();

    public abstract boolean canStopCurrentComputation();

    public abstract String getDescription();

    public abstract String describeError();

    int getDesiredBound()
    {
        return desiredBound;
    }

    public int getCounter()
    {
        return counter;
    }
}
