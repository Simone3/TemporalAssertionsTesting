package it.polimi.testing.checks;

import it.polimi.testing.temporalassertions.events.Event;
import rx.Subscriber;

public abstract class CheckSubscriber extends Subscriber<Event>
{
    private Subscriber<? super Result> child;

    @Override
    public void onError(Throwable e)
    {
        if(!child.isUnsubscribed())
        {
            child.onError(e);
        }
    }

    @Override
    public void onCompleted()
    {
        if(!child.isUnsubscribed())
        {
            Result result = getFinalResult();
            child.onNext(result);
            child.onCompleted();
        }
    }

    public void endCheck()
    {
        onCompleted();
        unsubscribe();
    }

    public abstract Result getFinalResult();

    public void setChild(Subscriber<? super Result> child)
    {
        this.child = child;
    }

    Subscriber<? super Result> getChild()
    {
        return child;
    }
}
