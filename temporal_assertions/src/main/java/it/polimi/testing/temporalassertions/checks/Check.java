package it.polimi.testing.temporalassertions.checks;

import rx.Subscriber;

/**
 * Basic consistency check for the event stream
 *
 * It allows to express a condition (e.g. "an event X exists") on the stream it is applied to
 */
public class Check
{
    private CheckSubscriber checkSubscriber;

    /**
     * Constructor
     * @param checkSubscriber the subscriber that implements the check logic
     */
    public Check(CheckSubscriber checkSubscriber)
    {
        this.checkSubscriber = checkSubscriber;
    }

    /**
     * Sets the given child to the check subscriber and returns it
     * @param child the subscriber that will receive the result of the check
     * @return the subscriber that implements the check logic
     */
    public CheckSubscriber getCheckSubscriber(final Subscriber<? super Result> child)
    {
        checkSubscriber.setChild(child);
        return checkSubscriber;
    }
}
