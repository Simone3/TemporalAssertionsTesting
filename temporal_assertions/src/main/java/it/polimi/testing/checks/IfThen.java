package it.polimi.testing.checks;

public class IfThen extends CheckConnective
{
    private Check ifCheck;
    private Check thenCheck;

    public IfThen(Check ifCheck, Check thenCheck)
    {
        super(ifCheck, thenCheck);
        this.ifCheck = ifCheck;
        this.thenCheck = thenCheck;
    }

    @Override
    ResultsSubscriber getResultsSubscriber()
    {
        return new ResultsSubscriber()
        {
            private Result ifResult;
            private Result thenResult;

            @Override
            boolean onNextResult(Check check, Result result)
            {
                // If it's the precondition...
                if(check==ifCheck)
                {
                    ifResult = result;

                    // If the precondition is false, no need to wait for the result of the actual check
                    if(Outcome.FAILURE.equals(ifResult.getOutcome()))
                    {
                        return false;
                    }
                }

                // If it's the actual check...
                else if(check==thenCheck)
                {
                    thenResult = result;
                }

                return true;
            }

            @Override
            Result getFinalResult()
            {
                if(Outcome.FAILURE.equals(ifResult.getOutcome()))
                {
                    return new Result(Outcome.SUCCESS, "The pre-condition didn't hold");
                }
                else
                {
                    return thenResult;
                }
            }
        };
    }
}
