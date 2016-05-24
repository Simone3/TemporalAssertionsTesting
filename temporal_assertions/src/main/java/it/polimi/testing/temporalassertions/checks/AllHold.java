package it.polimi.testing.temporalassertions.checks;

import it.polimi.testing.temporalassertions.Utils;

/**
 * Connective that allows to express a logic AND between two or more checks: it returns SUCCESS only if all
 * internal checks do not fail (i.e. success or warning)
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
        super("("+Utils.join(") AND (", checks)+")", checks);
    }

    /**
     * Expresses a logic AND between two or more checks: it will return SUCCESS only if all
     * internal checks do not fail (i.e. success or warning)
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
            private String reportsListForSuccess = "";
            private String failedReport;

            @Override
            Result getFinalResult()
            {
                // Failure if one of the checks failed
                if(oneFailed)
                {
                    return new Result(Outcome.FAILURE, failedReport);
                }
                else
                {
                    return new Result(Outcome.SUCCESS, reportsListForSuccess);
                }
            }

            @Override
            boolean onNextResult(Check check, Result result)
            {
                // If one fails, short-circuit
                if(Outcome.FAILURE.equals(result.getOutcome()))
                {
                    oneFailed = true;
                    failedReport = result.getReport();
                    return false;
                }

                reportsListForSuccess += ("".equals(reportsListForSuccess) ? "" : "; ")+result.getReport();

                return true;
            }
        };
    }
}