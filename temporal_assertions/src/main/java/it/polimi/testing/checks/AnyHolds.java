package it.polimi.testing.checks;

public class AnyHolds extends CheckConnective
{
    private AnyHolds(Check... checks)
    {
        super(checks);
    }

    public static AnyHolds anyHolds(Check... checks)
    {
        return new AnyHolds(checks);
    }

    @Override
    protected ResultsSubscriber getResultsSubscriber()
    {
        return new ResultsSubscriber()
        {
            private boolean oneSucceeded = false;

            @Override
            Result getFinalResult()
            {
                // Success if one of the checks succeeded
                if(oneSucceeded)
                {
                    return new Result(Outcome.SUCCESS, "One succeeded!");
                }
                else
                {
                    return new Result(Outcome.FAILURE, "All failed!");
                }
            }

            @Override
            boolean onNextResult(Check check, Result result)
            {
                // If one succeeds, exit
                if(!Outcome.FAILURE.equals(result.getOutcome()))
                {
                    oneSucceeded = true;
                    return false;
                }

                return true;
            }
        };
    }
}