package it.polimi.testing.checks;

public class Result
{
    private Outcome outcome;
    private String message;

    public Result(Outcome outcome, String message)
    {
        this.outcome = outcome;
        this.message = message;
    }

    public Outcome getOutcome()
    {
        return outcome;
    }

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
