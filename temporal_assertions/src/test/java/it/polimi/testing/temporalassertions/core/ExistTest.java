package it.polimi.testing.temporalassertions.core;

import org.junit.Test;

import static it.polimi.testing.temporalassertions.core.AnEventThat.anEventThat;
import static it.polimi.testing.temporalassertions.core.AtLeast.atLeast;
import static it.polimi.testing.temporalassertions.core.AtMost.atMost;
import static it.polimi.testing.temporalassertions.core.Exactly.exactly;
import static it.polimi.testing.temporalassertions.core.Exist.exist;
import static it.polimi.testing.temporalassertions.core.Exist.existsAnEventThat;
import static it.polimi.testing.temporalassertions.core.ExistAfterConstraint.after;
import static it.polimi.testing.temporalassertions.core.ExistBeforeConstraint.before;
import static it.polimi.testing.temporalassertions.core.ExistBetweenConstraint.between;
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

    /**********************************************
     * Exist After Tests
     **********************************************/

    @Test
    public void testExistAfter_Single_Correct()
    {
        String[] events = new String[]{"Y", "X", "A", "B", "C", "A", "D", "A"};
        Check check = exist(after(anEventThat(is("X"))), exactly(3)).eventsWhereEach(is("A"));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testExistAfter_Single_Warning()
    {
        String[] events = new String[]{"A", "B", "C", "A", "D", "A"};
        Check check = exist(after(anEventThat(is("X"))), exactly(3)).eventsWhereEach(is("A"));
        assertThatOutcomeIs(events, check, Outcome.WARNING);
    }

    @Test
    public void testExistAfter_Single_Less()
    {
        String[] events = new String[]{"Y", "X", "A", "B", "C", "A", "D", "D"};
        Check check = exist(after(anEventThat(is("X"))), exactly(3)).eventsWhereEach(is("A"));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testExistAfter_Single_More()
    {
        String[] events = new String[]{"Y", "X", "A", "B", "C", "A", "D", "A", "E", "A", "F"};
        Check check = exist(after(anEventThat(is("X"))), exactly(3)).eventsWhereEach(is("A"));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testExistAfter_Multiple_NotLast()
    {
        String[] events = new String[]{"Y", "X", "A", "B", "C", "A", "D", "A", "E", "A", "F", "X", "A", "A", "F", "X", "A", "A", "A", "X", "F"};
        Check check = exist(after(anEventThat(is("X"))), exactly(3)).eventsWhereEach(is("A"));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testExistAfter_Multiple_Last()
    {
        String[] events = new String[]{"Y", "X", "A", "B", "C", "A", "D", "A", "E", "A", "F", "X", "A", "A", "F", "X", "A", "A", "A"};
        Check check = exist(after(anEventThat(is("X"))), exactly(3)).eventsWhereEach(is("A"));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    /**********************************************
     * Exist Before Tests
     **********************************************/

    @Test
    public void testExistBefore_Single_Correct()
    {
        String[] events = new String[]{"A", "B", "C", "A", "D", "A", "X", "Y"};
        Check check = exist(before(anEventThat(is("X"))), exactly(3)).eventsWhereEach(is("A"));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testExistBefore_Single_Warning()
    {
        String[] events = new String[]{"A", "B", "C", "A", "D", "A"};
        Check check = exist(before(anEventThat(is("X"))), exactly(3)).eventsWhereEach(is("A"));
        assertThatOutcomeIs(events, check, Outcome.WARNING);
    }

    @Test
    public void testExistBefore_Single_Less()
    {
        String[] events = new String[]{"A", "B", "C", "A", "D", "D", "X", "Y"};
        Check check = exist(before(anEventThat(is("X"))), exactly(3)).eventsWhereEach(is("A"));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testExistBefore_Single_More()
    {
        String[] events = new String[]{"A", "B", "C", "A", "D", "A", "E", "A", "F", "X", "Y"};
        Check check = exist(before(anEventThat(is("X"))), exactly(3)).eventsWhereEach(is("A"));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testExistBefore_Multiple()
    {
        String[] events = new String[]{"A", "B", "C", "A", "D", "A", "E", "A", "F", "X", "A", "A", "F", "X", "A", "A", "A", "X", "F"};
        Check check = exist(before(anEventThat(is("X"))), exactly(3)).eventsWhereEach(is("A"));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    /**********************************************
     * Exist Between Tests
     **********************************************/

    @Test
    public void testExistBetween_Single_Correct()
    {
        String[] events = new String[]{"Z", "X", "A", "B", "C", "A", "D", "A", "Y", "Z"};
        Check check = exist(between(anEventThat(is("X")), anEventThat(is("Y"))), exactly(3)).eventsWhereEach(is("A"));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testExistBetween_Single_Warning()
    {
        String[] events = new String[]{"Z", "X", "A", "B", "C", "A", "D", "A", "Z"};
        Check check = exist(between(anEventThat(is("X")), anEventThat(is("Y"))), exactly(3)).eventsWhereEach(is("A"));
        assertThatOutcomeIs(events, check, Outcome.WARNING);
    }

    @Test
    public void testExistBetween_Single_Less()
    {
        String[] events = new String[]{"Z", "X", "A", "B", "C", "A", "D", "D", "Y", "Z"};
        Check check = exist(between(anEventThat(is("X")), anEventThat(is("Y"))), exactly(3)).eventsWhereEach(is("A"));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testExistBetween_Single_More()
    {
        String[] events = new String[]{"Z", "X", "A", "B", "C", "A", "D", "A", "E", "A", "F", "Y", "Z"};
        Check check = exist(between(anEventThat(is("X")), anEventThat(is("Y"))), exactly(3)).eventsWhereEach(is("A"));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testExistBetween_Multiple()
    {
        String[] events = new String[]{"Z", "X", "A", "B", "C", "A", "D", "A", "E", "A", "F", "Y", "X", "A", "A", "F", "Y", "F", "X", "A", "A", "A", "Y", "F"};
        Check check = exist(between(anEventThat(is("X")), anEventThat(is("Y"))), exactly(3)).eventsWhereEach(is("A"));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }
}
