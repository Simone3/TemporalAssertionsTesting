package it.polimi.testing.checks;

public class AllHold extends CheckConnective
{
    private AllHold(Check... checks)
    {
        super(checks);
    }

    public static AllHold allHold(Check... checks)
    {
        return new AllHold(checks);
    }

    @Override
    protected ResultsSubscriber getResultsSubscriber()
    {
        return new ResultsSubscriber()
        {
            private boolean oneFailed = false;

            @Override
            Result getFinalResult()
            {
                // Failure if one of the checks failed
                if(oneFailed)
                {
                    return new Result(Outcome.FAILURE, "One failed!");
                }
                else
                {
                    return new Result(Outcome.SUCCESS, "Ok!");
                }
            }

            @Override
            boolean onNextResult(Check check, Result result)
            {
                // If one fails, exit
                if(Outcome.FAILURE.equals(result.getOutcome()))
                {
                    oneFailed = true;
                    return false;
                }

                return true;
            }
        };
    }
}