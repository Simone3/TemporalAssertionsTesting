package it.polimi.testing.temporalassertions.checks;

/**
 * Connective that allows to express a logic AND between two or more checks: it returns SUCCESS only if all
 * internal checks do not fail
 *
 * C1 && C2 && ...
 */
public class AllHold extends CheckConnective
{
    /**
     * Constructor
     * @param checks the internal checks
     */
    private AllHold(Check... checks)
    {
        super(checks);
    }

    /**
     * Expresses a logic AND between two or more checks: it will return SUCCESS only if all
     * internal checks do not fail
     * @param checks the internal checks
     * @return the logic AND check: C1 && C2 && ...
     */
    public static AllHold allHold(Check... checks)
    {
        return new AllHold(checks);
    }

    /**
     * {@inheritDoc}
     */
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
                // If one fails, short-circuit
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