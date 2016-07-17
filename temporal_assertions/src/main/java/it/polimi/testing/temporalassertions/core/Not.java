package it.polimi.testing.temporalassertions.core;

/**
 * Connective that allows to invert a check result: it changes the internal check outcome in the following way:
 * - SUCCESS -> FAILURE
 * - WARNING -> WARNING
 * - FAILURE -> SUCCESS
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
        super("NOT TRUE THAT ("+check+")", check);
    }

    /**
     * Connective that allows to invert a check result: it changes the internal check outcome in the following way:
     * - SUCCESS -> FAILURE
     * - WARNING -> WARNING
     * - FAILURE -> SUCCESS
     * @param check the check to be inverted
     * @return the inverted check: !C
     */
    public static Check isNotSatisfied(Check check)
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
                // Invert the outcome
                Outcome outcome;
                switch(received.getOutcome())
                {
                    case FAILURE:
                        outcome = Outcome.SUCCESS;
                        break;
                    case SUCCESS:
                        outcome = Outcome.FAILURE;
                        break;
                    default:
                        outcome = Outcome.WARNING;
                }
                return new Result(outcome, received.getReport());
            }
        };
    }
}
