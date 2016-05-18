package it.polimi.testing.quantifiers;


public class AtMost extends AbstractQuantifier
{
    private AtMost(int n)
    {
        super(n);
    }

    public static AtMost atMost(int n)
    {
        return new AtMost(n);
    }

    @Override
    public boolean isConditionMet()
    {
        return getCounter()<=getDesiredBound();
    }

    @Override
    public boolean canStopCurrentComputation()
    {
        return !isConditionMet();
    }

    @Override
    public String getDescription()
    {
        return "at most "+getDesiredBound();
    }

    @Override
    public String describeError()
    {
        return "more than "+getDesiredBound();
    }
}
