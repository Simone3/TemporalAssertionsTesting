package it.polimi.testing.temporalassertions.matchers;


import org.hamcrest.Matcher;

import it.polimi.testing.temporalassertions.checks.Check;
import it.polimi.testing.temporalassertions.checks.CheckSubscriber;
import it.polimi.testing.temporalassertions.checks.Outcome;
import it.polimi.testing.temporalassertions.checks.Result;
import it.polimi.testing.temporalassertions.events.Event;

import static it.polimi.testing.temporalassertions.checks.AllHold.allHold;
import static it.polimi.testing.temporalassertions.matchers.AllEventsWhereEach.allEventsWhereEach;
import static it.polimi.testing.temporalassertions.quantifiers.AtLeast.atLeast;

public class AnEventThat extends AbstractEventDescriptor
{
    private AnEventThat(Matcher<? extends Event> matcher)
    {
        super(matcher);
    }

    public static AnEventThat anEventThat(Matcher<? extends Event> matcher)
    {
        return new AnEventThat(matcher);
    }

    public Check exists()
    {
        return allEventsWhereEach(getMatcher()).are(atLeast(1));
    }

    public Check existsAfter(final AnEventThat eventBefore)
    {
        return atLeast(1).eventsWhereEach(getMatcher()).mustHappenAfter(eventBefore);
    }

    public Check existsBefore(final AnEventThat eventAfter)
    {
        return atLeast(1).eventsWhereEach(getMatcher()).mustHappenBefore(eventAfter);
    }

    public Check existsBetween(final AnEventThat eventBefore, final AnEventThat eventAfter)
    {
        return atLeast(1).eventsWhereEach(getMatcher()).mustHappenBetween(eventBefore, eventAfter);
    }

    public Check canOnlyHappenAfter(final AnEventThat eventBefore)
    {
        return new Check(new CheckSubscriber()
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
                    case FOUND_NO_E2:

                        outcome = Outcome.WARNING;
                        report = "No event that "+eventBefore.getMatcher()+" was found in the sequence";

                        break;

                    case FOUND_E2:

                        outcome = Outcome.SUCCESS;
                        report = "No event that "+getMatcher()+" was found before "+state.getEvent(0);

                        break;

                    case FOUND_E1_BEFORE_E2:

                        outcome = Outcome.FAILURE;
                        report = state.getEvent(0)+" was found before an event that "+eventBefore.getMatcher();

                        break;
                }

                return new Result(outcome, report);
            }
        });
    }

    public Check canOnlyHappenBefore(final AnEventThat eventAfter)
    {
        return new Check(new CheckSubscriber()
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
                    case CORRECT:

                        //
                        if(getMatcher().matches(event))
                        {
                            state.setState(FOUND_E1);
                            state.setEvents(event);
                            foundAtLeastOneE1 = true;
                        }

                        break;

                    case FOUND_E1:

                        //
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
                    case CORRECT:

                        if(foundAtLeastOneE1)
                        {
                            outcome = Outcome.SUCCESS;
                            report = "Every event that "+getMatcher()+" was found before "+state.getEvent(0);
                        }
                        else
                        {
                            outcome = Outcome.WARNING;
                            report = "No event that "+getMatcher()+" was found in the sequence";
                        }

                        break;

                    case FOUND_E1:

                        outcome = Outcome.FAILURE;
                        report = "Event "+state.getEvent(0)+" was found after every event that "+eventAfter.getMatcher();

                        break;
                }

                return new Result(outcome, report);
            }
        });
    }

    public Check canOnlyHappenBetween(AnEventThat eventBefore, AnEventThat eventAfter)
    {
        return allHold(canOnlyHappenAfter(eventBefore), canOnlyHappenBefore(eventAfter));
    }

    public Check mustHappenAfter(final AnEventThat eventBefore)
    {
        return new Check(new CheckSubscriber()
        {
            private final static int CORRECT = 0;
            private final static int WAITING_FOR_E1 = 1;
            private final static int FOUND_TWO_E2 = 2;

            private State state = new State(CORRECT);

            private boolean foundAtLeastOneE2 = false;

            @Override
            public void onNext(Event event)
            {
                switch(state.getState())
                {
                    case CORRECT:

                        if(eventBefore.getMatcher().matches(event))
                        {
                            state.setState(WAITING_FOR_E1);
                            state.setEvents(event);
                            foundAtLeastOneE2 = true;
                        }

                        break;

                    case WAITING_FOR_E1:

                        if(getMatcher().matches(event))
                        {
                            state.setState(CORRECT);
                            state.clearEvents();
                        }
                        else if(eventBefore.getMatcher().matches(event))
                        {
                            state.setState(FOUND_TWO_E2);
                            state.addEvent(event);
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
                    case CORRECT:

                        if(!foundAtLeastOneE2)
                        {
                            outcome = Outcome.WARNING;
                            report = "No event that "+eventBefore.getMatcher()+" was found in the sequence";
                        }
                        else
                        {
                            outcome = Outcome.SUCCESS;
                            report = "Every event that "+eventBefore.getMatcher()+" was followed by an event that "+getMatcher();
                        }

                        break;

                    case WAITING_FOR_E1:

                        outcome = Outcome.FAILURE;
                        report = state.getEvent(0)+" was found but no event that "+eventBefore.getMatcher()+" was found afterwards";

                        break;

                    case FOUND_TWO_E2:

                        outcome = Outcome.FAILURE;
                        report = state.getEvent(1)+" was found after "+state.getEvent(0)+" without finding an event that "+eventBefore.getMatcher()+" in between";

                        break;
                }

                return new Result(outcome, report);
            }
        });
    }
}
