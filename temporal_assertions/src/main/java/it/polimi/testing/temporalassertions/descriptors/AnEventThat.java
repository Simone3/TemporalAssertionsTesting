package it.polimi.testing.temporalassertions.descriptors;


import org.hamcrest.Matcher;

import it.polimi.testing.temporalassertions.checks.Check;
import it.polimi.testing.temporalassertions.checks.CheckSubscriber;
import it.polimi.testing.temporalassertions.checks.Outcome;
import it.polimi.testing.temporalassertions.checks.Result;
import it.polimi.testing.temporalassertions.events.Event;

import static it.polimi.testing.temporalassertions.checks.AllHold.allHold;
import static it.polimi.testing.temporalassertions.descriptors.AllEventsWhereEach.allEventsWhereEach;
import static it.polimi.testing.temporalassertions.quantifiers.AtLeast.atLeast;

/**
 * Descriptor that matches a single event in the sequence
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
     * Descriptor that matches a single event in the sequence
     * @param matcher the Hamcrest matcher to recognize the event
     * @return the descriptor of a single event
     */
    public static AnEventThat anEventThat(Matcher<? extends Event> matcher)
    {
        return new AnEventThat(matcher);
    }

    /**
     * Checks that {@code this} exists, i.e. at least one event in the sequence matches the Hamcrest matcher
     * @return the check will return SUCCESS if {@code this} exists, FAILURE if it does not
     */
    public Check exists()
    {
        return allEventsWhereEach(getMatcher()).are(atLeast(1))
                .overwriteDescription("An event that "+getMatcher()+" exists");
    }

    /**
     * Checks that {@code this} exists after {@code eventBefore}, i.e. there's at least one {@code this} in
     * the sequence after {@code eventBefore}
     * @param eventBefore the descriptor of the event after which we must find {@code this}
     * @return the check will return SUCCESS if {@code this} exists after {@code eventBefore}, FAILURE if it
     *         does not and WARNING if no {@code eventBefore} has been found in the sequence
     */
    public Check existsAfter(final AnEventThat eventBefore)
    {
        return atLeast(1).eventsWhereEach(getMatcher()).mustHappenAfter(eventBefore)
                .overwriteDescription("An event that "+getMatcher()+" exists after an event that "+eventBefore.getMatcher());
    }

    /**
     * Checks that {@code this} exists before {@code eventAfter}, i.e. there's at least one {@code this} in
     * the sequence before {@code eventAfter}
     * @param eventAfter the descriptor of the event before which we must find {@code this}
     * @return the check will return SUCCESS if {@code this} exists before {@code eventAfter}, FAILURE if it
     *         does not and WARNING if no {@code eventAfter} has been found in the sequence
     */
    public Check existsBefore(final AnEventThat eventAfter)
    {
        return atLeast(1).eventsWhereEach(getMatcher()).mustHappenBefore(eventAfter)
                .overwriteDescription("An event that "+getMatcher()+" exists after an before that "+eventAfter.getMatcher());
    }

    /**
     * Checks that {@code this} exists between {@code eventBefore} and {@code eventAfter}, i.e. there's at
     * least one {@code this} in the sequence after {@code eventBefore} and before {@code eventAfter}
     * @param eventBefore the descriptor of the event after which we must find {@code this}
     * @param eventAfter the descriptor of the event before which we must find {@code this}
     * @return the check will return SUCCESS if {@code this} exists between {@code eventBefore} and
     *         {@code eventAfter}, FAILURE if it does not and WARNING if no pair
     *         {@code eventBefore-eventAfter} has been found in the sequence
     */
    public Check existsBetween(final AnEventThat eventBefore, final AnEventThat eventAfter)
    {
        return atLeast(1).eventsWhereEach(getMatcher()).mustHappenBetween(eventBefore, eventAfter)
                .overwriteDescription("An event that "+getMatcher()+" exists between an event that "+eventBefore.getMatcher()+" and an event that "+eventAfter.getMatcher());
    }

    /**
     * Checks that {@code this} is ONLY after {@code eventBefore}, i.e. there cannot be any {@code this} before
     * {@code eventBefore}
     * @param eventBefore the descriptor of the event before which we cannot find {@code this}
     * @return the check will return SUCCESS if every {@code this} is after a {@code eventBefore} or is not in
     *         the sequence at all, FAILURE if there's at least one before {@code eventBefore} and WARNING if no
     *         {@code eventBefore} has been found in the sequence
     */
    public Check canOnlyHappenAfter(final AnEventThat eventBefore)
    {
        return new Check(
                "Every event that "+getMatcher()+" happens after an event that "+eventBefore.getMatcher(),

                new CheckSubscriber()
                {
                    private final static int FOUND_NO_E2 = 0;
                    private final static int FOUND_E2 = 1;
                    private final static int FOUND_E1_BEFORE_E2 = 2;

                    private State state = new State(FOUND_NO_E2);

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
    public Check canOnlyHappenBefore(final AnEventThat eventAfter)
    {
        return new Check(
                "Every event that "+getMatcher()+" happens before an event that "+eventAfter.getMatcher(),

                new CheckSubscriber()
                {
                    private final static int CORRECT = 0;
                    private final static int FOUND_E1 = 1;

                    private State state = new State(CORRECT);

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
    public Check canOnlyHappenBetween(AnEventThat eventBefore, AnEventThat eventAfter)
    {
        return allHold(canOnlyHappenAfter(eventBefore), canOnlyHappenBefore(eventAfter))
                .overwriteDescription("Every event that "+getMatcher()+" happens between an event that "+eventBefore.getMatcher()+" and an event that "+eventAfter.getMatcher());
    }
}
