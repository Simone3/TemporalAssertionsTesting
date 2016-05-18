package it.polimi.testing.temporalassertions.matchers;


import org.hamcrest.Matcher;

import it.polimi.testing.temporalassertions.checks.Check;
import it.polimi.testing.temporalassertions.checks.CheckSubscriber;
import it.polimi.testing.temporalassertions.checks.Outcome;
import it.polimi.testing.temporalassertions.checks.Result;
import it.polimi.testing.temporalassertions.quantifiers.AbstractQuantifier;
import it.polimi.testing.temporalassertions.events.Event;

public class EventsWhereEach extends AbstractEventDescriptor
{
    AbstractQuantifier quantifier;

    public EventsWhereEach(AbstractQuantifier quantifier, Matcher<? extends Event> matcher)
    {
        super(matcher);
        this.quantifier = quantifier;
    }

    public Check mustHappenAfter(final AnEventThat eventBefore)
    {
        return new Check(new CheckSubscriber()
        {
            private final static int CORRECT = 0;
            private final static int WAITING_FOR_E1s = 1;
            private final static int CONDITION_NOT_MET = 2;

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
                            quantifier.resetCounter();
                            state.setState(WAITING_FOR_E1s);
                            state.setEvents(event);
                            foundAtLeastOneE2 = true;
                        }

                        break;

                    case WAITING_FOR_E1s:

                        if(getMatcher().matches(event))
                        {
                            quantifier.increaseCounter();
                        }
                        else if(eventBefore.getMatcher().matches(event))
                        {
                            if(quantifier.isConditionMet())
                            {
                                quantifier.resetCounter();
                            }
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
                final String SUCCESS_REPORT = "Every event that "+eventBefore.getMatcher()+" was followed by "+quantifier.getDescription()+" events that "+getMatcher();

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
                            outcome = SUCCESS_OUTCOME;
                            report = SUCCESS_REPORT;
                        }

                        break;

                    case WAITING_FOR_E1s:

                        if(quantifier.isConditionMet())
                        {
                            outcome = SUCCESS_OUTCOME;
                            report = SUCCESS_REPORT;
                        }
                        else
                        {
                            outcome = Outcome.FAILURE;
                            report = state.getEvent(0)+" was found but "+quantifier.getCounter()+" events that "+eventBefore.getMatcher()+" were found afterwards";
                        }

                        break;

                    case CONDITION_NOT_MET:

                        outcome = Outcome.FAILURE;
                        report = quantifier.getCounter()+" events that "+eventBefore.getMatcher()+" were found after "+state.getEvent(0);

                        break;
                }

                return new Result(outcome, report);
            }
        });
    }

    public Check mustHappenBefore(final AnEventThat eventAfter)
    {
        return new Check(new CheckSubscriber()
        {
            private final static int CORRECT = 0;
            private final static int CONDITION_NOT_MET = 1;

            private State state = new State(CORRECT);

            private boolean foundAtLeastOneE2 = false;

            @Override
            public void onNext(Event event)
            {
                if(getMatcher().matches(event))
                {
                    quantifier.increaseCounter();
                }
                else if(eventAfter.getMatcher().matches(event))
                {
                    foundAtLeastOneE2 = true;

                    if(quantifier.isConditionMet())
                    {
                        quantifier.resetCounter();
                    }
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
                    case CORRECT:

                        if(!foundAtLeastOneE2)
                        {
                            outcome = Outcome.WARNING;
                            report = "No event that "+eventAfter.getMatcher()+" was found in the sequence";
                        }
                        else
                        {
                            outcome = Outcome.SUCCESS;
                            report = "Every event that "+eventAfter.getMatcher()+" was preceded by "+quantifier.getDescription()+" events that "+getMatcher();
                        }

                        break;


                    case CONDITION_NOT_MET:

                        outcome = Outcome.FAILURE;
                        report = quantifier.getCounter()+" events that "+eventAfter.getMatcher()+" were found before "+state.getEvent(0);

                        break;
                }

                return new Result(outcome, report);
            }
        });
    }

    public Check mustHappenBetween(final AnEventThat eventBefore, final AnEventThat eventAfter)
    {
        return new Check(new CheckSubscriber()
        {
            private final static int CORRECT = 0;
            private final static int BETWEEN = 1;
            private final static int CONDITION_NOT_MET = 2;

            private State state = new State(CORRECT);

            private boolean foundAtLeastOneCouple = false;

            @Override
            public void onNext(Event event)
            {
                switch(state.getState())
                {
                    case CORRECT:

                        if(eventBefore.getMatcher().matches(event))
                        {
                            quantifier.resetCounter();
                            state.setState(BETWEEN);
                            state.setEvents(event);
                        }

                        break;

                    case BETWEEN:

                        if(getMatcher().matches(event))
                        {
                            quantifier.increaseCounter();
                        }
                        else if(eventAfter.getMatcher().matches(event))
                        {
                            if(quantifier.isConditionMet())
                            {
                                quantifier.resetCounter();
                                foundAtLeastOneCouple = true;
                            }
                            else
                            {
                                state.setState(CONDITION_NOT_MET);
                                state.addEvent(event);
                                endCheck();
                            }
                        }
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
                    case CORRECT:
                    case BETWEEN:

                        if(!foundAtLeastOneCouple)
                        {
                            outcome = Outcome.WARNING;
                            report = "No pair of events that "+eventBefore.getMatcher()+" and "+eventBefore.getMatcher()+" respectively were found in the sequence";
                        }
                        else
                        {
                            outcome = Outcome.SUCCESS;
                            report = "Every couple of events "+eventBefore.getMatcher()+" and "+eventAfter.getMatcher()+" respectively had "+quantifier.getDescription()+" events that "+getMatcher()+" in between";
                        }

                        break;

                    case CONDITION_NOT_MET:

                        outcome = Outcome.FAILURE;
                        report = quantifier.getCounter()+" events that "+getMatcher()+" were found between "+state.getEvent(0)+" and "+state.getEvent(1);

                        break;
                }

                return new Result(outcome, report);
            }
        });
    }
}
