package it.polimi.testing.temporalassertions.operators;

import it.polimi.testing.temporalassertions.checks.Check;
import it.polimi.testing.temporalassertions.checks.Result;
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