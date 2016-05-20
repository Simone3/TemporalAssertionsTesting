package it.polimi.testing.temporalassertions.quantifiers;

/**
 * Implements a >= quantifier
 */
public class AtLeast extends AbstractQuantifier
{
    /**
     * {@inheritDoc}
     */
    private AtLeast(int n)
    {
        super(n);
    }

    /**
     * Allows to match >= n events
     * @param n the lower bound of the quantifier
     * @return the quantifier
     */
    public static AtLeast atLeast(int n)
    {
        return new AtLeast(n);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConditionMet()
    {
        return getCounter()>=getDesiredBound();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canStopCurrentComputation()
    {
        return isConditionMet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription()
    {
        return "at least "+getDesiredBound();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String describeError()
    {
        return "less than "+getDesiredBound();
    }
}
