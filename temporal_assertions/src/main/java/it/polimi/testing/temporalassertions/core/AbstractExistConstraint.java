package it.polimi.testing.temporalassertions.core;

import org.hamcrest.Matcher;

import it.polimi.testing.temporalassertions.events.Event;

/**
 * Helper to build constrained existential quantifiers in {@link Exist#exist(AbstractExistConstraint, AbstractQuantifier)}
 */
abstract class AbstractExistConstraint
{
    /**
     * Used by {@link Exist} to get the constrained check
     * @param matcher the matcher passed by Exist
     * @param quantifier the quantifier passed by Exist
     * @return the constrained existential quantifier
     */
    abstract Check getConstrainedExistCheck(final Matcher<? extends Event> matcher, final AbstractQuantifier quantifier);
}
