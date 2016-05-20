package it.polimi.testing.temporalassertions.events;

/**
 * Abstract lifecycle change event for a component
 */
public abstract class LifecycleEvent extends Event
{
    private final String componentName;
    private final String callbackName;

    /**
     * Constructor
     * @param componentName the component (e.g. activity, fragment, etc.) name
     * @param callbackName the name of the lifecycle callback
     */
    public LifecycleEvent(String componentName, String callbackName)
    {
        this.componentName = componentName;
        this.callbackName = callbackName;
    }

    /**
     * Getter
     * @return the name of the lifecycle callback
     */
    public String getCallbackName()
    {
        return callbackName;
    }

    /**
     * Getter
     * @return the component (e.g. activity, fragment, etc.) name
     */
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
