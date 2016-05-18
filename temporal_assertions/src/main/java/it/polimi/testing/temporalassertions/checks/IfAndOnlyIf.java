package it.polimi.testing.temporalassertions.checks;

public class IfAndOnlyIf extends CheckConnective
{
    public IfAndOnlyIf(Check firstCheck, Check secondCheck)
    {
        super(firstCheck, secondCheck);
    }

    @Override
    ResultsSubscriber getResultsSubscriber()
    {
        return new ResultsSubscriber()
        {
            private Result[] results = new Result[2];
            private int i = 0;

            @Override
            boolean onNextResult(Check check, Result result)
            {
                results[i] = result;
                i++;

                return true;
            }

            @Override
            Result getFinalResult()
            {
                boolean firstIsFailure = Outcome.FAILURE.equals(results[0].getOutcome());
                boolean secondIsFailure = Outcome.FAILURE.equals(results[1].getOutcome());
                if(firstIsFailure && secondIsFailure)
                {
                    return new Result(Outcome.SUCCESS, "Both conditions didn't hold");
                }
                else if(!firstIsFailure && !secondIsFailure)
                {
                    return new Result(Outcome.SUCCESS, "Both conditions held");
                }
                else
                {
                    if(firstIsFailure)
                    {
                        return new Result(Outcome.FAILURE, "First didn't hold but second did");
                    }
                    else
                    {
                        return new Result(Outcome.FAILURE, "First held but second didn't");
                    }
                }
            }
        };
    }
}
