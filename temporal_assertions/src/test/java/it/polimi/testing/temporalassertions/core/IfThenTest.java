package it.polimi.testing.temporalassertions.core;


import org.junit.Test;

import static it.polimi.testing.temporalassertions.core.RxTestUtils.alwaysFailureCheck;
import static it.polimi.testing.temporalassertions.core.RxTestUtils.alwaysSuccessCheck;
import static it.polimi.testing.temporalassertions.core.RxTestUtils.assertThatOutcomeIs;
import static it.polimi.testing.temporalassertions.core.ProvidedThat.providedThat;

public class IfThenTest
{
    @Test
    public void testIfThen_PreconditionTrueCheckTrue()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = providedThat(alwaysSuccessCheck()).then(alwaysSuccessCheck());
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testIfThen_PreconditionTrueCheckFalse()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = providedThat(alwaysSuccessCheck()).then(alwaysFailureCheck());
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }

    @Test
    public void testIfThen_PreconditionFalseCheckTrue()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = providedThat(alwaysFailureCheck()).then(alwaysSuccessCheck());
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testIfThen_PreconditionFalseCheckFalse()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = providedThat(alwaysFailureCheck()).then(alwaysFailureCheck());
        assertThatOutcomeIs(events, check, Outcome.SUCCESS);
    }

    @Test
    public void testIfThen_Nested()
    {
        String[] events = new String[]{"A", "B", "C", "D", "E", "F"};
        Check check = providedThat(
                            providedThat(alwaysFailureCheck()).then(alwaysSuccessCheck()))
                        .then(
                            providedThat(alwaysSuccessCheck()).then(alwaysFailureCheck()));
        assertThatOutcomeIs(events, check, Outcome.FAILURE);
    }
}
