package it.polimi.testing.temporalassertions.core;


import org.hamcrest.Matcher;

import it.polimi.testing.temporalassertions.Utils;
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
        return new Check(
                "All events where each "+getMatcher()+" are "+quantifier.getDescription(),

                new CheckSubscriber()
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
                        }

                        // Failure otherwise
                        else
                        {
                            outcome = Outcome.FAILURE;
                        }
                        report = "Events were "+quantifier.getCounter();
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
        return new Check(
                "All events where each "+getMatcher()+" satisfy in order "+Utils.arrayToString(matchers),

                new CheckSubscriber()
                {
                    private int i = 0;
                    private boolean oneDoesNotMatch = false;
                    private Event event;

                    @Override
                    public void onNext(Event event)
                    {
                        // If we have a match...
                        if(getMatcher().matches(event))
                        {
                            // If we are in a valid situation...
                            if(i<matchers.length)
                            {
                                // If the current event matches the current matcher, simply increase the index
                                if(matchers[i].matches(event)) i++;

                                // Otherwise (no match), short-circuit
                                else
                                {
                                    oneDoesNotMatch = true;
                                    this.event = event;
                                    endCheck();
                                }
                            }

                            // Otherwise (too many events)
                            else
                            {
                                i++;
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
                            report = "All satisfied in order";
                        }

                        // Failure if...
                        else
                        {
                            outcome = Outcome.FAILURE;

                            // We found more events than #matchers
                            if(i>matchers.length)
                            {
                                report = "The events satisfied in order the given matchers but an extra event "+event+" was found afterwards";
                            }

                            // One event did not match
                            else if(oneDoesNotMatch)
                            {
                                report = "The event "+event+" does not match '"+matchers[i]+"'";
                            }

                            // We found less events that #matchers
                            else
                            {
                                report = "The events were not enough to satisfy all matchers: no event satisfied '"+matchers[i]+"'";
                            }
                        }

                        return new Result(outcome, report);
                    }
                });
    }
}
