package it.polimi.testing.temporalassertions.core;

/**
 * Implements a = quantifier
 */
public class Exactly extends AbstractQuantifier
{
    /**
     * {@inheritDoc}
     */
    private Exactly(int n)
    {
        super(n);
    }

    /**
     * Allows to match = n events
     * @param n the exact bound of the quantifier
     * @return the quantifier
     */
    public static Exactly exactly(int n)
    {
        return new Exactly(n);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isConditionMet()
    {
        return getCounter()==getDesiredBound();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean canStopCurrentComputation()
    {
        return getCounter()>getDesiredBound();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDescription()
    {
        return "exactly "+getDesiredBound();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String describeError()
    {
        return getCounter()>getDesiredBound() ? "more than "+getDesiredBound() : "less than "+getDesiredBound();
    }
}
