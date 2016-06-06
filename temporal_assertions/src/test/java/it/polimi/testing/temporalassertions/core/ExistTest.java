package it.polimi.testing.temporalassertions.core;

import org.junit.Test;

import static it.polimi.testing.temporalassertions.core.AtLeast.atLeast;
import static it.polimi.testing.temporalassertions.core.AtMost.atMost;
import static it.polimi.testing.temporalassertions.core.Exactly.exactly;
import static it.polimi.testing.temporalassertions.core.Exist.exist;
import static it.polimi.testing.temporalassertions.core.Exist.existsAnEventThat;
import static it.polimi.testing.temporalassertions.core.RxTestUtils.assertThatOutcomeIs;
import static it.polimi.testing.temporalassertions.core.RxTestUtils.is;

public class ExistTest
{
    /**********************************************
     * Exists Tests
     **********************************************/

    @Test
    public void testExists_Success()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = existsAnEventThat(is("D"));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testExists_Failure()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = existsAnEventThat(is("X"));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    /**********************************************
     * Exist Exactly Tests
     **********************************************/

    @Test
    public void testExistExactly_Correct()
    {
        String[] events = new String[]{"A", "B", "C", "A", "D", "A"};
        Check check = exist(exactly(3)).eventsWhereEach(is("A"));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testExistExactly_Less()
    {
        String[] events = new String[]{"A", "B", "C", "A", "D", "D"};
        Check check = exist(exactly(3)).eventsWhereEach(is("A"));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testExistExactly_More()
    {
        String[] events = new String[]{"A", "B", "C", "A", "D", "A", "E", "A", "F"};
        Check check = exist(exactly(3)).eventsWhereEach(is("A"));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    /**********************************************
     * Exist AtLeast Tests
     **********************************************/

    @Test
    public void testExistAtLeast_Correct()
    {
        String[] events = new String[]{"A", "B", "C", "A", "D", "A", "E", "A", "F"};
        Check check = exist(atLeast(3)).eventsWhereEach(is("A"));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testExistAtLeast_Less()
    {
        String[] events = new String[]{"A", "B", "C", "A", "D", "D"};
        Check check = exist(atLeast(3)).eventsWhereEach(is("A"));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    /**********************************************
     * Exist AtMost Tests
     **********************************************/

    @Test
    public void testExistAtMost_Correct()
    {
        String[] events = new String[]{"A", "B", "C", "A", "D", "D"};
        Check check = exist(atMost(3)).eventsWhereEach(is("A"));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testExistAtMost_More()
    {
        String[] events = new String[]{"A", "B", "C", "A", "D", "A", "E", "A", "F"};
        Check check = exist(atMost(3)).eventsWhereEach(is("A"));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }
}
