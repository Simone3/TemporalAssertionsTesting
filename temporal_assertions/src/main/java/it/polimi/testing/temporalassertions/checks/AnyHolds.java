package it.polimi.testing.temporalassertions.checks;

/**
 * Connective that allows to express a logic OR between two or more checks: it returns SUCCESS if at least
 * one internal check does not fail
 *
 * C1 || C2 || ...
 */
public class AnyHolds extends CheckConnective
{
    /**
     * Constructor
     * @param checks the internal checks
     */
    private AnyHolds(Check... checks)
    {
        super(checks);
    }

    /**
     * Expresses a logic OR between two or more checks: it will return SUCCESS if at least
     * one internal check does not fail
     * @param checks the internal checks
     * @return the logic OR check: C1 || C2 || ...
     */
    public static AnyHolds anyHolds(Check... checks)
    {
        return new AnyHolds(checks);
    }

    /**
     * {@inheritDoc}
     */
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
                // If one succeeds, short-circuit
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