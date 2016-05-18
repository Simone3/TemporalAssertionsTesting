package it.polimi.testing.matchers;

import org.hamcrest.Matcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.polimi.testing.temporalassertions.events.Event;

public class AbstractEventDescriptor
{
    private Matcher<? extends Event> matcher;

    AbstractEventDescriptor(Matcher<? extends Event> matcher)
    {
        this.matcher = matcher;
    }

    Matcher<? extends Event> getMatcher()
    {
        return matcher;
    }

    public static class State
    {
        private int state;
        private List<Event> events = new ArrayList<>();

        public State(int state)
        {
            this.state = state;
        }

        public int getState()
        {
            return state;
        }

        public void setState(int state)
        {
            this.state = state;
        }

        public List<Event> getEvents()
        {
            return events;
        }

        public Event getEvent(int i)
        {
            return events.get(i);
        }

        public void setEvents(List<Event> events)
        {
            this.events = events;
        }

        public void setEvents(Event[] events)
        {
            this.events = Arrays.asList(events);
        }

        public void setEvents(Event singleEvent)
        {
            clearEvents();
            addEvent(singleEvent);
        }

        public void addEvent(Event newEvent)
        {
            this.events.add(newEvent);
        }

        public void clearEvents()
        {
            this.events.clear();
        }
    }
}
