package it.polimi.testing.temporalassertions.core;

/**
 * Prefix used to create a {@link IfAndOnlyIf} check as a "syntactic sugar" for the user
 */
public class WeHaveThat
{
    private final Check firstCheck;

    /**
     * Constructor
     * @param check the check in the left side of the implication
     */
    private WeHaveThat(Check check)
    {
        this.firstCheck = check;
    }

    /**
     * Allows to build the first part of a logical biconditional (double implication) between two checks.
     * The check succeeds only if this check and the one passed to {@link WeHaveThat#ifAndOnlyIf(Check)}
     * both fail or both do not fail (i.e. success or warning)
     *
     * C1 <==> C2
     *
     * @param check the check in the left side of the double implication
     * @return the first part of the double implication
     */
    public static WeHaveThat weHaveThat(Check check)
    {
        return new WeHaveThat(check);
    }

    /**
     * Allows to build the second part of a logical biconditional (double implication)
     * @param check the check in the right side of the double implication
     * @return the check will return SUCCESS if the checks both fail or both do not fail
     *         (i.e. success or warning), FAILURE otherwise
     */
    public Check ifAndOnlyIf(Check check)
    {
        return new IfAndOnlyIf(firstCheck, check);
    }
}