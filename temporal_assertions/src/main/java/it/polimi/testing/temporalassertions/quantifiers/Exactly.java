package it.polimi.testing.temporalassertions.quantifiers;


public class Exactly extends AbstractQuantifier
{
    private Exactly(int n)
    {
        super(n);
    }

    public static Exactly exactly(int n)
    {
        return new Exactly(n);
    }

    @Override
    public boolean isConditionMet()
    {
        return getCounter()==getDesiredBound();
    }

    @Override
    public boolean canStopCurrentComputation()
    {
        return getCounter()>getDesiredBound();
    }

    @Override
    public String getDescription()
    {
        return "exactly "+getDesiredBound();
    }

    @Override
    public String describeError()
    {
        return getCounter()>getDesiredBound() ? "more than "+getDesiredBound() : "less than "+getDesiredBound();
    }
}
