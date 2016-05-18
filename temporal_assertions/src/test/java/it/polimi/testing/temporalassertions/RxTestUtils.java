package it.polimi.testing.temporalassertions;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.List;

import it.polimi.testing.temporalassertions.checks.Check;
import it.polimi.testing.temporalassertions.checks.CheckSubscriber;
import it.polimi.testing.temporalassertions.checks.Outcome;
import it.polimi.testing.temporalassertions.checks.Result;
import it.polimi.testing.temporalassertions.events.Event;
import it.polimi.testing.temporalassertions.events.GenericEvent;
import it.polimi.testing.temporalassertions.matchers.Matchers;
import it.polimi.testing.temporalassertions.operators.EnforceCheck;
import rx.Observable;
import rx.Subscriber;
import rx.observers.TestSubscriber;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.hamcrest.core.StringStartsWith.startsWith;

public abstract class RxTestUtils
{
    public static Observable<? extends Event> generateEvents(final String... strings)
    {
        return Observable.create(new Observable.OnSubscribe<Event>()
        {
            @Override
            public void call(final Subscriber<? super Event> subscriber)
            {
                for(String string: strings)
                {
                    if(!subscriber.isUnsubscribed())
                    {
                        subscriber.onNext(new GenericEvent(string));
                    }
                }

                subscriber.onCompleted();
            }
        });
    }

    public static Matcher<GenericEvent> is(String string)
    {
        return Matchers.isGenericEventWithObjects(string);
    }

    public static Matcher<GenericEvent> starts(String string)
    {
        return Matchers.isGenericEventWithObjectsThatMatch(generalStringMatcher(startsWith(string)));
    }

    public static Matcher<GenericEvent> ends(String string)
    {
        return Matchers.isGenericEventWithObjectsThatMatch(generalStringMatcher(endsWith(string)));
    }

    private static Matcher<Object> generalStringMatcher(final Matcher<String> matcher)
    {
        return new BaseMatcher<Object>()
        {
            @Override
            public boolean matches(Object item)
            {
                return item instanceof String && matcher.matches(item);
            }

            @Override
            public void describeTo(Description description)
            {

            }
        };
    }

    public static void assertThatOutcomeIs(String[] events, Check check, final Outcome outcome)
    {
        Observable<? extends Event> observable = generateEvents(events);
        Observable<Result> resultObservable = observable.lift(new EnforceCheck<>(check));

        TestSubscriber<Result> testSubscriber = new TestSubscriber<>();
        resultObservable.subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        List<Result> results = testSubscriber.getOnNextEvents();

        assertNotNull("Results list is null!", results);

        assertEquals("Received more than one result!", 1, results.size());

        Result result = results.get(0);

        assertNotNull("Result is null!", result);

        assertEquals("Test failed, report is '"+result.getMessage()+"'", outcome, result.getOutcome());
    }




    public static Check alwaysSuccessCheck()
    {
        return new Check(new CheckSubscriber()
        {
            @Override
            public Result getFinalResult()
            {
                return new Result(Outcome.SUCCESS, "Always Success Report");
            }

            @Override
            public void onNext(Event event)
            {
                endCheck();
            }
        });
    }

    public static Check alwaysFailureCheck()
    {
        return new Check(new CheckSubscriber()
        {
            @Override
            public Result getFinalResult()
            {
                return new Result(Outcome.FAILURE, "Always Failure Report");
            }

            @Override
            public void onNext(Event event)
            {
                endCheck();
            }
        });
    }

    public static Check alwaysWarningCheck()
    {
        return new Check(new CheckSubscriber()
        {
            @Override
            public Result getFinalResult()
            {
                return new Result(Outcome.WARNING, "Always Warning Report");
            }

            @Override
            public void onNext(Event event)
            {
                endCheck();
            }
        });
    }
}
