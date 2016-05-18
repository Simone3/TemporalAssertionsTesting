package it.polimi.testing.temporalassertions.events;


import android.view.View;

public class TextChangeEvent extends Event
{
    private final View view;
    private final String text;

    public TextChangeEvent(View view, String text)
    {
        this.view = view;
        this.text = text;
    }

    public View getView()
    {
        return view;
    }

    public String getText()
    {
        return text;
    }

    @Override
    public String toString()
    {
        return "TC '"+text+"' from "+view;
    }
}
