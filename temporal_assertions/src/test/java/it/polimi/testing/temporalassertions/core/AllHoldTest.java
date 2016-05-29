package it.polimi.testing.temporalassertions.core;

import org.junit.Test;

import static it.polimi.testing.temporalassertions.core.RxTestUtils.alwaysFailureCheck;
import static it.polimi.testing.temporalassertions.core.RxTestUtils.alwaysSuccessCheck;
import static it.polimi.testing.temporalassertions.core.RxTestUtils.alwaysWarningCheck;
import static it.polimi.testing.temporalassertions.core.RxTestUtils.assertThatOutcomeIs;
import static it.polimi.testing.temporalassertions.core.AllHold.allHold;

public class AllHoldTest
{
    @Test
    public void testAllHold_AllSuccesses()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = allHold(alwaysSuccessCheck(), alwaysSuccessCheck(), alwaysSuccessCheck(), alwaysSuccessCheck());
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testAllHold_OneFailureMiddle()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = allHold(alwaysSuccessCheck(), alwaysSuccessCheck(), alwaysFailureCheck(), alwaysSuccessCheck());
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testAllHold_OneFailureEnd()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = allHold(alwaysSuccessCheck(), alwaysSuccessCheck(), alwaysSuccessCheck(), alwaysFailureCheck());
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testAllHold_OneFailureBeginning()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = allHold(alwaysFailureCheck(), alwaysSuccessCheck(), alwaysSuccessCheck(), alwaysSuccessCheck());
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testAllHold_ManyFailures()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = allHold(alwaysSuccessCheck(), alwaysFailureCheck(), alwaysFailureCheck(), alwaysFailureCheck(), alwaysSuccessCheck());
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testAllHold_Warnings()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = allHold(alwaysSuccessCheck(), alwaysWarningCheck(), alwaysSuccessCheck(), alwaysSuccessCheck(), alwaysWarningCheck());
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testAllHold_NestedSuccess()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = allHold(
                            alwaysSuccessCheck(),
                            allHold(alwaysSuccessCheck(), alwaysSuccessCheck()),
                            alwaysSuccessCheck(),
                            allHold(alwaysSuccessCheck(), alwaysSuccessCheck(), alwaysSuccessCheck()));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testAllHold_NestedFailure()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = allHold(
                alwaysSuccessCheck(),
                allHold(alwaysSuccessCheck(), alwaysSuccessCheck()),
                alwaysSuccessCheck(),
                allHold(alwaysSuccessCheck(), alwaysFailureCheck(), alwaysSuccessCheck()));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }
}
