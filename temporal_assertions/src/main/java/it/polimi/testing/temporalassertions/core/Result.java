package it.polimi.testing.temporalassertions.core;

/**
 * The result of a check, contains an outcome (success, failure or warning) and a description
 */
public class Result
{
    private final Outcome outcome;
    private final String report;

    private String userFailureMessage;
    private String linkedCheckDescription;

    /**
     * Constructor
     * @param outcome the outcome of the check
     * @param report a message describing the result
     */
    public Result(Outcome outcome, String report)
    {
        this.outcome = outcome;
        this.report = report;
    }

    /**
     * Getter
     * @return the outcome of the check
     */
    public Outcome getOutcome()
    {
        return outcome;
    }

    /**
     * Getter
     * @return a message describing the result
     */
    public String getReport()
    {
        return report;
    }

    /**
     * Setter
     * @param userFailureMessage the message provided by the user to be displayed in case of failure
     */
    protected void setUserFailureMessage(String userFailureMessage)
    {
        this.userFailureMessage = userFailureMessage;
    }

    /**
     * Setter
     * @param checkDescription the description of the check that originated this result
     */
    protected void setLinkedCheckDescription(String checkDescription)
    {
        this.linkedCheckDescription = checkDescription;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        String failureMessage = Outcome.FAILURE.equals(outcome) ? "      ERROR: "+userFailureMessage+"\n" : "";
        return "["+outcome.name()+"] "+linkedCheckDescription+"\n"+failureMessage+"      REPORT: "+getReport();
    }
}
