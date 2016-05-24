package it.polimi.testing.temporalassertions.checks;

import it.polimi.testing.temporalassertions.Utils;

/**
 * Connective that allows to express a logic OR between two or more checks: it returns SUCCESS if at least
 * one internal check does not fail (i.e. success or warning)
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
        super("("+Utils.join(") OR (", checks)+")", checks);
    }

    /**
     * Expresses a logic OR between two or more checks: it will return SUCCESS if at least
     * one internal check does not fail (i.e. success or warning)
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
            private String reportsListForFailure = "";
            private String succeededReport;

            @Override
            Result getFinalResult()
            {
                // Success if one of the checks succeeded
                if(oneSucceeded)
                {
                    return new Result(Outcome.SUCCESS, succeededReport);
                }
                else
                {
                    return new Result(Outcome.FAILURE, reportsListForFailure);
                }
            }

            @Override
            boolean onNextResult(Check check, Result result)
            {
                // If one succeeds, short-circuit
                if(!Outcome.FAILURE.equals(result.getOutcome()))
                {
                    oneSucceeded = true;
                    succeededReport = result.getReport();
                    return false;
                }

                reportsListForFailure += ("".equals(reportsListForFailure) ? "" : "; ")+result.getReport();

                return true;
            }
        };
    }
}