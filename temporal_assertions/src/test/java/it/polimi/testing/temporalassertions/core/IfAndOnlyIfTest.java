package it.polimi.testing.temporalassertions.core;

import org.junit.Test;

import static it.polimi.testing.temporalassertions.core.IsSatisfied.isSatisfied;
import static it.polimi.testing.temporalassertions.core.RxTestUtils.alwaysFailureCheck;
import static it.polimi.testing.temporalassertions.core.RxTestUtils.alwaysSuccessCheck;
import static it.polimi.testing.temporalassertions.core.RxTestUtils.assertThatOutcomeIs;

public class IfAndOnlyIfTest
{
    @Test
    public void testIfAndOnlyIf_FirstTrueSecondTrue()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = isSatisfied(alwaysSuccessCheck()).iff(alwaysSuccessCheck());
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testIfAndOnlyIf_FirstTrueSecondFalse()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = isSatisfied(alwaysSuccessCheck()).iff(alwaysFailureCheck());
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testIfAndOnlyIf_FirstFalseSecondTrue()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = isSatisfied(alwaysFailureCheck()).iff(alwaysSuccessCheck());
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testIfAndOnlyIf_FirstFalseSecondFalse()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = isSatisfied(alwaysFailureCheck()).iff(alwaysFailureCheck());
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testIfAndOnlyIf_Nested()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check =
                isSatisfied(
                    isSatisfied(alwaysSuccessCheck()).iff(alwaysSuccessCheck()))
                .iff(
                    isSatisfied(alwaysSuccessCheck()).iff(alwaysFailureCheck()));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }
}
