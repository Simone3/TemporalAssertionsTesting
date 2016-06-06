package it.polimi.testing.temporalassertions.core;


import org.hamcrest.Matcher;

import it.polimi.testing.temporalassertions.events.Event;

/**
 * Descriptor that allows to match a given number of events in the sequence (set of events).
 * The number of those events  * is described by a quantifier, so it can be for example "exactly",
 * "at least", etc.
 */
public class EventsWhereEach extends AbstractEventDescriptor
{
    private final AbstractQuantifier quantifier;

    /**
     * Constructor
     * @param quantifier the number of events that should be matched by the descriptor
     * @param matcher the Hamcrest matcher to recognize the events
     */
    EventsWhereEach(AbstractQuantifier quantifier, Matcher<? extends Event> matcher)
    {
        super(matcher);
        this.quantifier = quantifier;
    }

    /**
     * Checks that the events described by {@code this} are always exclusively after each {@code eventBefore}.
     * For example {@code exactly(2).eventsWhereEach(m1).mustHappenAfter(anEventThat(m2)} means that after each
     * {@code eventBefore} event there must be 2 {@code this} events. "Exclusively" means that those 2 events must
     * be before the next (if any) event matched by {@code this}
     * @param eventBefore the event after which we must have {@code this} events
     * @return the check will return SUCCESS if after each {@code eventBefore} we found the desired amount of
     *         {@code this} events, FAILURE if we did not and WARNING if no {@code eventBefore} was found in
     *         the sequence
     */
    public Check mustHappenAfter(final AnEventThat eventBefore)
    {
        return new Check(
                "Every event that "+eventBefore.getMatcher()+" is followed by "+quantifier.getDescription()+" events where each "+getMatcher(),

                new CheckSubscriber()
                {
                    private final static int CORRECT = 0;
                    private final static int WAITING_FOR_E1s = 1;
                    private final static int CONDITION_NOT_MET = 2;

                    private final State state = new State(CORRECT);

                    private boolean foundAtLeastOneE2 = false;

                    @Override
                    public void onNext(Event event)
                    {
                        switch(state.getState())
                        {
                            // If we are in the CORRECT state...
                            case CORRECT:

                                // If we match an "eventBefore" we move to the WAITING_FOR_E1s state
                                if(eventBefore.getMatcher().matches(event))
                                {
                                    quantifier.resetCounter();
                                    state.setState(WAITING_FOR_E1s);
                                    state.setEvents(event);
                                    foundAtLeastOneE2 = true;
                                }

                                break;

                            // If we are in the WAITING_FOR_E1s state...
                            case WAITING_FOR_E1s:

                                // If we match a "this" event, simply increase the counter
                                if(getMatcher().matches(event))
                                {
                                    quantifier.increaseCounter();
                                }

                                // If we match an "eventBefore"...
                                else if(eventBefore.getMatcher().matches(event))
                                {
                                    // If the condition is met, we restart the count
                                    if(quantifier.isConditionMet())
                                    {
                                        quantifier.resetCounter();
                                    }

                                    // Otherwise we have an error ("exclusively" constraint: no "eventBefore" before we meet the desired number of "this" events!)
                                    else
                                    {
                                        state.setState(CONDITION_NOT_MET);
                                        state.addEvent(event);
                                        endCheck();
                                    }
                                }

                                break;
                        }
                    }

                    @Override
                    public Result getFinalResult()
                    {
                        final Outcome SUCCESS_OUTCOME = Outcome.SUCCESS;
                        final String SUCCESS_REPORT = "Check verified";

                        Outcome outcome = null;
                        String report = null;
                        switch(state.getState())
                        {
                            // If we are in CORRECT state, we can have a success or a warning
                            case CORRECT:

                                if(!foundAtLeastOneE2)
                                {
                                    outcome = Outcome.WARNING;
                                    report = "No event that "+eventBefore.getMatcher()+" was found in the sequence";
                                }
                                else
                                {
                                    outcome = SUCCESS_OUTCOME;
                                    report = SUCCESS_REPORT;
                                }

                                break;

                            // If we are waiting for "this" events...
                            case WAITING_FOR_E1s:

                                // If the last condition is met, it's a success
                                if(quantifier.isConditionMet())
                                {
                                    outcome = SUCCESS_OUTCOME;
                                    report = SUCCESS_REPORT;
                                }

                                // Otherwise failure
                                else
                                {
                                    outcome = Outcome.FAILURE;
                                    report = state.getEvent(0)+" was found but "+quantifier.getCounter()+" events where each "+eventBefore.getMatcher()+" were found afterwards";
                                }

                                break;

                            // Failure, if we had an error
                            case CONDITION_NOT_MET:

                                outcome = Outcome.FAILURE;
                                report = quantifier.getCounter()+" events where each "+eventBefore.getMatcher()+" were found after "+state.getEvent(0);

                                break;
                        }

                        return new Result(outcome, report);
                    }
                });
    }

