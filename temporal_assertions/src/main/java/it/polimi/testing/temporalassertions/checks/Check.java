package it.polimi.testing.temporalassertions.checks;


import rx.Subscriber;

public class Check
{
    private CheckSubscriber checkSubscriber;

    public Check(CheckSubscriber checkSubscriber)
    {
        this.checkSubscriber = checkSubscriber;
    }

    public CheckSubscriber getCheckSubscriber(final Subscriber<? super Result> child)
    {
        checkSubscriber.setChild(child);
        return checkSubscriber;
    }
}
