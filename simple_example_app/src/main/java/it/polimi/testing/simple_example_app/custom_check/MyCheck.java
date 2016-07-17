package it.polimi.testing.simple_example_app.custom_check;

import android.support.annotation.NonNull;

import it.polimi.testing.temporalassertions.core.Check;
import it.polimi.testing.temporalassertions.core.CheckSubscriber;
import it.polimi.testing.temporalassertions.core.Outcome;
import it.polimi.testing.temporalassertions.core.Result;
import it.polimi.testing.temporalassertions.events.Event;

/**
 * A custom check created inside the example app
 */
class MyCheck extends Check
{
    MyCheck()
    {
        super(
                "My check description",

                new CheckSubscriber()
                {
                    @Override
                    public void onNext(Event event)
                    {
                        endCheck();
                    }

                    @NonNull
                    @Override
                    public Result getFinalResult()
                    {
                        return new Result(Outcome.SUCCESS, "My check report");
                    }
                });
    }
}
