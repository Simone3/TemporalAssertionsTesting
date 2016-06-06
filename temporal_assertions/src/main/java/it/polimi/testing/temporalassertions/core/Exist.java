package it.polimi.testing.temporalassertions.core;

import org.hamcrest.Matcher;

import it.polimi.testing.temporalassertions.events.Event;

import static it.polimi.testing.temporalassertions.core.AllEventsWhereEach.allEventsWhereEach;
import static it.polimi.testing.temporalassertions.core.AtLeast.atLeast;

/**
 * Prefix used to create a existential checks
 */
public class Exist
{
    private AbstractQuantifier quantifier;

    /**
     * Constructor
     * @param quantifier defines the cardinality of the events
     */
    private Exist(AbstractQuantifier quantifier)
    {
        this.quantifier = quantifier;
    }

    /**
     * Allows to define the first part of an existential check
     * @param quantifier the quantifier that defines the number of events that must exist in the sequence
     * @return the first part of the existential quantifier
     */
    public static Exist exist(AbstractQuantifier quantifier)
    {
        return new Exist(quantifier);
    }

    /**
     * Checks that at least one event in the sequence matches the Hamcrest matcher
     * @return the check will return SUCCESS if the event exists, FAILURE if it does not
     */
    public static Check existsAnEventThat(Matcher<? extends Event> matcher)
    {
        return AllEventsWhereEach.allEventsWhereEach(matcher).are(atLeast(1))
                .overwriteDescription("Exists an event that "+matcher);
    }

    /**
     * Allows to build the second part of an existential quantifier. {@link Exist#exist(AbstractQuantifier)}
     * defines the number of events in the whole sequence that match the {@code matcher}. For example
     * {@code exist(exactly(10)).eventsWhereEach(m)} checks that in the entire sequence there are exactly 10
     * events that match {@code m}.
     * @param matcher the Hamcrest matcher that describes the events
     * @return the check will return SUCCESS if the quantifier is verified, FAILURE otherwise
     */
    public Check eventsWhereEach(Matcher<? extends Event> matcher)
    {
        return allEventsWhereEach(matcher).are(quantifier);
    }
}
