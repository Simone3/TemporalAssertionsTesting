package it.polimi.testing.temporalassertions.core;

/**
 * Implements a <= quantifier
 */
public class AtMost extends AbstractQuantifier
{
    /**
     * {@inheritDoc}
     */
    private AtMost(int n)
    {
        super(n);
    }

    /**
     * Allows to match <= n events
     * @param n the upper bound of the quantifier
     * @return the quantifier
     */
    public static AtMost atMost(int n)
    {
        return new AtMost(n);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isConditionMet()
    {
        return getCounter()<=getDesiredBound();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean canStopCurrentComputation()
    {
        return !isConditionMet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDescription()
    {
        return "at most "+getDesiredBound();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String describeError()
    {
        return "more than "+getDesiredBound();
    }
}
