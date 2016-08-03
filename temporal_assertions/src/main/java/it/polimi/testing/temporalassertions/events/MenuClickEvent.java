package it.polimi.testing.temporalassertions.events;

import android.support.annotation.IdRes;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.equalTo;

/**
 * An event representing a click on a menu option
 */
public class MenuClickEvent extends Event
{
    private int menuOptionId;

    /**
     * Constructor
     * @param menuOptionId the ID of the clicked menu option
     */
    public MenuClickEvent(int menuOptionId)
    {
        this.menuOptionId = menuOptionId;
    }

    /**
     * Getter
     * @return the ID of the clicked menu option
     */
    public int getMenuOptionId()
    {
        return menuOptionId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "{Click on menu option "+menuOptionId+"}";
    }

    /**
     * Hamcrest matcher that matches any menu click (any option)
     * @return the Hamcrest matcher
     */
    public static Matcher<MenuClickEvent> isMenuClick()
    {
        return new FeatureMatcher<MenuClickEvent, Void>(anything(""), "is click on menu option", "")
        {
            @Override
            protected Void featureValueOf(final MenuClickEvent actual)
            {
                return null;
            }
        };
    }

    /**
     * Hamcrest matcher that matches a click on the given menu option
     * @param menuOptionId the ID of the menu option
     * @return the Hamcrest matcher
     */
    public static Matcher<MenuClickEvent> isMenuClick(@IdRes int menuOptionId)
    {
        return new FeatureMatcher<MenuClickEvent, Integer>(equalTo(menuOptionId), "is click on menu option "+menuOptionId, "")
        {
            @Override
            protected Integer featureValueOf(final MenuClickEvent actual)
            {
                return actual.menuOptionId;
            }
        };
    }
}
