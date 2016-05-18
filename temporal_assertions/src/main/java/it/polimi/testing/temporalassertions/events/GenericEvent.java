package it.polimi.testing.temporalassertions.events;


public class GenericEvent extends Event
{
    private final Object[] objects;

    public GenericEvent(Object... objects)
    {
        this.objects = objects;
    }

    public Object[] getObjects()
    {
        return objects;
    }

    @Override
    public String toString()
    {
        return "GE with objects "+arrayToString(objects);
    }

    private static String arrayToString(Object[] array)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i=0; i<array.length; i++)
        {
            sb.append("\"");
            sb.append(array[i]);
            sb.append("\"");
            if(i!=array.length-1)
            {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
