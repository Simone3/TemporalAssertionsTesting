package it.polimi.testing.quantifiers;


public class AtLeast extends AbstractQuantifier
{
    private AtLeast(int n)
    {
        super(n);
    }

    public static AtLeast atLeast(int n)
    {
        return new AtLeast(n);
    }

    @Override
    public boolean isConditionMet()
    {
        return getCounter()>=getDesiredBound();
    }

    @Override
    public boolean canStopCurrentComputation()
    {
        return isConditionMet();
    }

    @Override
    public String getDescription()
    {
        return "at least "+getDesiredBound();
    }

    @Override
    public String describeError()
    {
        return "less than "+getDesiredBound();
    }
}
