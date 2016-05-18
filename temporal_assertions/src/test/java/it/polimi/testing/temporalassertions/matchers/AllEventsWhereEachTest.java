package it.polimi.testing.temporalassertions.matchers;


import org.junit.Test;

import it.polimi.testing.temporalassertions.checks.Check;
import it.polimi.testing.temporalassertions.checks.Outcome;

import static it.polimi.testing.temporalassertions.RxTestUtils.assertThatOutcomeIs;
import static it.polimi.testing.temporalassertions.RxTestUtils.ends;
import static it.polimi.testing.temporalassertions.RxTestUtils.is;
import static it.polimi.testing.temporalassertions.RxTestUtils.starts;
import static it.polimi.testing.temporalassertions.matchers.AllEventsWhereEach.allEventsWhereEach;
import static it.polimi.testing.temporalassertions.quantifiers.AtLeast.atLeast;
import static it.polimi.testing.temporalassertions.quantifiers.AtMost.atMost;
import static it.polimi.testing.temporalassertions.quantifiers.Exactly.exactly;

public class AllEventsWhereEachTest
{
    /**********************************************
     * Are + Exactly Tests
     **********************************************/

    @Test
    public void testAreExactly_Correct()
    {
        String[] events = new String[]{"A", "B", "C", "A", "D", "A"};
        Check check = allEventsWhereEach(is("A")).are(exactly(3));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testAreExactly_Less()
    {
        String[] events = new String[]{"A", "B", "C", "A", "D", "D"};
        Check check = allEventsWhereEach(is("A")).are(exactly(3));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testAreExactly_More()
    {
        String[] events = new String[]{"A", "B", "C", "A", "D", "A", "E", "A", "F"};
        Check check = allEventsWhereEach(is("A")).are(exactly(3));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    /**********************************************
     * Are + AtLeast Tests
     **********************************************/

    @Test
    public void testAreAtLeast_Correct()
    {
        String[] events = new String[]{"A", "B", "C", "A", "D", "A", "E", "A", "F"};
        Check check = allEventsWhereEach(is("A")).are(atLeast(3));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testAreAtLeast_Less()
    {
        String[] events = new String[]{"A", "B", "C", "A", "D", "D"};
        Check check = allEventsWhereEach(is("A")).are(atLeast(3));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    /**********************************************
     * Are + AtMost Tests
     **********************************************/

    @Test
    public void testAreAtMost_Correct()
    {
        String[] events = new String[]{"A", "B", "C", "A", "D", "D"};
        Check check = allEventsWhereEach(is("A")).are(atMost(3));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testAreAtMost_More()
    {
        String[] events = new String[]{"A", "B", "C", "A", "D", "A", "E", "A", "F"};
        Check check = allEventsWhereEach(is("A")).are(atMost(3));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

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
}
