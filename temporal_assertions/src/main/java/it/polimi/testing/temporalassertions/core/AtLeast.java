package it.polimi.testing.temporalassertions.core;

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
    protected boolean isConditionMet()
    {
        return getCounter()>=getDesiredBound();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean canStopCurrentComputation()
    {
        return isConditionMet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDescription()
    {
        return "at least "+getDesiredBound();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String describeError()
    {
        return "less than "+getDesiredBound();
    }
}
