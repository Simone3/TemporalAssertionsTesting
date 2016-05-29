package it.polimi.testing.temporalassertions.core;

import java.util.ArrayList;
import java.util.List;

import it.polimi.testing.temporalassertions.events.Event;
import rx.Subscriber;

/**
 * A check connective is a check that contains other checks, i.e. it allows to "combine" several
 * checks and then return a single final result
 */
public abstract class CheckConnective extends Check
{
    private final Check[] checks;
    private Subscriber<? super Result> finalResultChild;

    /**
     * Constructor
     * @param description a description of the check
     * @param checks the checks contained in this connective
     */
    CheckConnective(String description, Check... checks)
    {
        super(description, null);

        this.checks = checks;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected CheckSubscriber getCheckSubscriber(final Subscriber<? super Result> finalResultChild)
    {
        this.finalResultChild = finalResultChild;
        final List<CheckSubscriber> singleTermSubscribers = new ArrayList<>();

        // This subscriber receives all results of the internal checks: it implements the actual logic of the connective
        final Subscriber<ChildResult> resultsSubscriber = getResultsSubscriber();

        // Loop all internal checks
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

                /**
                 * Simply sends the result to the results subscriber
                 * @param result the result
                 */
                private void forwardResult(Result result)
                {
                    resultsSubscriber.onNext(new ChildResult(check, result));
                }
            };

            // Get the internal check subscriber and add the child
            CheckSubscriber singleTermCheckSubscriber = check.getCheckSubscriber(singleTermChild);
            singleTermSubscribers.add(singleTermCheckSubscriber);
        }

        // Return a subscriber that will be attached to the main event stream and takes care of forwarding the received events to all internal checks
        return new CheckSubscriber()
        {
            private boolean allTermsUnsubscribed;

            @Override
            public void onCompleted()
            {
                // Send the onCompleted to all the internal subscribers
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

                // Forward the events to each internal subscriber
                for(CheckSubscriber singleTermSubscriber: singleTermSubscribers)
                {
                    if(!singleTermSubscriber.isUnsubscribed())
                    {
                        allTermsUnsubscribed = false;
                        singleTermSubscriber.onNext(event);
                    }
                }

                // If all internal subscribers already unsubscribed on their own, no need to receive events anymore
                if(allTermsUnsubscribed)
                {
                    unsubscribe();
                }
            }
        };
    }

    /**
     * Getter
     * @return the subscriber that receives all results of the internal checks
     */
    abstract ResultsSubscriber getResultsSubscriber();

    /**
     * The subscriber that receives all results of the internal checks, i.e. the actual implementation
     * of the connective logic
     */
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
            // Forward error to main child
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
            boolean continueStream = onNextResult(childResult.check, childResult.result);

            // Since onCompleted is never called by the internal checks we need to check the condition here
            if(!continueStream || resultsToBeReceived<=0)
            {
                onCompleted();
            }
        }

        /**
         * Allows the implementations to receive the result of an internal check
         * @param check the internal check related to the result
         * @param result the result of the internal check
         * @return true if we need to continue the stream of results, false if we stop (i.e. short-circuit the connective)
         */
        abstract boolean onNextResult(Check check, Result result);

        /**
         * Allows to build the single final result of the connective.
         * This is called either after onNextResult returns false or when all results of the internal checks
         * have been received.
         * @return the single final result of the connective
         */
        abstract Result getFinalResult();
    }

    /**
     * Internal class to hold both a child and a result
     */
    private static class ChildResult
    {
        private final Check check;
        private final Result result;

        private ChildResult(Check check, Result result)
        {
            this.check = check;
            this.result = result;
        }
    }
}
