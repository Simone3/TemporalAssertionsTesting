package it.polimi.testing.temporalassertions.prefixes;

import it.polimi.testing.temporalassertions.checks.Check;
import it.polimi.testing.temporalassertions.checks.IfThen;

/**
 * Prefix used to create a {@link IfThen} check as a "syntactic sugar" for the user
 */
public class ProvidedThat
{
    private Check ifCheck;

    /**
     * Constructor
     * @param check the check in the left side of the implication
     */
    private ProvidedThat(Check check)
    {
        this.ifCheck = check;
    }

    /**
     * Allows to build the first part of a material conditional (single implication) between two checks.
     * The check passed to {@link ProvidedThat#then(Check)} will be evaluated only if this check does not fail
     * (i.e. success or warning)
     *
     * C1 => C2
     *
     * @param check the check in the left side of the implication
     * @return the first part of the implication
     */
    public static ProvidedThat providedThat(Check check)
    {
        return new ProvidedThat(check);
    }

    /**
     * Allows to build the second part of a material conditional (single implication)
     * @param check the check in the right side of the implication
     * @return the check will return SUCCESS if the check passed to {@link ProvidedThat#providedThat(Check)}
     *         fails, otherwise the actual result of {@code check}
     */
    public Check then(Check check)
    {
        return new IfThen(ifCheck, check);
    }
}
