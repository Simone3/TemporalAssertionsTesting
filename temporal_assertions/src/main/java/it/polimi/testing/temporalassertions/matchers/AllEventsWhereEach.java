package it.polimi.testing.temporalassertions.matchers;


import org.hamcrest.Matcher;

import it.polimi.testing.temporalassertions.checks.Check;
import it.polimi.testing.temporalassertions.checks.CheckSubscriber;
import it.polimi.testing.temporalassertions.checks.Outcome;
import it.polimi.testing.temporalassertions.checks.Result;
import it.polimi.testing.temporalassertions.quantifiers.AbstractQuantifier;
import it.polimi.testing.temporalassertions.events.Event;

public class AllEventsWhereEach extends AbstractEventDescriptor
{
    private AllEventsWhereEach(Matcher<? extends Event> matcher)
    {
        super(matcher);
    }

    public static AllEventsWhereEach allEventsWhereEach(Matcher<? extends Event> matcher)
    {
        return new AllEventsWhereEach(matcher);
    }

    public Check are(final AbstractQuantifier quantifier)
    {
        return new Check(new CheckSubscriber()
        {
            @Override
            public void onNext(Event event)
            {
                if(getMatcher().matches(event))
                {
                    quantifier.increaseCounter();
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
                if(quantifier.isConditionMet())
                {
                    outcome = Outcome.SUCCESS;
                    report = "All events where each "+getMatcher()+" were "+quantifier.getDescription();
                }
                else
                {
                    outcome = Outcome.FAILURE;
                    report = "Found "+quantifier.describeError()+" events where each "+getMatcher();
                }
                return new Result(outcome, report);
            }
        });
    }

    @SafeVarargs
    public final Check matchInOrder(final Matcher<? extends Event>... matchers)
    {
        return new Check(new CheckSubscriber()
        {
            private int i = 0;
            private Event event;

            @Override
            public void onNext(Event event)
            {
                if(getMatcher().matches(event))
                {
                    if(i<matchers.length && matchers[i].matches(event))
                    {
                        i++;
                    }
                    else
                    {
                        if(i>=matchers.length) i++;
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

                if(i==matchers.length)
                {
                    outcome = Outcome.SUCCESS;
                    report = "All events where each "+getMatcher()+" satisfied in order the given matchers";
                }
                else if(i>matchers.length)
                {
                    outcome = Outcome.FAILURE;
                    report = "The events where each "+getMatcher()+" satisfied in order the given matchers but an extra event "+event+" was found afterwards";
                }
                else
                {
                    outcome = Outcome.FAILURE;
                    report = "The event "+event+" does not match "+matchers[i];
                }

                return new Result(outcome, report);
            }
        });
    }
}
