package it.polimi.testing.temporalassertions.core;

import it.polimi.testing.temporalassertions.events.Event;
import rx.Observable;
import rx.Subscriber;

/**
 * Operator that allows to apply a consistency check on a stream of events, i.e. transforms an observable
 * of events into an observable that will fire one result
 * @param <T> the class of the event
 */
class EnforceCheck<T extends Event> implements Observable.Operator<Result, T>
{
    private final Check check;

    /**
     * Constructor
     * @param check the check to be enforced
     */
    public EnforceCheck(Check check)
    {
        this.check = check;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Subscriber<? super T> call(final Subscriber<? super Result> child)
    {
        // Each check has its own subscriber that implements the logic and is in charge of returning the result
        return check.getCheckSubscriber(child);
    }
}