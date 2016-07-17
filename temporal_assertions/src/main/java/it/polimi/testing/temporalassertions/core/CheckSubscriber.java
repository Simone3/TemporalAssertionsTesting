package it.polimi.testing.temporalassertions.core;

import android.support.annotation.NonNull;

import it.polimi.testing.temporalassertions.events.Event;
import rx.Subscriber;

/**
 * A subscriber that allows to implement the logic of a check
 *
 * It loops all events of the stream and sends the result to a child subscriber
 */
public abstract class CheckSubscriber extends Subscriber<Event>
{
    private Subscriber<? super Result> child;

    /**
     * {@inheritDoc}
     */
    protected CheckSubscriber()
    {
        super();
    }

    /**
     * {@inheritDoc}
     */
    protected CheckSubscriber(Subscriber<?> subscriber)
    {
        super(subscriber);
    }

    /**
     * {@inheritDoc}
     */
    protected CheckSubscriber(Subscriber<?> subscriber, boolean shareSubscriptions)
    {
        super(subscriber, shareSubscriptions);
    }

    @Override
    public void onError(Throwable e)
    {
        // Forward error to the child
        if(!child.isUnsubscribed())
        {
            child.onError(e);
        }
    }

    @Override
    public void onCompleted()
    {
        // Send result to the child
        if(!child.isUnsubscribed())
        {
            Result result = getFinalResult();
            child.onNext(result);
            child.onCompleted();
        }
    }

    /**
     * Allows to short-circuit a check
     */
    public void endCheck()
    {
        onCompleted();
        unsubscribe();
    }

    /**
     * Allows to build the final result of the check, based on its logic
     * @return the single final result of the check
     */
    public abstract @NonNull Result getFinalResult();

    /**
     * Allows the caller to set the child that will receive the check result
     * @param child a subscriber of results
     */
    public void setChild(Subscriber<? super Result> child)
    {
        this.child = child;
    }
}
