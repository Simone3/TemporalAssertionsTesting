package it.polimi.testing.temporalassertions.events;


public abstract class LifecycleEvent extends Event
{
    private final String componentName;
    private final String callbackName;

    public LifecycleEvent(String componentName, String callbackName)
    {
        this.componentName = componentName;
        this.callbackName = callbackName;
    }

    public String getCallbackName()
    {
        return callbackName;
    }

    public String getComponentName()
    {
        return componentName;
    }

    @Override
    public String toString()
    {
        return componentName+" Lifecycle: "+callbackName;
    }
}
