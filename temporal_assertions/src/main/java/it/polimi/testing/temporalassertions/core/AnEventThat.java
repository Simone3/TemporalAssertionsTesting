package it.polimi.testing.temporalassertions.core;


import android.support.annotation.NonNull;

import org.hamcrest.Matcher;

import it.polimi.testing.temporalassertions.events.Event;

import static it.polimi.testing.temporalassertions.core.AllHold.allHold;

/**
 * Descriptor that matches a single event at a time in the sequence
 */
public class AnEventThat extends AbstractEventDescriptor
{
    /**
     * {@inheritDoc}
     */
    private AnEventThat(Matcher<? extends Event> matcher)
    {
        super(matcher);
    }

    /**
     * Descriptor that matches a single event at a time in the sequence
     * @param matcher the Hamcrest matcher to recognize the event
     * @return the descriptor of a single event
     */
    public static AnEventThat anEventThat(Matcher<? extends Event> matcher)
    {
        return new AnEventThat(matcher);
    }

    /**
     * Checks that {@code this} is ONLY after {@code eventBefore}, i.e. there cannot be any {@code this} before
     * {@code eventBefore}
     * @param eventBefore the descriptor of the event before which we cannot find {@code this}
     * @return the check will return SUCCESS if every {@code this} is after a {@code eventBefore} or is not in
     *         the sequence at all, FAILURE if there's at least one before {@code eventBefore} and WARNING if no
     *         {@code eventBefore} has been found in the sequence
     */
    public Check canHappenOnlyAfter(final AnEventThat eventBefore)
    {
        return new Check(
                "Every event that "+getMatcher()+" happens after an event that "+eventBefore.getMatcher(),

                new CheckSubscriber()
                {
                    private final static int FOUND_NO_E2 = 0;
                    private final static int FOUND_E2 = 1;
                    private final static int FOUND_E1_BEFORE_E2 = 2;

                    private final State state = new State(FOUND_NO_E2);

                    @Override
                    public void onNext(Event event)
                    {
                        switch(state.getState())
                        {
                            // If we are still waiting for "eventBefore"...
                            case FOUND_NO_E2:

                                // If I find an event that matches the parameter...
                                if(eventBefore.getMatcher().matches(event))
                                {
                                    // Complete (success): from this point anything can happen, don't care anymore
                                    state.setState(FOUND_E2);
                                    state.setEvents(event);
                                    endCheck();
                                }

                                // If I find an event that matches the field before one that matches the parameter...
                                else if(getMatcher().matches(event))
                                {
                                    // Complete (error)
                                    state.setState(FOUND_E1_BEFORE_E2);
                                    state.setEvents(event);
                                    endCheck();
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
                            // Warning if no "eventBefore" has been found in the sequence
                            case FOUND_NO_E2:

                                outcome = Outcome.WARNING;
                                report = "No event that "+eventBefore.getMatcher()+" was found in the sequence";

                                break;

                            // Success if we didn't find any "this" before "eventBefore"
                            case FOUND_E2:

                                outcome = Outcome.SUCCESS;
                                report = "No event that "+getMatcher()+" was found before "+state.getEvent(0);

                                break;

                            // Failure if we found a "this" before "eventBefore"
                            case FOUND_E1_BEFORE_E2:

                                outcome = Outcome.FAILURE;
                                report = state.getEvent(0)+" was found before an event that "+eventBefore.getMatcher();

                                break;
                        }

                        return new Result(outcome, report);
                    }
                });
    }

    /**
     * Checks that {@code this} is ONLY before {@code eventAfter}, i.e. there cannot be any {@code this} after
     * {@code eventAfter}
     * @param eventAfter the descriptor of the event after which we cannot find {@code this}
     * @return the check will return SUCCESS if every {@code this} is before a {@code eventAfter} or is not in
     *         the sequence at all, FAILURE if there's at least one after {@code eventBefore} and WARNING if no
     *         {@code this} has been found in the sequence
     */
    public Check canHappenOnlyBefore(final AnEventThat eventAfter)
    {
        return new Check(
                "Every event that "+getMatcher()+" happens before an event that "+eventAfter.getMatcher(),

                new CheckSubscriber()
                {
                    private final static int CORRECT = 0;
                    private final static int FOUND_E1 = 1;

                    private final State state = new State(CORRECT);

                    private boolean foundAtLeastOneE1 = false;

                    @Override
                    public void onNext(Event event)
                    {
                        switch(state.getState())
                        {
                            // If we are in the CORRECT state (i.e. found an "eventAfter" after each "this" so far)
                            case CORRECT:

                                // If we match "this", go to the FOUND_E1 state
                                if(getMatcher().matches(event))
                                {
                                    state.setState(FOUND_E1);
                                    state.setEvents(event);
                                    foundAtLeastOneE1 = true;
                                }

                                break;

                            // If we found a "this" but not an "eventAfter" after it...
                            case FOUND_E1:

                                // If we match "eventAfter", go back to CORRECT
                                if(eventAfter.getMatcher().matches(event))
                                {
                                    state.setState(CORRECT);
                                    state.setEvents(event);
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
                            // If we are in the CORRECT state...
                            case CORRECT:

                                // Success if we found at least one "this" and a "eventAfter" after every one of them
                                if(foundAtLeastOneE1)
                                {
                                    outcome = Outcome.SUCCESS;
                                    report = "Every event that "+getMatcher()+" was found before "+state.getEvent(0);
                                }

                                // Warning if no "this" has been found in the sequence
                                else
                                {
                                    outcome = Outcome.WARNING;
                                    report = "No event that "+getMatcher()+" was found in the sequence";
                                }

                                break;

                            // Failure if we found a "this" but no "eventAfter" after it
                            case FOUND_E1:

                                outcome = Outcome.FAILURE;
                                report = "Event "+state.getEvent(0)+" was found after every event that "+eventAfter.getMatcher();

                                break;
                        }

                        return new Result(outcome, report);
                    }
                });
    }

    /**
     * Checks that {@code this} is ONLY between {@code eventBetween} and {@code eventAfter}, i.e. there
     * cannot be any {@code this} after {@code eventAfter} or before {@code eventBefore}
     * @param eventBefore the descriptor of the event before which we cannot find {@code this}
     * @param eventAfter the descriptor of the event after which we cannot find {@code this}
     * @return the check will return SUCCESS if every {@code this} is before a {@code eventAfter} and after
     *         a {@code eventBefore} or is not in the sequence at all, FAILURE if there's at least one
     *         after {@code eventAfter} or before {@code eventBefore}
     */
    public Check canHappenOnlyBetween(AnEventThat eventBefore, AnEventThat eventAfter)
    {
        return allHold(canHappenOnlyAfter(eventBefore), canHappenOnlyBefore(eventAfter))
                .overwriteDescription("Every event that "+getMatcher()+" happens between an event that "+eventBefore.getMatcher()+" and an event that "+eventAfter.getMatcher());
    }
}
