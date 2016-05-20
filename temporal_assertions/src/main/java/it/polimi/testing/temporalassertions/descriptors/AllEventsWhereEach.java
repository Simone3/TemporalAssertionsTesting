package it.polimi.testing.temporalassertions.descriptors;


import org.hamcrest.Matcher;

import it.polimi.testing.temporalassertions.checks.Check;
import it.polimi.testing.temporalassertions.checks.CheckSubscriber;
import it.polimi.testing.temporalassertions.checks.Outcome;
import it.polimi.testing.temporalassertions.checks.Result;
import it.polimi.testing.temporalassertions.quantifiers.AbstractQuantifier;
import it.polimi.testing.temporalassertions.events.Event;

/**
 * Descriptor that describes all the events in the whole sequence that match the given
 * Hamcrest matcher
 */
public class AllEventsWhereEach extends AbstractEventDescriptor
{
    /**
     * Constructor
     * @param matcher the Hamcrest matcher to recognize the events
     */
    private AllEventsWhereEach(Matcher<? extends Event> matcher)
    {
        super(matcher);
    }

    /**
     * Descriptor that describes all the events in the whole sequence
     * @param matcher the Hamcrest matcher to recognize the events
     * @return the descriptor of all events
     */
    public static AllEventsWhereEach allEventsWhereEach(Matcher<? extends Event> matcher)
    {
        return new AllEventsWhereEach(matcher);
    }

    /**
     * Allows to quantify all the events matched by the descriptor, e.g. {@code are(exactly(10))} checks that all
     * events of the specified type are exactly 10 in the whole sequence
     * @param quantifier the quantifier that describes the number of events that are expected
     * @return the check will return SUCCESS if the quantifier is verified, FAILURE otherwise
     */
    public Check are(final AbstractQuantifier quantifier)
    {
        return new Check(new CheckSubscriber()
        {
            @Override
            public void onNext(Event event)
            {
                // If the event matches...
                if(getMatcher().matches(event))
                {
                    // Increase counter
                    quantifier.increaseCounter();

                    // Check if we can short-circuit the computation
                    if(quantifier.canStopCurrentComputation())
                    {
                        endCheck();
                    }
                }
            }

            @Override
            public Result getFinalResult()
            {
                Outcome outcome;
                String report;

                // Success if the quantifier's condition is met
                if(quantifier.isConditionMet())
                {
                    outcome = Outcome.SUCCESS;
                    report = "All events where each "+getMatcher()+" were "+quantifier.getDescription();
                }

                // Failure otherwise
                else
                {
                    outcome = Outcome.FAILURE;
                    report = "Found "+quantifier.describeError()+" events where each "+getMatcher();
                }
                return new Result(outcome, report);
            }
        });
    }

    /**
     * Allows to see if all the events matched by the descriptor match in order the given matchers (and so
     * it is also an implied {@link AllEventsWhereEach#are(AbstractQuantifier) are(exactly(#matchers))}).
     * @param matchers the matchers that need to be verified in order on all events
     * @return the check will return SUCCESS if all events match in order, FAILURE if one does not match or
     *         if there are more/less events than the number of matchers
     */
    @SafeVarargs
    public final Check matchInOrder(final Matcher<? extends Event>... matchers)
    {
        return new Check(new CheckSubscriber()
        {
            private int i = 0;
            private Event event;

            @Override
            public void onNext(Event event)
            {
                // If we have a match...
                if(getMatcher().matches(event))
                {
                    // If we are in a valid situation and the current event matches the current matcher, simply increase the index
                    if(i<matchers.length && matchers[i].matches(event))
                    {
                        i++;
                    }

                    // Otherwise (does not match or we went beyond the matchers length), short-circuit
                    else
                    {
                        if(i>=matchers.length) i++;
                        this.event = event;
                        endCheck();
                    }
                }
            }

            @Override
            public Result getFinalResult()
            {
                Outcome outcome;
                String report;

                // Success, if we found exactly "matchers.length" events and they matched in order
                if(i==matchers.length)
                {
                    outcome = Outcome.SUCCESS;
                    report = "All events where each "+getMatcher()+" satisfied in order the given matchers";
                }

                // Failure, if we found more events than #matchers
                else if(i>matchers.length)
                {
                    outcome = Outcome.FAILURE;
                    report = "The events where each "+getMatcher()+" satisfied in order the given matchers but an extra event "+event+" was found afterwards";
                }

                // Failure, if we found less events than #matchers or one did not match in order
                else
                {
                    outcome = Outcome.FAILURE;
                    report = "The event "+event+" does not match "+matchers[i];
                }

                return new Result(outcome, report);
            }
        });
    }
}
