package it.polimi.testing.temporalassertions.prefixes;

import it.polimi.testing.temporalassertions.checks.Check;
import it.polimi.testing.temporalassertions.checks.IfAndOnlyIf;

public class WeHaveThat
{
    private Check firstCheck;

    private WeHaveThat(Check check)
    {
        this.firstCheck = check;
    }

    public static WeHaveThat weHaveThat(Check check)
    {
        return new WeHaveThat(check);
    }

    public Check ifAndOnlyIf(Check check)
    {
        return new IfAndOnlyIf(firstCheck, check);
    }
}
