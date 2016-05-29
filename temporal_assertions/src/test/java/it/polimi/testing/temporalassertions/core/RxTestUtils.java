package it.polimi.testing.temporalassertions.core;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.List;

import it.polimi.testing.temporalassertions.events.Event;
import it.polimi.testing.temporalassertions.events.GenericEvent;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.observers.TestSubscriber;

import static it.polimi.testing.temporalassertions.events.GenericEvent.isGenericEventWithObjects;
import static it.polimi.testing.temporalassertions.events.GenericEvent.isGenericEventWithObjectsThatMatch;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.hamcrest.core.StringStartsWith.startsWith;

/**
 * Some utilities for testing the library
 */
public abstract class RxTestUtils
{
    /**
     * Generates "strings.length" generic events, each containing the corresponding string
     * @param strings the strings contained by each event
     * @return the observable of events
     */
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

    /**
     * Matcher used with generateEvents() to match an event with the given string
     * @param string the event string
     * @return the Hamcrest matcher
     */
    public static Matcher<GenericEvent> is(String string)
    {
        return isGenericEventWithObjects(string);
    }

    /**
     * Matcher used with generateEvents() to match an event with a string that starts with the given one
     * @param string the event string initial part
     * @return the Hamcrest matcher
     */
    public static Matcher<GenericEvent> starts(String string)
    {
        return isGenericEventWithObjectsThatMatch(generalStringMatcher(startsWith(string)));
    }

    /**
     * Matcher used with generateEvents() to match an event with a string that ends with the given one
     * @param string the event string final part
     * @return the Hamcrest matcher
     */
    public static Matcher<GenericEvent> ends(String string)
    {
        return isGenericEventWithObjectsThatMatch(generalStringMatcher(endsWith(string)));
    }

    /**
     * Wrapper matcher that applies a String matcher to an object (i.e. only if the object is a string)
     * @param matcher the string matcher
     * @return the object matcher
     */
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

    /**
     * Assertions for testing a consistency check
     * @param events all the events of the stream
     * @param check the check to be applied
     * @param outcome the expected outcome
     */
    public static void assertThatOutcomeIs(String[] events, final Check check, final Outcome outcome)
    {
        Observable<? extends Event> observable = generateEvents(events);
        Observable<Result> resultObservable = observable.lift(new EnforceCheck<>(check)).map(new Func1<Result, Result>()
        {
            @Override
            public Result call(Result result)
            {
                result.setLinkedCheckDescription(check.getDescription());
                result.setUserFailureMessage("This is the failure message");
                return result;
            }
        });

        TestSubscriber<Result> testSubscriber = new TestSubscriber<>();
        resultObservable.subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        List<Result> results = testSubscriber.getOnNextEvents();

        assertNotNull("Results list is null", results);

        assertEquals("Received more than one result", 1, results.size());

        Result result = results.get(0);

        assertNotNull("Result is null", result);

        System.out.println(result);
        System.out.println();

        assertEquals("The outcome is wrong", outcome, result.getOutcome());
    }

    /**
     * A check that always returns SUCCESS
     * @return the check
     */
    public static Check alwaysSuccessCheck()
    {
        class AlwaysSuccess extends Check
        {
            protected AlwaysSuccess()
            {
                super("Always succeeds", new CheckSubscriber()
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
        }
        return new AlwaysSuccess();
    }

    /**
     * A check that always returns FAILURE
     * @return the check
     */
    public static Check alwaysFailureCheck()
    {
        class AlwaysFail extends Check
        {
            protected AlwaysFail()
            {
                super("Always fails", new CheckSubscriber()
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
        }
        return new AlwaysFail();
    }

    /**
     * A check that always returns WARNING
     * @return the check
     */
    public static Check alwaysWarningCheck()
    {
        class AlwaysWarn extends Check
        {
            protected AlwaysWarn()
            {
                super("Always warns", new CheckSubscriber()
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
        return new AlwaysWarn();
    }
}
