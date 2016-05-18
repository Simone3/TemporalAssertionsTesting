package it.polimi.testing.temporalassertions.monitor;


import org.junit.Test;

import java.util.List;

import it.polimi.testing.temporalassertions.checks.Result;
import it.polimi.testing.temporalassertions.events.Event;
import rx.observers.TestSubscriber;

import static it.polimi.testing.temporalassertions.RxTestUtils.alwaysFailureCheck;
import static it.polimi.testing.temporalassertions.RxTestUtils.alwaysSuccessCheck;
import static it.polimi.testing.temporalassertions.RxTestUtils.generateEvents;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EventMonitorTest
{
    private void pause()
    {
        try{Thread.sleep(2000);}catch(Exception e){/**/}
    }

    private void addObservables()
    {
        EventMonitor eventMonitor = EventMonitor.getInstance();

        eventMonitor.addObservable(generateEvents("A", "A", "A", "A", "A"));
        eventMonitor.addObservable(generateEvents("B", "B", "B"));
        eventMonitor.addObservable(generateEvents("C", "C", "C", "C", "C", "C", "C"));
    }

    private void addChecks()
    {
        EventMonitor eventMonitor = EventMonitor.getInstance();

        eventMonitor.checkThat(alwaysSuccessCheck());
        eventMonitor.checkThat(alwaysFailureCheck());
        eventMonitor.checkThat(alwaysSuccessCheck());
    }

    private void verifyChecks(int expectedResults)
    {
        EventMonitor eventMonitor = EventMonitor.getInstance();

        TestSubscriber<Result> resultsTestSubscriber = new TestSubscriber<>();
        eventMonitor.verify(resultsTestSubscriber);

        resultsTestSubscriber.assertNoErrors();
        List<Result> results = resultsTestSubscriber.getOnNextEvents();
        assertNotNull("Results list is null!", results);
        assertEquals("Wrong number of results!", expectedResults, results.size());
        for(Result result: results)
        {
            assertNotNull("Result is null!", result);
        }
    }

    private void setSubscriber(int expectedEvents)
    {
        EventMonitor eventMonitor = EventMonitor.getInstance();

        TestSubscriber<? super Event> eventsTestSubscriber = new TestSubscriber<>();
        eventMonitor.setSubscriber(eventsTestSubscriber);

        //pause();

        eventsTestSubscriber.assertNoErrors();
        List<? super Event> events = eventsTestSubscriber.getOnNextEvents();
        assertNotNull("Events list is null!", events);
        assertEquals("Wrong number of events!", expectedEvents, events.size());
        for(Object event: events)
        {
            assertNotNull("Event is null!", event);
        }
    }

    @Test
    public void testMonitor_ObservablesChecksVerifySubscriber()
    {
        EventMonitor eventMonitor = EventMonitor.getInstance();

        try
        {
            addObservables();
            addChecks();
            verifyChecks(3);
            setSubscriber(15);
        }
        finally
        {
            eventMonitor.stop();
        }
    }

    @Test
    public void testMonitor_ChecksObservablesVerifySubscriber()
    {
        EventMonitor eventMonitor = EventMonitor.getInstance();

        try
        {
            addChecks();
            addObservables();
            verifyChecks(3);
            setSubscriber(15);
        }
        finally
        {
            eventMonitor.stop();
        }
    }

    @Test
    public void testMonitor_ObservablesChecksSubscriberVerify()
    {
        EventMonitor eventMonitor = EventMonitor.getInstance();

        try
        {
            addObservables();
            addChecks();
            setSubscriber(15);
            verifyChecks(3);
        }
        finally
        {
            eventMonitor.stop();
        }
    }

    @Test
    public void testMonitor_ObservablesSubscriberChecksVerify()
    {
        EventMonitor eventMonitor = EventMonitor.getInstance();

        try
        {
            addObservables();
            setSubscriber(15);
            addChecks();
            verifyChecks(3);
        }
        finally
        {
            eventMonitor.stop();
        }
    }

    @Test
    public void testMonitor_ObservablesSubscriberObservablesSubscriberChecksVerify()
    {
        EventMonitor eventMonitor = EventMonitor.getInstance();

        try
        {
            addObservables();
            setSubscriber(15);
            eventMonitor.addObservable(generateEvents("X", "X", "X", "X"));
            setSubscriber(4);
            /* TODO checks probably receive just the 4 events... */
            addChecks();
            verifyChecks(3);
        }
        finally
        {
            eventMonitor.stop();
        }
    }

    @Test
    public void testMonitor_ObservablesChecksVerifyObservablesChecksVerifySubscriber()
    {
        EventMonitor eventMonitor = EventMonitor.getInstance();

        try
        {
            addObservables();
            addChecks();
            verifyChecks(3);

            eventMonitor.addObservable(generateEvents("X", "X", "X", "X"));
            /* TODO checks probably receive just the 4 events... */
            eventMonitor.checkThat(alwaysFailureCheck());
            eventMonitor.checkThat(alwaysSuccessCheck());
            verifyChecks(2);

            setSubscriber(4);
        }
        finally
        {
            eventMonitor.stop();
        }
    }
}
