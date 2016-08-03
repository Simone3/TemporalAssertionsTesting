package it.polimi.testing.temporalassertions.core;


import org.junit.Test;

import java.util.Comparator;

import it.polimi.testing.temporalassertions.events.GenericEvent;

import static it.polimi.testing.temporalassertions.core.AllEventsWhereEach.allEventsWhereEach;
import static it.polimi.testing.temporalassertions.core.RxTestUtils.assertThatOutcomeIs;
import static it.polimi.testing.temporalassertions.core.RxTestUtils.ends;
import static it.polimi.testing.temporalassertions.core.RxTestUtils.starts;

public class AllEventsWhereEachTest
{
    /**********************************************
     * MatchInOrder Tests
     **********************************************/

    @Test
    public void testMatchInOrder_Correct()
    {
        String[] events = new String[]{"A1", "A2", "B", "A3", "C", "D", "E", "A4", "F"};
        Check check = allEventsWhereEach(starts("A")).matchInOrder(ends("1"), ends("2"), ends("3"), ends("4"));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testMatchInOrder_MoreEvents()
    {
        String[] events = new String[]{"A1", "A2", "B", "A3", "C", "D", "E", "A4", "F", "A5"};
        Check check = allEventsWhereEach(starts("A")).matchInOrder(ends("1"), ends("2"), ends("3"), ends("4"));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testMatchInOrder_LessEvents()
    {
        String[] events = new String[]{"A1", "A2", "B", "A3", "C", "D", "E", "F", "G", "H"};
        Check check = allEventsWhereEach(starts("A")).matchInOrder(ends("1"), ends("2"), ends("3"), ends("4"));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testMatchInOrder_WrongOrder()
    {
        String[] events = new String[]{"A1", "A3", "B", "A2", "C", "D", "E", "A4", "F"};
        Check check = allEventsWhereEach(starts("A")).matchInOrder(ends("1"), ends("2"), ends("3"), ends("4"));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    /**********************************************
     * AreOrdered Tests
     **********************************************/

    // Helper to get the comparator used in the tests
    private Comparator<GenericEvent> getComparator()
    {
        return new Comparator<GenericEvent>()
        {
            @Override
            public int compare(GenericEvent lhs, GenericEvent rhs)
            {
                // Second digits of the each string contained in the events are in order
                String lhsString = (String) lhs.getObjects()[0];
                String rhsString = (String) rhs.getObjects()[0];
                return lhsString.substring(1, 2).compareTo(rhsString.substring(1, 2));
            }
        };
    }

    @Test
    public void testAreOrdered_Correct()
    {
        String[] events = new String[]{"A1", "A2", "B", "A3", "C", "D", "E", "A4", "F"};
        Check check = allEventsWhereEach(starts("A")).areOrdered(getComparator());
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testAreOrdered_CorrectSame()
    {
        String[] events = new String[]{"A1", "A2", "B", "A2", "C", "A2", "E", "A4", "F"};
        Check check = allEventsWhereEach(starts("A")).areOrdered(getComparator());
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testAreOrdered_WrongOrder()
    {
        String[] events = new String[]{"A1", "A2", "B", "A4", "C", "D", "E", "A3", "F"};
        Check check = allEventsWhereEach(starts("A")).areOrdered(getComparator());
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testAreOrdered_NoEvents()
    {
        String[] events = new String[]{"B", "C", "B", "E", "C", "D", "E", "H", "F"};
        Check check = allEventsWhereEach(starts("A")).areOrdered(getComparator());
        assertThatOutcomeIs(events, check, Outcome.WARNING);
    }
}
