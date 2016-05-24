package it.polimi.testing.temporalassertions;

import android.view.View;

/**
 * Some helper methods
 */
public class Utils
{
    /**
     * Helper to write a string that describes an array of objects
     * @param array the array
     * @return the string
     */
    public static String arrayToString(Object[] array)
    {
        return "[\""+join("\", \"", array)+"\"]";
    }

    /**
     * Joins an array with the given separator, i.e. {@code join(",", {A, B, C})} returns {@code "A,B,C"}
     * @param sep the separator between the elements
     * @param array the array
     * @return a string containing all elements of the array separated by the separator
     */
    public static String join(String sep, Object[] array)
    {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<array.length; i++)
        {
            sb.append(array[i]);
            if(i!=array.length-1) sb.append(sep);
        }
        return sb.toString();
    }

    /**
     * Helper to describe a view as a string
     * @param view the view
     * @return the description
     */
    public static String describeView(View view)
    {
        if(view==null) return "null";

        int id = view.getId();
        String className = view.getClass().getSimpleName();
        return className+id;
    }
}
