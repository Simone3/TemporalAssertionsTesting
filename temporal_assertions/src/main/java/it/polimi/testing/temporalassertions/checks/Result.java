package it.polimi.testing.temporalassertions.checks;

/**
 * The result of a check, contains an outcome (success, failure or warning) and a description
 */
public class Result
{
    private Outcome outcome;
    private String message;

    /**
     * Constructor
     * @param outcome the outcome of the check
     * @param message a message describing the result
     */
    public Result(Outcome outcome, String message)
    {
        this.outcome = outcome;
        this.message = message;
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
    public String getMessage()
    {
        return message;
    }

    @Override
    public String toString()
    {
        return "["+outcome.name()+"] "+getMessage();
    }
}
