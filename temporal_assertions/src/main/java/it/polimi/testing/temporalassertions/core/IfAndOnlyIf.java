package it.polimi.testing.temporalassertions.core;

/**
 * Connective that allows to express a logical biconditional (double implication) between two checks:
 * it returns SUCCESS only if both fail or both do not fail (i.e. success or warning)
 *
 * C1 <==> C2
 */
class IfAndOnlyIf extends CheckConnective
{
    /**
     * Constructor
     * @param firstCheck a check in the double implication
     * @param secondCheck a check in the double implication
     */
    IfAndOnlyIf(Check firstCheck, Check secondCheck)
    {
        super("("+firstCheck+") IF AND ONLY IF ("+secondCheck+")", firstCheck, secondCheck);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    ResultsSubscriber getResultsSubscriber()
    {
        return new ResultsSubscriber()
        {
            private final Result[] results = new Result[2];
            private int i = 0;

            @Override
            boolean onNextResult(Check check, Result result)
            {
                // Simply store the two results in the field
                results[i] = result;
                i++;

                return true;
            }

            @Override
            Result getFinalResult()
            {
                // Get status of the two checks
                boolean firstIsFailure = Outcome.FAILURE.equals(results[0].getOutcome());
                boolean secondIsFailure = Outcome.FAILURE.equals(results[1].getOutcome());

                // Success if both failed
                if(firstIsFailure && secondIsFailure)
                {
                    return new Result(Outcome.SUCCESS, "Both conditions didn't hold: "+results[0].getReport()+"; "+results[1].getReport());
                }

                // Success if both didn't fail
                else if(!firstIsFailure && !secondIsFailure)
                {
                    return new Result(Outcome.SUCCESS, "Both conditions held: "+results[0].getReport()+"; "+results[1].getReport());
                }

                // Failure if one fails and the other does not
                else
                {
                    if(firstIsFailure)
                    {
                        return new Result(Outcome.FAILURE, "We have that '"+results[1].getReport()+"' but '"+results[0].getReport()+"'");
                    }
                    else
                    {
                        return new Result(Outcome.FAILURE, "We have that '"+results[0].getReport()+"' but '"+results[1].getReport()+"'");
                    }
                }
            }
        };
    }
}
