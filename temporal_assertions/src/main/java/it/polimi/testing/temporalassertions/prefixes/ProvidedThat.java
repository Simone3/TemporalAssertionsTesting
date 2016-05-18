package it.polimi.testing.temporalassertions.prefixes;

import it.polimi.testing.temporalassertions.checks.Check;
import it.polimi.testing.temporalassertions.checks.IfThen;

public class ProvidedThat
{
    private Check ifCheck;

    private ProvidedThat(Check check)
    {
        this.ifCheck = check;
    }

    public static ProvidedThat providedThat(Check check)
    {
        return new ProvidedThat(check);
    }

    public Check then(Check check)
    {
        return new IfThen(ifCheck, check);
    }
}
