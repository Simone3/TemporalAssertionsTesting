package it.polimi.testing.temporalassertions.quantifiers;

import org.hamcrest.Matcher;

import it.polimi.testing.temporalassertions.descriptors.EventsWhereEach;
import it.polimi.testing.temporalassertions.events.Event;

/**
 * An abstract quantifier. The implementations express some condition on the number of events.
 */
public abstract class AbstractQuantifier
{
    private int counter;
    private final int desiredBound;

    /**
     * Constructor
     * @param n the value of the quantifier
     */
    AbstractQuantifier(int n)
    {
        this.desiredBound = n;
    }

    /**
     * Allows to describe the given number of events in the sequence that match the given Hamcrest matcher to
     * express some condition on them
     * @param matcher the Hamcrest matcher that matches the events in the sequence
     * @return the event descriptor
     */
    public EventsWhereEach eventsWhereEach(Matcher<? extends Event> matcher)
    {
        return new EventsWhereEach(this, matcher);
    }

    /**
     * Simply +1 the internal counter
     */
    public void increaseCounter()
    {
        counter++;
    }

    /**
     * Simply sets to 0 the internal counter
     */
    public void resetCounter()
    {
        counter = 0;
    }

    /**
     * Allows the implementations to build the quantifier logic
     * @return true if the condition of the quantifier is met given the current counter value
     */
    public abstract boolean isConditionMet();

    /**
     * Allows the implementations to short-circuit the computation in case the condition can never be reached
     * @return true if we can short-circuit the computation
     */
    public abstract boolean canStopCurrentComputation();

    /**
     * Allows the implementations to describe in a string their meaning
     * @return a string explaining the quantifier
     */
    public abstract String getDescription();

    /**
     * Allows the implementations to describe the error when {@link AbstractQuantifier#isConditionMet()}
     * returns false
     * @return a string explaining why the condition is not met
     */
    public abstract String describeError();

    /**
     * Getter
     * @return the value of the quantifier
     */
    int getDesiredBound()
    {
        return desiredBound;
    }

    /**
     * Getter
     * @return the current counter value
     */
    public int getCounter()
    {
        return counter;
    }
}
