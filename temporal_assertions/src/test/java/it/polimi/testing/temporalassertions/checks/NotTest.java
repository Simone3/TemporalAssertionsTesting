package it.polimi.testing.temporalassertions.checks;

import org.junit.Test;

import static it.polimi.testing.temporalassertions.RxTestUtils.alwaysFailureCheck;
import static it.polimi.testing.temporalassertions.RxTestUtils.alwaysSuccessCheck;
import static it.polimi.testing.temporalassertions.RxTestUtils.alwaysWarningCheck;
import static it.polimi.testing.temporalassertions.RxTestUtils.assertThatOutcomeIs;
import static it.polimi.testing.temporalassertions.checks.Not.notTrueThat;

public class NotTest
{
    @Test
    public void testNot_InvertSuccess()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = notTrueThat(alwaysSuccessCheck());
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testNot_InvertFailure()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = notTrueThat(alwaysFailureCheck());
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testNot_InvertWarning()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = notTrueThat(alwaysWarningCheck());
        assertThatOutcomeIs(events, check, Outcome.WARNING);
    }

    @Test
    public void testNot_InvertSelf()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = notTrueThat(notTrueThat(alwaysSuccessCheck()));
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }
}
