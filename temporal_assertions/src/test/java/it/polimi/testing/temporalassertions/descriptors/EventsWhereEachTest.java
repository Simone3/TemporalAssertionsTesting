package it.polimi.testing.temporalassertions.descriptors;


import org.junit.Test;

import it.polimi.testing.temporalassertions.core.Check;
import it.polimi.testing.temporalassertions.core.Outcome;

import static it.polimi.testing.temporalassertions.RxTestUtils.assertThatOutcomeIs;
import static it.polimi.testing.temporalassertions.RxTestUtils.is;
import static it.polimi.testing.temporalassertions.core.AllHold.allHold;
import static it.polimi.testing.temporalassertions.core.AnEventThat.anEventThat;
import static it.polimi.testing.temporalassertions.core.AtLeast.atLeast;
import static it.polimi.testing.temporalassertions.core.AtMost.atMost;
import static it.polimi.testing.temporalassertions.core.Exactly.exactly;

public class EventsWhereEachTest
{
    /**********************************************
     * Exactly + MustHappenAfter Tests
     **********************************************/

    @Test
    public void testExactlyMustHappenAfter_MultipleM2Correct()
    {
        String[] events = new String[]{"A", "B", "C", "D", "D", "F", "B", "D", "G", "D", "H"};
        Check check = exactly(2).eventsWhereEach(is("D")).mustHappenAfter(anEventThat(is("B")));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testExactlyMustHappenAfter_MultipleM2TooFewEnd()
    {
        String[] events = new String[]{"A", "B", "C", "D", "D", "F", "B", "D", "G", "H", "I"};
        Check check = exactly(2).eventsWhereEach(is("D")).mustHappenAfter(anEventThat(is("B")));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testExactlyMustHappenAfter_MultipleM2TooManyEnd()
    {
        String[] events = new String[]{"A", "B", "C", "D", "D", "F", "B", "D", "G", "D", "D"};
        Check check = exactly(2).eventsWhereEach(is("D")).mustHappenAfter(anEventThat(is("B")));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testExactlyMustHappenAfter_MultipleM2TooFewMiddle()
    {
        String[] events = new String[]{"A", "B", "C", "D", "C", "F", "B", "D", "G", "D", "H"};
        Check check = exactly(2).eventsWhereEach(is("D")).mustHappenAfter(anEventThat(is("B")));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testExactlyMustHappenAfter_MultipleM2TooManyMiddle()
    {
        String[] events = new String[]{"A", "B", "C", "D", "D", "F", "D", "F", "B", "D", "G", "D", "H"};
        Check check = exactly(2).eventsWhereEach(is("D")).mustHappenAfter(anEventThat(is("B")));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testExactlyMustHappenAfter_NoM2()
    {
        String[] events = new String[]{"A", "A", "C", "D", "D", "F", "A", "D", "G", "D", "D"};
        Check check = exactly(2).eventsWhereEach(is("D")).mustHappenAfter(anEventThat(is("B")));
        assertThatOutcomeIs(events, check, Outcome.WARNING);
    }

    /**********************************************
     * AtLeast + MustHappenAfter Tests
     **********************************************/

    @Test
    public void testAtLeastMustHappenAfter_MultipleM2Correct()
    {
        String[] events = new String[]{"A", "B", "D", "C", "D", "D", "F", "B", "D", "G", "D", "H"};
        Check check = atLeast(2).eventsWhereEach(is("D")).mustHappenAfter(anEventThat(is("B")));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testAtLeastMustHappenAfter_MultipleM2LessEnd()
    {
        String[] events = new String[]{"A", "B", "C", "D", "D", "F", "B", "D", "G", "H", "I"};
        Check check = atLeast(2).eventsWhereEach(is("D")).mustHappenAfter(anEventThat(is("B")));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testAtLeastMustHappenAfter_MultipleM2MoreEnd()
    {
        String[] events = new String[]{"A", "B", "C", "D", "D", "F", "B", "D", "G", "D", "D"};
        Check check = atLeast(2).eventsWhereEach(is("D")).mustHappenAfter(anEventThat(is("B")));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testAtLeastMustHappenAfter_MultipleM2LessMiddle()
    {
        String[] events = new String[]{"A", "B", "C", "D", "C", "F", "B", "D", "G", "D", "H"};
        Check check = atLeast(2).eventsWhereEach(is("D")).mustHappenAfter(anEventThat(is("B")));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testAtLeastMustHappenAfter_MultipleM2MoreMiddle()
    {
        String[] events = new String[]{"A", "B", "C", "D", "D", "F", "D", "F", "B", "D", "G", "D", "H"};
        Check check = atLeast(2).eventsWhereEach(is("D")).mustHappenAfter(anEventThat(is("B")));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testAtLeastMustHappenAfter_NoM2()
    {
        String[] events = new String[]{"A", "A", "C", "D", "D", "F", "A", "D", "G", "D", "D"};
        Check check = atLeast(2).eventsWhereEach(is("D")).mustHappenAfter(anEventThat(is("B")));
        assertThatOutcomeIs(events, check, Outcome.WARNING);
    }

    /**********************************************
     * AtMost + MustHappenAfter Tests
     **********************************************/

    @Test
    public void testAtMostMustHappenAfter_MultipleM2Correct()
    {
        String[] events = new String[]{"A", "B", "D", "C", "F", "B", "D", "G", "D", "H"};
        Check check = atMost(2).eventsWhereEach(is("D")).mustHappenAfter(anEventThat(is("B")));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testAtMostMustHappenAfter_MultipleM2LessEnd()
    {
        String[] events = new String[]{"A", "B", "C", "D", "D", "F", "B", "D", "G", "H", "I"};
        Check check = atMost(2).eventsWhereEach(is("D")).mustHappenAfter(anEventThat(is("B")));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testAtMostMustHappenAfter_MultipleM2MoreEnd()
    {
        String[] events = new String[]{"A", "B", "C", "D", "D", "F", "B", "D", "G", "D", "D"};
        Check check = atMost(2).eventsWhereEach(is("D")).mustHappenAfter(anEventThat(is("B")));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testAtMostMustHappenAfter_MultipleM2LessMiddle()
    {
        String[] events = new String[]{"A", "B", "C", "D", "C", "F", "B", "D", "G", "D", "H"};
        Check check = atMost(2).eventsWhereEach(is("D")).mustHappenAfter(anEventThat(is("B")));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testAtMostMustHappenAfter_MultipleM2MoreMiddle()
    {
        String[] events = new String[]{"A", "B", "C", "D", "D", "F", "D", "F", "B", "D", "G", "D", "H"};
        Check check = atMost(2).eventsWhereEach(is("D")).mustHappenAfter(anEventThat(is("B")));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testAtMostMustHappenAfter_NoM2()
    {
        String[] events = new String[]{"A", "A", "C", "D", "D", "F", "A", "D", "G", "D", "D"};
        Check check = atMost(2).eventsWhereEach(is("D")).mustHappenAfter(anEventThat(is("B")));
        assertThatOutcomeIs(events, check, Outcome.WARNING);
    }

    /**********************************************
     * Exactly + MustHappenBefore Tests
     **********************************************/

    @Test
    public void testExactlyMustHappenBefore_MultipleM2Correct()
    {
        String[] events = new String[]{"A", "B", "C", "B", "D", "B", "B", "E", "G", "D", "H"};
        Check check = exactly(2).eventsWhereEach(is("B")).mustHappenBefore(anEventThat(is("D")));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testExactlyMustHappenBefore_MultipleM2TooFew()
    {
        String[] events = new String[]{"B", "B", "C", "F", "D", "F", "B", "X", "G", "D", "I"};
        Check check = exactly(2).eventsWhereEach(is("B")).mustHappenBefore(anEventThat(is("D")));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testExactlyMustHappenBefore_MultipleM2TooMany()
    {
        String[] events = new String[]{"B", "B", "C", "F", "D", "B", "B", "X", "G", "B", "D", "I"};
        Check check = exactly(2).eventsWhereEach(is("B")).mustHappenBefore(anEventThat(is("D")));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testExactlyMustHappenBefore_NoM2()
    {
        String[] events = new String[]{"A", "A", "C", "B", "B", "F", "A", "B", "G", "B", "B"};
        Check check = exactly(2).eventsWhereEach(is("B")).mustHappenBefore(anEventThat(is("D")));
        assertThatOutcomeIs(events, check, Outcome.WARNING);
    }

    /**********************************************
     * AtLeast + MustHappenBefore Tests
     **********************************************/

    @Test
    public void testAtLeastMustHappenBefore_MultipleM2Correct()
    {
        String[] events = new String[]{"A", "B", "C", "B", "D", "B", "B", "E", "B", "D", "B"};
        Check check = atLeast(2).eventsWhereEach(is("B")).mustHappenBefore(anEventThat(is("D")));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testAtLeastMustHappenBefore_MultipleM2Less()
    {
        String[] events = new String[]{"A", "B", "C", "B", "D", "F", "B", "E", "G", "D", "B"};
        Check check = atLeast(2).eventsWhereEach(is("B")).mustHappenBefore(anEventThat(is("D")));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testAtLeastMustHappenBefore_NoM2()
    {
        String[] events = new String[]{"A", "A", "C", "B", "B", "F", "A", "B", "G", "B", "B"};
        Check check = atLeast(2).eventsWhereEach(is("B")).mustHappenBefore(anEventThat(is("D")));
        assertThatOutcomeIs(events, check, Outcome.WARNING);
    }

    /**********************************************
     * AtMost + MustHappenBefore Tests
     **********************************************/

    @Test
    public void testAtMostMustHappenBefore_MultipleM2Correct()
    {
        String[] events = new String[]{"A", "B", "C", "B", "D", "F", "B", "E", "G", "D", "B"};
        Check check = atMost(2).eventsWhereEach(is("B")).mustHappenBefore(anEventThat(is("D")));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testAtMostMustHappenBefore_MultipleM2More()
    {
        String[] events = new String[]{"A", "B", "C", "B", "D", "B", "B", "E", "B", "G", "D", "F"};
        Check check = atMost(2).eventsWhereEach(is("B")).mustHappenBefore(anEventThat(is("D")));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testAtMostMustHappenBefore_NoM2()
    {
        String[] events = new String[]{"A", "A", "C", "B", "B", "F", "A", "B", "G", "B", "B"};
        Check check = atMost(2).eventsWhereEach(is("B")).mustHappenBefore(anEventThat(is("D")));
        assertThatOutcomeIs(events, check, Outcome.WARNING);
    }

    /**********************************************
     * Exactly + MustHappenBetween Tests
     **********************************************/

    @Test
    public void testExactlyMustHappenBetween_MultipleM2Correct()
    {
        String[] events = new String[]{"A", "B", "D", "C", "D", "F", "B", "B", "D", "D", "F", "B"};
        Check check = exactly(2).eventsWhereEach(is("D")).mustHappenBetween(anEventThat(is("B")), anEventThat(is("F")));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testExactlyMustHappenBetween_BetweenNotEqualToAfterAndBetween()
    {
        // This test "explains" why "between" is not "after && before" but a different implementation

        String[] events = new String[]{"A", "D", "B", "D", "C", "D", "F", "D", "C"};

        Check check = exactly(3).eventsWhereEach(is("D")).mustHappenBetween(anEventThat(is("B")), anEventThat(is("F")));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);

        check = allHold(exactly(3).eventsWhereEach(is("D")).mustHappenAfter(anEventThat(is("B"))), exactly(3).eventsWhereEach(is("D")).mustHappenBefore(anEventThat(is("F"))));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testExactlyMustHappenBetween_MultipleM2TooFew()
    {
        String[] events = new String[]{"A", "B", "D", "C", "D", "F", "B", "B", "D", "G", "F", "B"};
        Check check = exactly(2).eventsWhereEach(is("D")).mustHappenBetween(anEventThat(is("B")), anEventThat(is("F")));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testExactlyMustHappenBetween_MultipleM2TooMany()
    {
        String[] events = new String[]{"A", "B", "D", "C", "D", "F", "B", "B", "D", "G", "D", "D", "F", "B"};
        Check check = exactly(2).eventsWhereEach(is("D")).mustHappenBetween(anEventThat(is("B")), anEventThat(is("F")));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testExactlyMustHappenBetween_MultipleM2NoM3()
    {
        String[] events = new String[]{"A", "B", "D", "C", "D", "F", "B", "B", "D", "G", "D", "D", "G", "T"};
        Check check = exactly(2).eventsWhereEach(is("D")).mustHappenBetween(anEventThat(is("B")), anEventThat(is("F")));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testExactlyMustHappenBetween_NoM2()
    {
        String[] events = new String[]{"A", "A", "C", "D", "D", "F", "A", "D", "G", "D", "D"};
        Check check = exactly(2).eventsWhereEach(is("D")).mustHappenBetween(anEventThat(is("B")), anEventThat(is("F")));
        assertThatOutcomeIs(events, check, Outcome.WARNING);
    }

    /**********************************************
     * AtLeast + MustHappenBetween Tests
     **********************************************/

    @Test
    public void testAtLeastMustHappenBetween_MultipleM2Correct()
    {
        String[] events = new String[]{"A", "B", "D", "C", "D", "D", "C", "F", "B", "B", "D", "D", "F", "B"};
        Check check = atLeast(2).eventsWhereEach(is("D")).mustHappenBetween(anEventThat(is("B")), anEventThat(is("F")));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testAtLeastMustHappenBetween_MultipleM2Less()
    {
        String[] events = new String[]{"A", "B", "D", "C", "D", "F", "B", "B", "C", "D", "F", "B"};
        Check check = atLeast(2).eventsWhereEach(is("D")).mustHappenBetween(anEventThat(is("B")), anEventThat(is("F")));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testAtLeastMustHappenBetween_MultipleM2NoM3()
    {
        String[] events = new String[]{"A", "B", "D", "C", "D", "F", "B", "B", "D", "G", "C", "C", "G", "T"};
        Check check = exactly(2).eventsWhereEach(is("D")).mustHappenBetween(anEventThat(is("B")), anEventThat(is("F")));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testAtLeastMustHappenBetween_NoM2()
    {
        String[] events = new String[]{"A", "A", "C", "D", "D", "F", "A", "D", "G", "D", "D"};
        Check check = atLeast(2).eventsWhereEach(is("D")).mustHappenBetween(anEventThat(is("B")), anEventThat(is("F")));
        assertThatOutcomeIs(events, check, Outcome.WARNING);
    }

    /**********************************************
     * AtMost + MustHappenBetween Tests
     **********************************************/

    @Test
    public void testAtMostMustHappenBetween_MultipleM2Correct()
    {
        String[] events = new String[]{"A", "B", "D", "C", "D", "F", "B", "B", "D", "F", "B"};
        Check check = atMost(2).eventsWhereEach(is("D")).mustHappenBetween(anEventThat(is("B")), anEventThat(is("F")));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testAtMostMustHappenBetween_MultipleM2More()
    {
        String[] events = new String[]{"A", "B", "D", "C", "D", "F", "B", "B", "D", "D", "C", "D", "C", "F", "B"};
        Check check = atMost(2).eventsWhereEach(is("D")).mustHappenBetween(anEventThat(is("B")), anEventThat(is("F")));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testAtMostMustHappenBetween_MultipleM2NoM3()
    {
        String[] events = new String[]{"A", "B", "D", "C", "D", "F", "B", "B", "D", "D", "C", "D", "G", "C", "C", "G", "T"};
        Check check = exactly(2).eventsWhereEach(is("D")).mustHappenBetween(anEventThat(is("B")), anEventThat(is("F")));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testAtMostMustHappenBetween_NoM2()
    {
        String[] events = new String[]{"A", "A", "C", "D", "D", "F", "A", "D", "G", "D", "D"};
        Check check = atMost(2).eventsWhereEach(is("D")).mustHappenBetween(anEventThat(is("B")), anEventThat(is("F")));
        assertThatOutcomeIs(events, check, Outcome.WARNING);
    }
}
