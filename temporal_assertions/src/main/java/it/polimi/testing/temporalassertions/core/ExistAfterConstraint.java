package it.polimi.testing.temporalassertions.core;

import android.support.annotation.NonNull;

import org.hamcrest.Matcher;

import it.polimi.testing.temporalassertions.events.Event;

/**
 * Allows to define an existential quantifier after a given event
 */
public class ExistAfterConstraint extends AbstractExistConstraint
{
    private AnEventThat eventBefore;

    /**
     * Constructor
     * @param eventBefore the event after which we apply the existential check
     */
    private ExistAfterConstraint(AnEventThat eventBefore)
    {
        this.eventBefore = eventBefore;
    }

    /**
     * Allows to constrain the existential quantifier to check the events after ANY {@code eventBefore}
     * @param eventBefore the event after which we apply the existential check
     * @return the constraint for the existential quantifier
     */
    public static ExistAfterConstraint after(AnEventThat eventBefore)
    {
        return new ExistAfterConstraint(eventBefore);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Check getConstrainedExistCheck(final Matcher<? extends Event> matcher, final AbstractQuantifier quantifier)
    {
        return new Check(
                quantifier.getDescription()+" events where each "+matcher+" are after an event that "+eventBefore.getMatcher(),

                new CheckSubscriber()
                {
                    private final static int BEFORE = 0;
                    private final static int WAITING_FOR_E1s = 1;
                    private final static int CONDITION_MET = 2;

                    private final AbstractEventDescriptor.State state = new AbstractEventDescriptor.State(BEFORE);

                    private int foundE2s = 0;

                    @Override
                    public void onNext(Event event)
                    {
                        switch(state.getState())
                        {
                            // If we are in the BEFORE state...
                            case BEFORE:

                                // If we match an "eventBefore" we move to the WAITING_FOR_E1s state
                                if(eventBefore.getMatcher().matches(event))
                                {
                                    quantifier.resetCounter();
                                    state.setState(WAITING_FOR_E1s);
                                    state.setEvents(event);
                                    foundE2s++;
                                }

                                break;

                            // If we are in the WAITING_FOR_E1s state...
                            case WAITING_FOR_E1s:

                                // If we match an event we are interested in, simply increase the counter
                                if(matcher.matches(event))
                                {
                                    quantifier.increaseCounter();
                                }

                                // If we match an "eventBefore"...
                                else if(eventBefore.getMatcher().matches(event))
                                {
                                    // If the condition is met, exit successfully
                                    if(quantifier.isConditionMet())
                                    {
                                        state.setState(CONDITION_MET);
                                        endCheck();
                                    }

                                    // Otherwise reset
                                    else
                                    {
                                        quantifier.resetCounter();
                                    }
                                }

                                break;
                        }
                    }

                    @NonNull
                    @Override
                    public Result getFinalResult()
                    {
                        // If we are waiting and the condition is met, it's like CONDITION_MET
                        if(state.getState()==WAITING_FOR_E1s && quantifier.isConditionMet())
                        {
                            state.setState(CONDITION_MET);
                        }

                        Outcome outcome = null;
                        String report = null;
                        switch(state.getState())
                        {
                            // If we are in BEFORE or WAITING_FOR_E1s state, failure or warning
                            case BEFORE:
                            case WAITING_FOR_E1s:

                                if(foundE2s<=0)
                                {
                                    outcome = Outcome.WARNING;
                                    report = "No event that "+eventBefore.getMatcher()+" was found in the sequence";
                                }
                                else
                                {
                                    outcome = Outcome.FAILURE;
                                    report = "The condition was never verified after any of the "+foundE2s+" events where each "+eventBefore.getMatcher();
                                }

                                break;

                            // Success, if the condition was met
                            case CONDITION_MET:

                                outcome = Outcome.SUCCESS;
                                report = quantifier.getCounter()+" events were found after "+state.getEvent(0);

                                break;
                        }

                        return new Result(outcome, report);
                    }
                });
    }
}