    /**
     * Checks that the events described by {@code this} are always exclusively before each {@code eventAfter}.
     * For example {@code exactly(2).eventsWhereEach(m1).mustHappenBefore(anEventThat(m2)} means that before
     * each {@code eventAfter} event there must be 2 {@code this} events. "Exclusively" means that those 2 events
     * must be after the previous (if any) event matched by {@code this}
     * @param eventAfter the event before which we must have {@code this} events
     * @return the check will return SUCCESS if before each {@code eventAfter} we found the desired amount of
     *         {@code this} events, FAILURE if we did not and WARNING if no {@code eventAfter} was found in
     *         the sequence
     */
    public Check mustHappenBefore(final AnEventThat eventAfter)
    {
        return new Check(
                "Every event that "+eventAfter.getMatcher()+" is preceded by "+quantifier.getDescription()+" events where each "+getMatcher(),

                new CheckSubscriber()
                {
                    private final static int CORRECT = 0;
                    private final static int CONDITION_NOT_MET = 1;

                    private final State state = new State(CORRECT);

                    private boolean foundAtLeastOneE2 = false;

                    @Override
                    public void onNext(Event event)
                    {
                        // If we match "this", simply increase counter
                        if(getMatcher().matches(event))
                        {
                            quantifier.increaseCounter();
                        }

                        // If we match "eventAfter"...
                        else if(eventAfter.getMatcher().matches(event))
                        {
                            foundAtLeastOneE2 = true;

                            // If the precondition is met, restart count
                            if(quantifier.isConditionMet())
                            {
                                quantifier.resetCounter();
                            }

                            // Otherwise we have an error
                            else
                            {
                                state.setState(CONDITION_NOT_MET);
                                state.setEvents(event);
                                endCheck();
                            }
                        }
                    }

                    @Override
                    public Result getFinalResult()
                    {
                        Outcome outcome = null;
                        String report = null;
                        switch(state.getState())
                        {
                            // If we are in CORRECT state, warning or success
                            case CORRECT:

                                if(!foundAtLeastOneE2)
                                {
                                    outcome = Outcome.WARNING;
                                    report = "No event that "+eventAfter.getMatcher()+" was found in the sequence";
                                }
                                else
                                {
                                    outcome = Outcome.SUCCESS;
                                    report = "Check verified";
                                }

                                break;

                            // Failure if we had an error with one of the preconditions
                            case CONDITION_NOT_MET:

                                outcome = Outcome.FAILURE;
                                report = quantifier.getCounter()+" events where each "+eventAfter.getMatcher()+" were found before "+state.getEvent(0);

                                break;
                        }

                        return new Result(outcome, report);
                    }
                });
    }

    /**
     * Checks that the events described by {@code this} are always exclusively between each
     * {@code eventBefore-eventAfter} pair. For example
     * {@code exactly(2).eventsWhereEach(m1).mustHappenBetween(anEventThat(m2), anEventThat(m3)} means that
     * between each pair of {@code eventBefore-eventAfter} events there must be 2 {@code this} events.
     * "Exclusively" means that those 2 events cannot be "shared" by overlapping pairs.
     * @param eventBefore the event after which we must have {@code this} events
     * @param eventAfter the event before which we must have {@code this} events
     * @return the check will return SUCCESS if between each {@code eventBefore-eventAfter} pair we found the
     *         desired amount of {@code this} events, FAILURE if we did not and WARNING if no
     *         {@code eventBefore-eventAfter} pair was found in  the sequence
     */
    public Check mustHappenBetween(final AnEventThat eventBefore, final AnEventThat eventAfter)
    {
        return new Check(
                "Every pair of events '"+eventBefore.getMatcher()+"' and '"+eventAfter.getMatcher()+"' respectively has "+quantifier.getDescription()+" events where each "+getMatcher()+" in between",

                new CheckSubscriber()
                {
                    private final static int CORRECT = 0;
                    private final static int BETWEEN = 1;
                    private final static int CONDITION_NOT_MET = 2;

                    private final State state = new State(CORRECT);

                    private boolean foundAtLeastOnePair = false;

                    @Override
                    public void onNext(Event event)
                    {
                        switch(state.getState())
                        {
                            // If we are CORRECT state and we match an "eventBefore" we go in BETWEEN to wait for the "this" events
                            case CORRECT:

                                if(eventBefore.getMatcher().matches(event))
                                {
                                    quantifier.resetCounter();
                                    state.setState(BETWEEN);
                                    state.setEvents(event);
                                }

                                break;

                            // If we are in BETWEEN (i.e. after an "eventBefore")
                            case BETWEEN:

                                // Increase counter if we find a "this" event
                                if(getMatcher().matches(event))
                                {
                                    quantifier.increaseCounter();
                                }

                                // If we match an "eventAfter" event...
                                else if(eventAfter.getMatcher().matches(event))
                                {
                                    // Go back to CORRECT if the condition is met
                                    if(quantifier.isConditionMet())
                                    {
                                        quantifier.resetCounter();
                                        state.setState(CORRECT);
                                        foundAtLeastOnePair = true;
                                    }

                                    // Error otherwise
                                    else
                                    {
                                        state.setState(CONDITION_NOT_MET);
                                        state.addEvent(event);
                                        endCheck();
                                    }
                                }

                                // If we match another "eventBefore" before we find an "eventAfter", simply reset the counter
                                else if(eventBefore.getMatcher().matches(event))
                                {
                                    quantifier.resetCounter();
                                }

                                break;
                        }
                    }

                    @Override
                    public Result getFinalResult()
                    {
                        Outcome outcome = null;
                        String report = null;
                        switch(state.getState())
                        {
                            // Success or warning if we are in CORRECT (all pairs contained the expected number of "this" events) or in BETWEEN (in the last part we found an "eventBefore" but no "eventAfter" to close the pair, so it's ok)
                            case CORRECT:
                            case BETWEEN:

                                if(!foundAtLeastOnePair)
                                {
                                    outcome = Outcome.WARNING;
                                    report = "No pair of events was found in the sequence";
                                }
                                else
                                {
                                    outcome = Outcome.SUCCESS;
                                    report = "Check verified";
                                }

                                break;

                            // Failure if we found an error
                            case CONDITION_NOT_MET:

                                outcome = Outcome.FAILURE;
                                report = quantifier.getCounter()+" events where each "+getMatcher()+" were found between "+state.getEvent(0)+" and "+state.getEvent(1);

                                break;
                        }

                        return new Result(outcome, report);
                    }
                });
    }
}
