package it.polimi.testing.temporalassertions.events;


public class CallbackEvent extends Event
{
    private final String callbackName;

    public CallbackEvent(String callbackName)
    {
        this.callbackName = callbackName;
    }

    public String getCallbackName()
    {
        return callbackName;
    }

    @Override
    public String toString()
    {
        return "Callback "+callbackName;
    }
}
