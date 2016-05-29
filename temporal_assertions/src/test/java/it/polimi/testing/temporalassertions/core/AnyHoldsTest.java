package it.polimi.testing.temporalassertions.core;

import org.junit.Test;

import static it.polimi.testing.temporalassertions.RxTestUtils.alwaysFailureCheck;
import static it.polimi.testing.temporalassertions.RxTestUtils.alwaysSuccessCheck;
import static it.polimi.testing.temporalassertions.RxTestUtils.alwaysWarningCheck;
import static it.polimi.testing.temporalassertions.RxTestUtils.assertThatOutcomeIs;
import static it.polimi.testing.temporalassertions.core.AnyHolds.anyHolds;

public class AnyHoldsTest
{
    @Test
    public void testAnyHolds_AllFailures()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anyHolds(alwaysFailureCheck(), alwaysFailureCheck(), alwaysFailureCheck(), alwaysFailureCheck());
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testAnyHolds_OneSuccessMiddle()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anyHolds(alwaysFailureCheck(), alwaysFailureCheck(), alwaysSuccessCheck(), alwaysFailureCheck());
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testAnyHolds_OneSuccessEnd()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anyHolds(alwaysFailureCheck(), alwaysFailureCheck(), alwaysFailureCheck(), alwaysSuccessCheck());
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testAnyHolds_OneSuccessBeginning()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anyHolds(alwaysSuccessCheck(), alwaysFailureCheck(), alwaysFailureCheck(), alwaysFailureCheck());
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testAnyHolds_ManySuccesses()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anyHolds(alwaysFailureCheck(), alwaysSuccessCheck(), alwaysSuccessCheck(), alwaysSuccessCheck(), alwaysFailureCheck());
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testAnyHolds_Warnings()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anyHolds(alwaysFailureCheck(), alwaysWarningCheck(), alwaysFailureCheck(), alwaysFailureCheck(), alwaysWarningCheck());
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testAnyHolds_NestedSuccess()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anyHolds(
                alwaysFailureCheck(),
                anyHolds(alwaysFailureCheck(), alwaysFailureCheck()),
                alwaysFailureCheck(),
                anyHolds(alwaysFailureCheck(), alwaysSuccessCheck(), alwaysFailureCheck()));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testAnyHolds_NestedFailure()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = anyHolds(
                alwaysFailureCheck(),
                anyHolds(alwaysFailureCheck(), alwaysFailureCheck()),
                alwaysFailureCheck(),
                anyHolds(alwaysFailureCheck(), alwaysFailureCheck(), alwaysFailureCheck()));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }
}
