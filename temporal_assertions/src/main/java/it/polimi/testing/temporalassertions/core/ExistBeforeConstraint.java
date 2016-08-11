package it.polimi.testing.temporalassertions.core;

import android.support.annotation.NonNull;

import org.hamcrest.Matcher;

import it.polimi.testing.temporalassertions.events.Event;

/**
 * Allows to define an existential quantifier before a given event
 */
public class ExistBeforeConstraint extends AbstractExistConstraint
{
    private AnEventThat eventAfter;

    /**
     * Constructor
     * @param eventAfter the event before which we apply the existential check
     */
    private ExistBeforeConstraint(AnEventThat eventAfter)
    {
        this.eventAfter = eventAfter;
    }

    /**
     * Allows to constrain the existential quantifier to check the events before ANY {@code eventAfter}
     * @param eventAfter the event before which we apply the existential check
     * @return the constraint for the existential quantifier
     */
    public static ExistBeforeConstraint before(AnEventThat eventAfter)
    {
        return new ExistBeforeConstraint(eventAfter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Check getConstrainedExistCheck(final Matcher<? extends Event> matcher, final AbstractQuantifier quantifier)
    {
        return new Check(
                quantifier.getDescription()+" events where each "+matcher+" are before an event that "+eventAfter.getMatcher(),

                new CheckSubscriber()
                {
                    private final static int COUNTING = 0;
                    private final static int CONDITION_MET = 1;

                    private final AbstractEventDescriptor.State state = new AbstractEventDescriptor.State(COUNTING);

                    private int foundE2s = 0;

                    @Override
                    public void onNext(Event event)
                    {
                        // If we match an event we are interested in, simply increase counter
                        if(matcher.matches(event))
                        {
                            quantifier.increaseCounter();
                        }

                        // If we match "eventAfter"...
                        else if(eventAfter.getMatcher().matches(event))
                        {
                            foundE2s++;

                            // If the precondition is met, exit with success
                            if(quantifier.isConditionMet())
                            {
                                state.setState(CONDITION_MET);
                                state.setEvents(event);
                                endCheck();
                            }

                            // Otherwise reset
                            else
                            {
                                quantifier.resetCounter();
                            }
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
                            // Success if the condition was met
                            case CONDITION_MET:

                                outcome = Outcome.SUCCESS;
                                report = quantifier.getCounter()+" events were found before "+state.getEvent(0);

                                break;

                            // If we are in COUNTING state, warning or failure
                            case COUNTING:

                                if(foundE2s<=0)
                                {
                                    outcome = Outcome.WARNING;
                                    report = "No event that "+eventAfter.getMatcher()+" was found in the sequence";
                                }
                                else
                                {
                                    outcome = Outcome.FAILURE;
                                    report = "The condition was never verified before any of the "+foundE2s+" events where each "+eventAfter.getMatcher();
                                }

                                break;
                        }

                        return new Result(outcome, report);
                    }
                });
    }
}
