package it.polimi.testing.temporalassertions.checks;

public class Not extends CheckConnective
{
    private Not(Check check)
    {
        super(check);
    }

    public static Not notTrueThat(Check check)
    {
        return new Not(check);
    }

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
                Outcome outcome = Outcome.FAILURE.equals(received.getOutcome()) ? Outcome.SUCCESS : Outcome.FAILURE;
                return new Result(outcome, received.getMessage());
            }
        };
    }
}
