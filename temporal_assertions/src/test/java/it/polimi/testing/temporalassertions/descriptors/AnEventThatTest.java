package it.polimi.testing.temporalassertions.descriptors;

import org.junit.Test;

import it.polimi.testing.temporalassertions.checks.Check;
import it.polimi.testing.temporalassertions.checks.Outcome;

import static it.polimi.testing.temporalassertions.RxTestUtils.assertThatOutcomeIs;
import static it.polimi.testing.temporalassertions.RxTestUtils.is;
import static it.polimi.testing.temporalassertions.descriptors.AnEventThat.anEventThat;

public class AnEventThatTest
{
    /**********************************************
     * Exists Tests
     **********************************************/

    @Test
    public void testExists_Success()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anEventThat(is("D")).exists();
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testExists_Failure()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anEventThat(is("X")).exists();
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    /**********************************************
     * CanOnlyHappenAfter Tests
     **********************************************/

    @Test
    public void testCanOnlyHappenAfter_M1afterM2()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anEventThat(is("D")).canOnlyHappenAfter(anEventThat(is("B")));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testCanOnlyHappenAfter_M1beforeM2()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anEventThat(is("B")).canOnlyHappenAfter(anEventThat(is("E")));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testCanOnlyHappenAfter_NoM1()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anEventThat(is("G")).canOnlyHappenAfter(anEventThat(is("B")));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testCanOnlyHappenAfter_NoM1NoM2()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anEventThat(is("G")).canOnlyHappenAfter(anEventThat(is("H")));
        assertThatOutcomeIs(events, check, Outcome.WARNING);
    }

    /**********************************************
     * CanOnlyHappenBefore Tests
     **********************************************/

    @Test
    public void testCanOnlyHappenBefore_M1afterM2()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anEventThat(is("D")).canOnlyHappenBefore(anEventThat(is("B")));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testCanOnlyHappenBefore_M1beforeM2()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anEventThat(is("B")).canOnlyHappenBefore(anEventThat(is("E")));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testCanOnlyHappenBefore_NoM1()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anEventThat(is("G")).canOnlyHappenBefore(anEventThat(is("B")));
        assertThatOutcomeIs(events, check, Outcome.WARNING);
    }

    @Test
    public void testCanOnlyHappenBefore_NoM1NoM2()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anEventThat(is("G")).canOnlyHappenBefore(anEventThat(is("H")));
        assertThatOutcomeIs(events, check, Outcome.WARNING);
    }

    @Test
    public void testCanOnlyHappenBefore_MultipleM1Before()
    {
        String[] events = new String[]{"A", "B", "A", "D", "A", "E", "F"};
        Check check = anEventThat(is("A")).canOnlyHappenBefore(anEventThat(is("E")));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testCanOnlyHappenBefore_MultipleM1BeforeAndAfter()
    {
        String[] events = new String[]{"A", "B", "A", "D", "A", "E", "F", "F", "A", "B"};
        Check check = anEventThat(is("A")).canOnlyHappenBefore(anEventThat(is("E")));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testCanOnlyHappenBefore_MultipleM1AndM2Correct()
    {
        String[] events = new String[]{"A", "B", "C", "B", "B", "A", "A", "A", "B", "B"};
        Check check = anEventThat(is("A")).canOnlyHappenBefore(anEventThat(is("B")));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testCanOnlyHappenBefore_MultipleM1AndM2Failure()
    {
        String[] events = new String[]{"A", "B", "C", "B", "B", "A", "A", "A", "B", "A"};
        Check check = anEventThat(is("A")).canOnlyHappenBefore(anEventThat(is("B")));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    /**********************************************
     * CanOnlyHappenBetween Tests
     **********************************************/

    @Test
    public void testCanOnlyHappenBetween_singleM1between()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anEventThat(is("D")).canOnlyHappenBetween(anEventThat(is("B")), anEventThat(is("E")));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testCanOnlyHappenBetween_M1after()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anEventThat(is("E")).canOnlyHappenBetween(anEventThat(is("A")), anEventThat(is("D")));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testCanOnlyHappenBetween_M1before()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anEventThat(is("B")).canOnlyHappenBetween(anEventThat(is("D")), anEventThat(is("F")));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testCanOnlyHappenBetween_NoM1()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anEventThat(is("G")).canOnlyHappenBetween(anEventThat(is("A")), anEventThat(is("E")));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testCanOnlyHappenBetween_NoM2()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anEventThat(is("C")).canOnlyHappenBetween(anEventThat(is("H")), anEventThat(is("F")));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testCanOnlyHappenBetween_NoM3()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anEventThat(is("D")).canOnlyHappenBetween(anEventThat(is("B")), anEventThat(is("H")));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testCanOnlyHappenBetween_MultipleM1Between()
    {
        String[] events = new String[]{"A", "D", "C", "D", "D", "F"};
        Check check = anEventThat(is("D")).canOnlyHappenBetween(anEventThat(is("A")), anEventThat(is("F")));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testCanOnlyHappenBetween_MultipleMatchesSuccess()
    {
        String[] events = new String[]{"A", "B", "C", "C", "A", "A", "B", "C", "A", "A"};
        Check check = anEventThat(is("B")).canOnlyHappenBetween(anEventThat(is("A")), anEventThat(is("C")));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testCanOnlyHappenBetween_MultipleMatchesFailure()
    {
        String[] events = new String[]{"A", "B", "C", "C", "A", "A", "B", "A", "A", "A"};
        Check check = anEventThat(is("B")).canOnlyHappenBetween(anEventThat(is("A")), anEventThat(is("C")));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    /**********************************************
     * MustHappenAfter Tests
     **********************************************/

    @Test
    public void testMustHappenAfter_M1afterM2()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anEventThat(is("D")).mustHappenAfter(anEventThat(is("B")));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testMustHappenAfter_M1beforeM2()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anEventThat(is("B")).mustHappenAfter(anEventThat(is("E")));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testMustHappenAfter_NoM1()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anEventThat(is("G")).mustHappenAfter(anEventThat(is("B")));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testMustHappenAfter_NoM1NoM2()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anEventThat(is("G")).mustHappenAfter(anEventThat(is("H")));
        assertThatOutcomeIs(events, check, Outcome.WARNING);
    }

    @Test
    public void testMustHappenAfter_MultipleM1()
    {
        String[] events = new String[]{"A", "B", "D", "D", "D", "D"};
        Check check = anEventThat(is("D")).mustHappenAfter(anEventThat(is("B")));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testMustHappenAfter_M1AndM2Alternated()
    {
        String[] events = new String[]{"A", "C", "B", "C", "C", "A", "C", "C", "B"};
        Check check = anEventThat(is("B")).mustHappenAfter(anEventThat(is("A")));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testMustHappenAfter_LastM2WithoutM1()
    {
        String[] events = new String[]{"A", "C", "B", "C", "C", "A", "C", "C", "C"};
        Check check = anEventThat(is("B")).mustHappenAfter(anEventThat(is("A")));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }
}
