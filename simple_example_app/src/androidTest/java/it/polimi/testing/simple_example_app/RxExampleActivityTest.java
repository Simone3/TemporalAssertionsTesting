package it.polimi.testing.simple_example_app;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.Button;
import android.widget.TextView;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import it.polimi.testing.temporalassertions.core.EventMonitor;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static com.jayway.awaitility.Awaitility.await;
import static it.polimi.testing.temporalassertions.core.AllHold.allHold;
import static it.polimi.testing.temporalassertions.core.AnEventThat.anEventThat;
import static it.polimi.testing.temporalassertions.events.FragmentLifecycleEvent.isFragmentLifecycleEvent;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class RxExampleActivityTest
{
    @Rule
    public ActivityTestRule<RxExampleActivity> activityTestRule = new ActivityTestRule<RxExampleActivity>(RxExampleActivity.class)
    {
        @Override
        protected void beforeActivityLaunched()
        {
            // Initialize event monitor before the activity is launched: this way if the activity adds some observables/checks directly in the production code we get them
            EventMonitor.getInstance().initialize();
        }
    };

    @After
    public void after()
    {
        // Stop verification at the end of each test
        EventMonitor.getInstance().stopVerification();
    }

    @Test
    public void testNormalBehavior()
    {
        // Get references
        RxExampleActivity activity = activityTestRule.getActivity();
        EventMonitor monitor = EventMonitor.getInstance();

        /* No custom checks and observables for this test */

        // Start verification here (after both the activity and this test have added their observables/checks)
        monitor.startVerification(null, EventMonitor.getAssertionErrorResultsSubscriber());

        // Start and wait timer
        TextView resultView = startTimer(activity);
        waitTimerEnd(resultView);
    }

    @Test
    public void testPauseResume()
    {
        // Get references
        final RxExampleActivity activity = activityTestRule.getActivity();
        EventMonitor monitor = EventMonitor.getInstance();

        // Add custom checks and observables
        monitor.checkThat("Activity is not paused and resumed during the test!",
                allHold(
                        anEventThat(isFragmentLifecycleEvent("onPause"))
                            .exists(),
                        anEventThat(isFragmentLifecycleEvent("onResume"))
                            .exists()));

        // Start verification here (after both the activity and this test have added their observables/checks)
        monitor.startVerification(null, EventMonitor.getAssertionErrorResultsSubscriber());

        // Start countdown
        TextView resultView = startTimer(activity);

        // Wait half countdown
        TextView countDownView = (TextView) activity.findViewById(R.id.countdown);
        waitHalfTimer(countDownView);

        // Pause and resume activity
        activity.runOnUiThread(new Runnable()
        {
           @Override
           public void run()
           {
               // Pause
               getInstrumentation().callActivityOnPause(activity);
           }
        });
        try
        {
            Thread.sleep(2000);
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                // Resume
                getInstrumentation().callActivityOnResume(activity);
            }
        });

        // Wait countdown end
        waitTimerEnd(resultView);
    }







    private TextView startTimer(RxExampleActivity activity)
    {
        // Click button
        TextView resultView = (TextView) activity.findViewById(R.id.result);
        if(resultView==null) fail("RxExampleActivity result view is null");
        final Button button = (Button) activity.findViewById(R.id.send_event_to_fragment);
        if(button==null) fail("RxExampleActivity button is null");
        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                button.performClick();
            }
        });
        return resultView;
    }

    private void waitHalfTimer(final TextView countDownView)
    {
        // Use Awaitility library to wait half of the countdown
        await().atMost(10, TimeUnit.SECONDS).until(new Callable<Boolean>()
        {
            @Override
            public Boolean call() throws Exception
            {
                return "seconds remaining: 5".equals(countDownView.getText().toString());
            }
        });
    }

    private void waitTimerEnd(final TextView resultView)
    {
        // Use Awaitility library to wait the end of the countdown
        await().atMost(15, TimeUnit.SECONDS).until(new Callable<Boolean>()
        {
            @Override
            public Boolean call() throws Exception
            {
                return "Completed!".equals(resultView.getText().toString());
            }
        });
    }
}