package it.polimi.testing.temporalassertions.core;

import android.support.annotation.NonNull;

import org.hamcrest.Matcher;

import it.polimi.testing.temporalassertions.events.Event;

/**
 * Allows to define an existential quantifier between two given events
 */
public class ExistBetweenConstraint extends AbstractExistConstraint
{
    private AnEventThat eventBefore;
    private AnEventThat eventAfter;

    /**
     * Constructor
     * @param eventBefore the first element of a pair
     * @param eventAfter the second element of a pair
     */
    private ExistBetweenConstraint(AnEventThat eventBefore, AnEventThat eventAfter)
    {
        this.eventBefore = eventBefore;
        this.eventAfter = eventAfter;
    }

    /**
     * Allows to constrain the existential quantifier to check the events between ANY pair of
     * {@code eventBefore}-{@code eventAfter}
     * @param eventBefore the first element of a pair
     * @param eventAfter the second element of a pair
     * @return the constraint for the existential quantifier
     */
    public static ExistBetweenConstraint between(AnEventThat eventBefore, AnEventThat eventAfter)
    {
        return new ExistBetweenConstraint(eventBefore, eventAfter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Check getConstrainedExistCheck(final Matcher<? extends Event> matcher, final AbstractQuantifier quantifier)
    {
        return new Check(
                quantifier.getDescription()+" events where each "+matcher+" are between at least one pair of events where the first "+eventBefore.getMatcher()+" and the second "+eventAfter.getMatcher(),

                new CheckSubscriber()
                {
                    private final static int BEFORE = 0;
                    private final static int BETWEEN = 1;
                    private final static int CONDITION_MET = 2;

                    private final AbstractEventDescriptor.State state = new AbstractEventDescriptor.State(BEFORE);

                    private boolean atLeastOnePair = false;

                    @Override
                    public void onNext(Event event)
                    {
                        switch(state.getState())
                        {
                            // If we are in BEFORE state and we match an "eventBefore" we go in BETWEEN to wait for the events we are interested in
                            case BEFORE:

                                if(eventBefore.getMatcher().matches(event))
                                {
                                    quantifier.resetCounter();
                                    state.setState(BETWEEN);
                                    state.setEvents(event);
                                }

                                break;

                            // If we are in BETWEEN (i.e. after an "eventBefore")
                            case BETWEEN:

                                // Increase counter if we find an event we are interested in
                                if(matcher.matches(event))
                                {
                                    quantifier.increaseCounter();
                                }

                                else
                                {
                                    // Get matches
                                    boolean isEventBefore = eventBefore.getMatcher().matches(event);
                                    boolean isEventAfter = eventAfter.getMatcher().matches(event);
                                    boolean isBoth = isEventBefore && isEventAfter;

                                    // If it's both "eventBefore" and "eventAfter"...
                                    if(isBoth)
                                    {
                                        atLeastOnePair = true;

                                        // Exit with success if the condition is met
                                        if(quantifier.isConditionMet())
                                        {
                                            state.setState(CONDITION_MET);
                                            state.addEvent(event);
                                            endCheck();
                                        }

                                        // Otherwise simply reset counter
                                        else
                                        {
                                            quantifier.resetCounter();
                                            state.setEvents(event);
                                        }
                                    }

                                    // If we get an "eventAfter"...
                                    else if(isEventAfter)
                                    {
                                        atLeastOnePair = true;

                                        // Exit with success if the condition is met
                                        if(quantifier.isConditionMet())
                                        {
                                            state.setState(CONDITION_MET);
                                            state.addEvent(event);
                                            endCheck();
                                        }

                                        // Otherwise restart everything
                                        else
                                        {
                                            quantifier.resetCounter();
                                            state.setState(BEFORE);
                                            state.clearEvents();
                                        }
                                    }

                                    // If we get an "eventBefore" again before we get an "eventAfter", simply reset the counter
                                    else if(isEventBefore)
                                    {
                                        quantifier.resetCounter();
                                        state.setEvents(event);
                                    }
                                }

                                break;
                        }
                    }

                    @NonNull
                    @Override
                    public Result getFinalResult()
                    {
                        Outcome outcome = null;
                        String report = null;
                        switch(state.getState())
                        {
                            // Success if the condition has been met
                            case CONDITION_MET:

                                outcome = Outcome.SUCCESS;
                                report = quantifier.getCounter()+" events were found between "+state.getEvent(0)+" and "+state.getEvent(1);

                                break;

                            // Failure or warning otherwise
                            case BEFORE:
                            case BETWEEN:

                                if(atLeastOnePair)
                                {
                                    outcome = Outcome.FAILURE;
                                    report = "The condition was never met between any pair";
                                }
                                else
                                {
                                    outcome = Outcome.WARNING;
                                    report = "No pair was found in the sequence";
                                }

                                break;
                        }

                        return new Result(outcome, report);
                    }
                }
        );
    }
}
