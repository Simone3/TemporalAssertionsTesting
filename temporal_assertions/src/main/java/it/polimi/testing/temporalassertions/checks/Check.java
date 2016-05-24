package it.polimi.testing.temporalassertions.checks;

import rx.Subscriber;

/**
 * Basic consistency check for the event stream
 *
 * It allows to express a condition (e.g. "an event X exists") on the stream it is applied to
 */
public class Check
{
    private CheckSubscriber checkSubscriber;
    private String description;
    private String userFailureMessage;

    /**
     * Constructor
     * @param description a description of the check
     * @param checkSubscriber the subscriber that implements the check logic
     */
    public Check(String description, CheckSubscriber checkSubscriber)
    {
        this.description = description;
        this.checkSubscriber = checkSubscriber;
    }

    /**
     * Sets the given child to the check subscriber and returns it
     * @param child the subscriber that will receive the result of the check
     * @return the subscriber that implements the check logic
     */
    public CheckSubscriber getCheckSubscriber(final Subscriber<? super Result> child)
    {
        checkSubscriber.setChild(child);
        return checkSubscriber;
    }

    /**
     * Getter
     * @return the message provided by the user to be displayed in case of check failure
     */
    public String getUserFailureMessage()
    {
        return userFailureMessage;
    }

    /**
     * Getter
     * @return a description of the check
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Setter
     * @param userFailureMessage the message provided by the user to be displayed in case of check failure
     */
    public void setUserFailureMessage(String userFailureMessage)
    {
        this.userFailureMessage = userFailureMessage;
    }

    /**
     * Changes the description of the check with the given one
     * @param newDescription the new description
     * @return the check itself with changed description (useful for chaining)
     */
    public Check overwriteDescription(String newDescription)
    {
        this.description = newDescription;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return description;
    }
}
