package it.polimi.testing.temporalassertions.core;

import org.junit.Test;

import static it.polimi.testing.temporalassertions.core.RxTestUtils.assertThatOutcomeIs;
import static it.polimi.testing.temporalassertions.core.RxTestUtils.is;
import static it.polimi.testing.temporalassertions.core.AnEventThat.anEventThat;

public class AnEventThatTest
{
    /**********************************************
     * CanHappenOnlyAfter Tests
     **********************************************/

    @Test
    public void testCanHappenOnlyAfter_M1afterM2()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anEventThat(is("D")).canHappenOnlyAfter(anEventThat(is("B")));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testCanHappenOnlyAfter_M1beforeM2()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anEventThat(is("B")).canHappenOnlyAfter(anEventThat(is("E")));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testCanHappenOnlyAfter_NoM1()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anEventThat(is("G")).canHappenOnlyAfter(anEventThat(is("B")));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testCanHappenOnlyAfter_NoM1NoM2()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anEventThat(is("G")).canHappenOnlyAfter(anEventThat(is("H")));
        assertThatOutcomeIs(events, check, Outcome.WARNING);
    }

    /**********************************************
     * CanHappenOnlyBefore Tests
     **********************************************/

    @Test
    public void testCanHappenOnlyBefore_M1afterM2()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anEventThat(is("D")).canHappenOnlyBefore(anEventThat(is("B")));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testCanHappenOnlyBefore_M1beforeM2()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anEventThat(is("B")).canHappenOnlyBefore(anEventThat(is("E")));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testCanHappenOnlyBefore_NoM1()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anEventThat(is("G")).canHappenOnlyBefore(anEventThat(is("B")));
        assertThatOutcomeIs(events, check, Outcome.WARNING);
    }

    @Test
    public void testCanHappenOnlyBefore_NoM1NoM2()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anEventThat(is("G")).canHappenOnlyBefore(anEventThat(is("H")));
        assertThatOutcomeIs(events, check, Outcome.WARNING);
    }

    @Test
    public void testCanHappenOnlyBefore_MultipleM1Before()
    {
        String[] events = new String[]{"A", "B", "A", "D", "A", "E", "F"};
        Check check = anEventThat(is("A")).canHappenOnlyBefore(anEventThat(is("E")));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testCanHappenOnlyBefore_MultipleM1BeforeAndAfter()
    {
        String[] events = new String[]{"A", "B", "A", "D", "A", "E", "F", "F", "A", "B"};
        Check check = anEventThat(is("A")).canHappenOnlyBefore(anEventThat(is("E")));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testCanHappenOnlyBefore_MultipleM1AndM2Correct()
    {
        String[] events = new String[]{"A", "B", "C", "B", "B", "A", "A", "A", "B", "B"};
        Check check = anEventThat(is("A")).canHappenOnlyBefore(anEventThat(is("B")));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testCanHappenOnlyBefore_MultipleM1AndM2Failure()
    {
        String[] events = new String[]{"A", "B", "C", "B", "B", "A", "A", "A", "B", "A"};
        Check check = anEventThat(is("A")).canHappenOnlyBefore(anEventThat(is("B")));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    /**********************************************
     * CanHappenOnlyBetween Tests
     **********************************************/

    @Test
    public void testCanHappenOnlyBetween_singleM1between()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anEventThat(is("D")).canHappenOnlyBetween(anEventThat(is("B")), anEventThat(is("E")));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testCanHappenOnlyBetween_M1after()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anEventThat(is("E")).canHappenOnlyBetween(anEventThat(is("A")), anEventThat(is("D")));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testCanHappenOnlyBetween_M1before()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anEventThat(is("B")).canHappenOnlyBetween(anEventThat(is("D")), anEventThat(is("F")));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testCanHappenOnlyBetween_NoM1()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anEventThat(is("G")).canHappenOnlyBetween(anEventThat(is("A")), anEventThat(is("E")));
        assertThatOutcomeIs(events, check, Outcome.WARNING);
    }

    @Test
    public void testCanHappenOnlyBetween_NoM2()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anEventThat(is("C")).canHappenOnlyBetween(anEventThat(is("H")), anEventThat(is("F")));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testCanHappenOnlyBetween_NoM3()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anEventThat(is("D")).canHappenOnlyBetween(anEventThat(is("B")), anEventThat(is("H")));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testCanHappenOnlyBetween_MultipleM1Between()
    {
        String[] events = new String[]{"A", "D", "C", "D", "D", "F"};
        Check check = anEventThat(is("D")).canHappenOnlyBetween(anEventThat(is("A")), anEventThat(is("F")));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testCanHappenOnlyBetween_MultipleMatchesSuccess()
    {
        String[] events = new String[]{"A", "B", "C", "C", "A", "A", "B", "C", "A", "A"};
        Check check = anEventThat(is("B")).canHappenOnlyBetween(anEventThat(is("A")), anEventThat(is("C")));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testCanHappenOnlyBetween_MultipleMatchesFailure()
    {
        String[] events = new String[]{"A", "B", "C", "C", "A", "A", "B", "A", "A", "A"};
        Check check = anEventThat(is("B")).canHappenOnlyBetween(anEventThat(is("A")), anEventThat(is("C")));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testCanHappenOnlyBetween_MultipleMatchesFailure2()
    {
        String[] events = new String[]{"A", "F", "B", "B", "C", "F", "B", "A", "B", "C"};
        Check check = anEventThat(is("B")).canHappenOnlyBetween(anEventThat(is("A")), anEventThat(is("C")));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    /**********************************************
     * CannotHappenBetween Tests
     **********************************************/

    @Test
    public void testCannotHappenBetween_Correct_EndWithClosedPair()
    {
        String[] events = new String[]{"B", "A", "F", "G", "C", "B", "B", "A", "C", "B"};
        Check check = anEventThat(is("B")).cannotHappenBetween(anEventThat(is("A")), anEventThat(is("C")));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testCannotHappenBetween_Correct_EndWithOpenPair1()
    {
        String[] events = new String[]{"B", "A", "F", "G", "C", "B", "B", "A", "G", "F"};
        Check check = anEventThat(is("B")).cannotHappenBetween(anEventThat(is("A")), anEventThat(is("C")));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testCannotHappenBetween_Correct_EndWithOpenPair2()
    {
        String[] events = new String[]{"X", "A", "F", "G", "C", "X", "X", "A", "G", "B"};
        Check check = anEventThat(is("B")).cannotHappenBetween(anEventThat(is("A")), anEventThat(is("C")));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testCannotHappenBetween_Failure()
    {
        String[] events = new String[]{"B", "A", "F", "B", "C", "B", "B", "A", "C", "B"};
        Check check = anEventThat(is("B")).cannotHappenBetween(anEventThat(is("A")), anEventThat(is("C")));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testCannotHappenBetween_NoM1()
    {
        String[] events = new String[]{"X", "A", "F", "X", "C", "X", "X", "A", "C", "X"};
        Check check = anEventThat(is("B")).cannotHappenBetween(anEventThat(is("A")), anEventThat(is("C")));
        assertThatOutcomeIs(events, check, Outcome.WARNING);
    }
}
