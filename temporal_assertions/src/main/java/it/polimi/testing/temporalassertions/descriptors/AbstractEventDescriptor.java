package it.polimi.testing.temporalassertions.descriptors;

import org.hamcrest.Matcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.polimi.testing.temporalassertions.events.Event;

/**
 * A basic descriptor of events. Descriptors allow to "select" one or more events in the sequence
 * to express some conditions on them
 *
 * All descriptors use an Hamcrest matcher to describe the events they are interested into
 */
public class AbstractEventDescriptor
{
    private Matcher<? extends Event> matcher;

    /**
     * Constructor
     * @param matcher the Hamcrest matcher that describes the events
     */
    AbstractEventDescriptor(Matcher<? extends Event> matcher)
    {
        this.matcher = matcher;
    }

    /**
     * Getter
     * @return the Hamcrest matcher that describes the events
     */
    Matcher<? extends Event> getMatcher()
    {
        return matcher;
    }

    /**
     * Utility class that allows to store a "state" of the current computation
     *
     * Can be used by the implementations' methods to express the state of a check on this descriptor
     */
    public static class State
    {
        private int state;
        private List<Event> events = new ArrayList<>();

        /**
         * Constructor
         * @param state the initial state
         */
        public State(int state)
        {
            this.state = state;
        }

        /**
         * Getter
         * @return the current state
         */
        public int getState()
        {
            return state;
        }

        /**
         * Setter
         * @param state the new state
         */
        public void setState(int state)
        {
            this.state = state;
        }

        /**
         * Getter
         * @return the events stored in the object
         */
        public List<Event> getEvents()
        {
            return events;
        }

        /**
         * Helper equivalent to getEvents().get(i)
         * @param i the index
         * @return the event at the given index
         */
        public Event getEvent(int i)
        {
            return events.get(i);
        }

        /**
         * Setter
         * @param events the events stored in the object
         */
        public void setEvents(List<Event> events)
        {
            this.events = events;
        }

        /**
         * Setter
         * @param events the events stored in the object
         */
        public void setEvents(Event[] events)
        {
            this.events = Arrays.asList(events);
        }

        /**
         * Setter
         * @param singleEvent the single event to store in the object
         */
        public void setEvents(Event singleEvent)
        {
            clearEvents();
            addEvent(singleEvent);
        }

        /**
         * Adder
         * @param newEvent the event to be added to the current list of events
         */
        public void addEvent(Event newEvent)
        {
            this.events.add(newEvent);
        }

        /**
         * Helper equivalent to getEvents().clear();
         */
        public void clearEvents()
        {
            this.events.clear();
        }
    }
}
