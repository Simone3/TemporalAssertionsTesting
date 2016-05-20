package it.polimi.testing.temporalassertions.checks;

/**
 * Connective that allows to express a material conditional (single implication) between two checks:
 * it evaluates the second check only if the first one does not fail
 *
 * C1 => C2
 */
public class IfThen extends CheckConnective
{
    private Check ifCheck;
    private Check thenCheck;

    /**
     * Constructor
     * @param ifCheck the left term of the implication
     * @param thenCheck the right term of the implication
     */
    public IfThen(Check ifCheck, Check thenCheck)
    {
        super(ifCheck, thenCheck);
        this.ifCheck = ifCheck;
        this.thenCheck = thenCheck;
    }

    /**
     * {@inheritDoc}
     */
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
                // Success if the precondition fails, independently of the actual check outcome
                if(Outcome.FAILURE.equals(ifResult.getOutcome()))
                {
                    return new Result(Outcome.SUCCESS, "The pre-condition didn't hold");
                }

                // If the precondition didn't fail, then return the result of the actual check
                else
                {
                    return thenResult;
                }
            }
        };
    }
}
