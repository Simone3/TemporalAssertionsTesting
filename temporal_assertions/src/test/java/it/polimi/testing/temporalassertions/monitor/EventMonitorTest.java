package it.polimi.testing.temporalassertions.monitor;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import it.polimi.testing.temporalassertions.BuildConfig;
import it.polimi.testing.temporalassertions.core.EventMonitor;
import it.polimi.testing.temporalassertions.core.Result;
import it.polimi.testing.temporalassertions.events.Event;
import rx.observers.TestSubscriber;

import static it.polimi.testing.temporalassertions.RxTestUtils.alwaysFailureCheck;
import static it.polimi.testing.temporalassertions.RxTestUtils.alwaysSuccessCheck;
import static it.polimi.testing.temporalassertions.RxTestUtils.generateEvents;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class EventMonitorTest
{
    private void addObservables()
    {
        EventMonitor eventMonitor = EventMonitor.getInstance();

        eventMonitor.observe(generateEvents("A", "A", "A", "A", "A"));
        eventMonitor.observe(generateEvents("B", "B", "B"));
        eventMonitor.observe(generateEvents("C", "C", "C", "C", "C", "C", "C"));
    }

    private void addChecks()
    {
        EventMonitor eventMonitor = EventMonitor.getInstance();

        eventMonitor.checkThat("", alwaysSuccessCheck());
        eventMonitor.checkThat("", alwaysFailureCheck());
        eventMonitor.checkThat("", alwaysSuccessCheck());
    }

    private void startVerification(int expectedResults, int expectedEvents)
    {
        EventMonitor eventMonitor = EventMonitor.getInstance();

        TestSubscriber<Result> resultsTestSubscriber = new TestSubscriber<>();
        TestSubscriber<? super Event> eventsTestSubscriber = new TestSubscriber<>();

        eventMonitor.startVerification(eventsTestSubscriber, resultsTestSubscriber);

        resultsTestSubscriber.assertNoErrors();
        List<Result> results = resultsTestSubscriber.getOnNextEvents();
        assertNotNull("Results list is null!", results);
        assertEquals("Wrong number of results!", expectedResults, results.size());
        for(Result result: results)
        {
            assertNotNull("Result is null!", result);
        }

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
    public void testMonitor_ObservablesChecksVerification()
    {
        EventMonitor eventMonitor = EventMonitor.getInstance();
        eventMonitor.initialize();

        try
        {
            addObservables();
            addChecks();
            startVerification(3, 15);
        }
        finally
        {
            eventMonitor.stopVerification();
        }
    }

    @Test
    public void testMonitor_ChecksObservablesVerification()
    {
        EventMonitor eventMonitor = EventMonitor.getInstance();
        eventMonitor.initialize();

        try
        {
            addChecks();
            addObservables();
            startVerification(3, 15);
        }
        finally
        {
            eventMonitor.stopVerification();
        }
    }
}
