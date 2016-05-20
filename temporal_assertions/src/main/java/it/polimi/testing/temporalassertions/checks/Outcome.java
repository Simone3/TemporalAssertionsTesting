package it.polimi.testing.temporalassertions.checks;

/**
 * Enum that expresses the outcome of a check
 * - success
 * - failure
 * - warning (it's success but in a particular situation or with some minor error)
 */
public enum Outcome
{
    SUCCESS, WARNING, FAILURE
}
