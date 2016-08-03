package it.polimi.testing.temporalassertions.core;


import android.support.annotation.NonNull;

import org.hamcrest.Matcher;

import java.util.Comparator;

import it.polimi.testing.temporalassertions.Utils;
import it.polimi.testing.temporalassertions.events.Event;

/**
 * Descriptor that describes all the events in the whole sequence that match the given
 * Hamcrest matcher
 */
public class AllEventsWhereEach<T extends Event> extends AbstractEventDescriptor
{
    /**
     * Constructor
     * @param matcher the Hamcrest matcher to recognize the events
     */
    public AllEventsWhereEach(Matcher<T> matcher)
    {
        super(matcher);
    }

    /**
     * Descriptor that describes all the events in the whole sequence
     * @param matcher the Hamcrest matcher to recognize the events
     * @return the descriptor of all events
     */
    public static <T extends Event> AllEventsWhereEach<T> allEventsWhereEach(Matcher<T> matcher)
    {
        return new AllEventsWhereEach<>(matcher);
    }

    /**
     * Internal implementation for: {@see Exist#eventsWhereEach(Matcher)}
     */
    Check are(final AbstractQuantifier quantifier)
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

                    @NonNull
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
    public final Check matchInOrder(final Matcher<T>... matchers)
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

                    @NonNull
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

    /**
     * Checks if all the events in the sequence are in a specific order, defined by the given comparator.
     * @param comparator the comparator defining the order between the events
     * @return the check will return SUCCESS if all comparisons return a value <= 0, FAILURE if at least one
     *         returns a value > 0, WARNING if no events of the given type were found in the sequence
     */
    public final Check areOrdered(final Comparator<T> comparator)
    {
        return new Check(
                "All events where each "+getMatcher()+" are in the order defined by the comparator: "+comparator,

                new CheckSubscriber()
                {
                    private boolean matchedAtLeastOneEvent = false;
                    private boolean wrongOrder = false;
                    private T wrongEvent;
                    private T previousEvent = null;

                    @Override
                    @SuppressWarnings("unchecked")
                    public void onNext(Event event)
                    {
                        T interestingEvent;
                        try
                        {
                            interestingEvent = (T) event;
                        }
                        catch(ClassCastException e)
                        {
                            return;
                        }

                        // If we have a match...
                        if(getMatcher().matches(interestingEvent))
                        {
                            matchedAtLeastOneEvent = true;

                            // If the order is wrong, exit
                            if(previousEvent!=null && comparator.compare(previousEvent, interestingEvent)>0)
                            {
                                wrongOrder = true;
                                wrongEvent = interestingEvent;
                                endCheck();
                            }

                            previousEvent = interestingEvent;
                        }
                    }

                    @NonNull
                    @Override
                    public Result getFinalResult()
                    {
                        Outcome outcome;
                        String report;

                        if(wrongOrder)
                        {
                            outcome = Outcome.FAILURE;
                            report = "Events "+previousEvent+" and "+wrongEvent+" are not ordered";
                        }
                        else if(matchedAtLeastOneEvent)
                        {
                            outcome = Outcome.SUCCESS;
                            report = "All events are in order";
                        }
                        else
                        {
                            outcome = Outcome.WARNING;
                            report = "No events found in the sequence";
                        }

                        return new Result(outcome, report);
                    }
                });
    }
}
