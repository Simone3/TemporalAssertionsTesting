package it.polimi.testing.temporalassertions.checks;

import java.util.ArrayList;
import java.util.List;

import it.polimi.testing.temporalassertions.events.Event;
import rx.Subscriber;

public abstract class CheckConnective extends Check
{
    private final Check[] checks;
    private Subscriber<? super Result> finalResultChild;

    CheckConnective(Check... checks)
    {
        super(null);

        this.checks = checks;
    }

    @Override
    public CheckSubscriber getCheckSubscriber(final Subscriber<? super Result> finalResultChild)
    {
        this.finalResultChild = finalResultChild;
        final List<CheckSubscriber> singleTermSubscribers = new ArrayList<>();

        // This subscriber receives all results of the "child" checks: it implements the actual logic of the connective
        final Subscriber<ChildResult> resultsSubscriber = getResultsSubscriber();

        // Loop all "child" checks
        for(final Check check: checks)
        {
            // Each check has a child that will forward the result to the results subscriber
            Subscriber<? super Result> singleTermChild = new Subscriber<Result>()
            {
                private boolean sentResult = false;

                @Override
                public void onCompleted()
                {
                    // Make sure we always send a result, even if null
                    if(!sentResult && !resultsSubscriber.isUnsubscribed())
                    {
                        forwardResult(null);
                    }
                }

                @Override
                public void onError(Throwable e)
                {
                    // Make sure we always send a result, even if null
                    if(!sentResult && !resultsSubscriber.isUnsubscribed())
                    {
                        forwardResult(null);
                    }
                }

                @Override
                public void onNext(Result result)
                {
                    // Forward result to the results subscriber
                    if(!resultsSubscriber.isUnsubscribed())
                    {
                        forwardResult(result);
                        sentResult = true;
                    }
                }

                private void forwardResult(Result result)
                {
                    resultsSubscriber.onNext(new ChildResult(check, result));
                }
            };

            // Get the "child" check subscriber and add the child
            CheckSubscriber singleTermCheckSubscriber = check.getCheckSubscriber(singleTermChild);
            singleTermSubscribers.add(singleTermCheckSubscriber);
        }

        // Return a subscriber that will be attached to the main event stream and takes care of forwarding the received events to all "child" checks
        return new CheckSubscriber()
        {
            private boolean allTermsUnsubscribed;

            @Override
            public void onCompleted()
            {
                // Send the onCompleted to all the "child" subscribers
                for(CheckSubscriber singleTermSubscriber: singleTermSubscribers)
                {
                    if(!singleTermSubscriber.isUnsubscribed())
                    {
                        singleTermSubscriber.onCompleted();
                    }
                }
            }

            @Override
            public Result getFinalResult()
            {
                return null;
            }

            @Override
            public void onNext(Event event)
            {
                allTermsUnsubscribed = true;

                // Forward the events to each "child" subscriber
                for(CheckSubscriber singleTermSubscriber: singleTermSubscribers)
                {
                    if(!singleTermSubscriber.isUnsubscribed())
                    {
                        allTermsUnsubscribed = false;
                        singleTermSubscriber.onNext(event);
                    }
                }

                // If all "child" subscribers already unsubscribed on their own, no need to receive events anymore
                if(allTermsUnsubscribed)
                {
                    unsubscribe();
                }
            }
        };
    }

    abstract ResultsSubscriber getResultsSubscriber();

    abstract class ResultsSubscriber extends Subscriber<ChildResult>
    {
        private int resultsToBeReceived = checks.length;

        @Override
        public void onCompleted()
        {
            // Send the (single) final result of the connective to the main child
            if(!finalResultChild.isUnsubscribed())
            {
                finalResultChild.onNext(getFinalResult());
                finalResultChild.onCompleted();
            }

            // This is needed!
            unsubscribe();
        }

        @Override
        public void onError(Throwable e)
        {
            if(!finalResultChild.isUnsubscribed())
            {
                finalResultChild.onError(e);
            }
        }

        @Override
        public void onNext(ChildResult childResult)
        {
            resultsToBeReceived--;

            // Allow subclasses to implement their connective logic
            boolean continueStream = onNextResult(childResult.child, childResult.result);

            // Since onCompleted is never called by the "child" checks we need to check the condition here
            if(!continueStream || resultsToBeReceived<=0)
            {
                onCompleted();
            }
        }

        // Return is true if we can stop, false if we continue
        abstract boolean onNextResult(Check check, Result result);

        abstract Result getFinalResult();
    }

    private static class ChildResult
    {
        private Check child;
        private Result result;

        private ChildResult(Check child, Result result)
        {
            this.child = child;
            this.result = result;
        }
    }
}
