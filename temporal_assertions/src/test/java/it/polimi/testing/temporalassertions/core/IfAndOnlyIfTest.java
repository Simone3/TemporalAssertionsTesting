package it.polimi.testing.temporalassertions.core;

import org.junit.Test;

import static it.polimi.testing.temporalassertions.RxTestUtils.alwaysFailureCheck;
import static it.polimi.testing.temporalassertions.RxTestUtils.alwaysSuccessCheck;
import static it.polimi.testing.temporalassertions.RxTestUtils.assertThatOutcomeIs;
import static it.polimi.testing.temporalassertions.core.WeHaveThat.weHaveThat;

public class IfAndOnlyIfTest
{
    @Test
    public void testIfAndOnlyIf_FirstTrueSecondTrue()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = weHaveThat(alwaysSuccessCheck()).ifAndOnlyIf(alwaysSuccessCheck());
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testIfAndOnlyIf_FirstTrueSecondFalse()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = weHaveThat(alwaysSuccessCheck()).ifAndOnlyIf(alwaysFailureCheck());
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testIfAndOnlyIf_FirstFalseSecondTrue()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = weHaveThat(alwaysFailureCheck()).ifAndOnlyIf(alwaysSuccessCheck());
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testIfAndOnlyIf_FirstFalseSecondFalse()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = weHaveThat(alwaysFailureCheck()).ifAndOnlyIf(alwaysFailureCheck());
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testIfAndOnlyIf_Nested()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = weHaveThat(
                weHaveThat(alwaysSuccessCheck()).ifAndOnlyIf(alwaysSuccessCheck()))
                .ifAndOnlyIf(
                        weHaveThat(alwaysSuccessCheck()).ifAndOnlyIf(alwaysFailureCheck()));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }
}
