package it.polimi.testing.temporalassertions.checks;

/**
 * Connective that allows to invert a check result: it returns SUCCESS only if the
 * internal check fails
 *
 * !C
 */
public class Not extends CheckConnective
{
    /**
     * Constructor
     * @param check the check to be inverted
     */
    private Not(Check check)
    {
        super(check);
    }

    /**
     * Connective that allows to invert a check result: it returns SUCCESS only if the
     * internal check fails
     * @param check the check to be inverted
     * @return the inverted check: !C
     */
    public static Not notTrueThat(Check check)
    {
        return new Not(check);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    ResultsSubscriber getResultsSubscriber()
    {
        return new ResultsSubscriber()
        {
            private Result received;

            @Override
            boolean onNextResult(Check check, Result result)
            {
                received = result;
                return false;
            }

            @Override
            Result getFinalResult()
            {
                // Simply invert the outcome
                Outcome outcome = Outcome.FAILURE.equals(received.getOutcome()) ? Outcome.SUCCESS : Outcome.FAILURE;
                return new Result(outcome, received.getMessage());
            }
        };
    }
}
