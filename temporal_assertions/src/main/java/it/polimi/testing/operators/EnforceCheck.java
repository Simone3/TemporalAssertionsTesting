package it.polimi.testing.operators;

import it.polimi.testing.checks.Check;
import it.polimi.testing.checks.Result;
import it.polimi.testing.temporalassertions.events.Event;
import rx.Observable;
import rx.Subscriber;


public class EnforceCheck<T extends Event> implements Observable.Operator<Result, T>
{
    final Check check;

    public EnforceCheck(Check check)
    {
        this.check = check;
    }

    @Override
    public Subscriber<? super T> call(final Subscriber<? super Result> child)
    {
        return check.getCheckSubscriber(child);
    }
}