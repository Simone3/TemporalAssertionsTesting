package it.polimi.testing.temporalassertions.core;

import org.junit.Test;

import static it.polimi.testing.temporalassertions.core.Not.isNotSatisfied;
import static it.polimi.testing.temporalassertions.core.RxTestUtils.alwaysFailureCheck;
import static it.polimi.testing.temporalassertions.core.RxTestUtils.alwaysSuccessCheck;
import static it.polimi.testing.temporalassertions.core.RxTestUtils.alwaysWarningCheck;
import static it.polimi.testing.temporalassertions.core.RxTestUtils.assertThatOutcomeIs;

public class NotTest
{
    @Test
    public void testNot_InvertSuccess()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = isNotSatisfied(alwaysSuccessCheck());
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testNot_InvertFailure()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = isNotSatisfied(alwaysFailureCheck());
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testNot_InvertWarning()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = isNotSatisfied(alwaysWarningCheck());
        assertThatOutcomeIs(events, check, Outcome.WARNING);
    }

    @Test
    public void testNot_InvertSelf()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = isNotSatisfied(isNotSatisfied(alwaysSuccessCheck()));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }
}
