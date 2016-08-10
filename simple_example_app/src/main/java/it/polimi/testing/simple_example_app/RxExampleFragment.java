package it.polimi.testing.simple_example_app;


import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;

import it.polimi.testing.temporalassertions.core.EventMonitor;
import it.polimi.testing.temporalassertions.events.CallbackEvent;
import it.polimi.testing.temporalassertions.events.GenericEvent;
import it.polimi.testing.temporalassertions.events.SupportFragmentLifecycleEvent;
import it.polimi.testing.temporalassertions.events.TextChangeEvent;
import rx.Observable;
import rx.functions.Func1;

import static it.polimi.testing.temporalassertions.core.AllEventsWhereEach.allEventsWhereEach;
import static it.polimi.testing.temporalassertions.core.AllHold.allHold;
import static it.polimi.testing.temporalassertions.core.AnEventThat.anEventThat;
import static it.polimi.testing.temporalassertions.core.Exactly.exactly;
import static it.polimi.testing.temporalassertions.core.Exist.exist;
import static it.polimi.testing.temporalassertions.events.CallbackEvent.isCallbackEvent;
import static it.polimi.testing.temporalassertions.events.FragmentLifecycleEvent.ON_ATTACH;
import static it.polimi.testing.temporalassertions.events.FragmentLifecycleEvent.ON_CREATE;
import static it.polimi.testing.temporalassertions.events.FragmentLifecycleEvent.ON_CREATE_VIEW;
import static it.polimi.testing.temporalassertions.events.FragmentLifecycleEvent.ON_DETACH;
import static it.polimi.testing.temporalassertions.events.FragmentLifecycleEvent.ON_PAUSE;
import static it.polimi.testing.temporalassertions.events.FragmentLifecycleEvent.ON_RESUME;
import static it.polimi.testing.temporalassertions.events.FragmentLifecycleEvent.ON_SAVE_INSTANCE_STATE;
import static it.polimi.testing.temporalassertions.events.SupportFragmentLifecycleEvent.isSupportFragmentLifecycleEvent;
import static it.polimi.testing.temporalassertions.events.TextChangeEvent.isTextChange;
import static it.polimi.testing.temporalassertions.events.TextChangeEvent.isTextChangeFrom;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.hamcrest.core.StringStartsWith.startsWith;

public class RxExampleFragment extends Fragment
{
    private static final String COUNT_DOWN_STARTED = "COUNT_DOWN_STARTED";

    private OnCountDownEnded listener;

    private TextView countDownView;

    private final static long TOTAL_COUNT_DOWN = 10000;
    private long currentCountDownValue = TOTAL_COUNT_DOWN;
    @VisibleForTesting
    CountDownTimer countDownTimer;
    private boolean countDownStarted = false;

    public RxExampleFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        EventMonitor.getInstance().fireCustomEvent(new SupportFragmentLifecycleEvent(RxExampleFragment.class, ON_CREATE));

        super.onCreate(savedInstanceState);
        if(getArguments()!=null)
        {
            countDownStarted = getArguments().getBoolean(COUNT_DOWN_STARTED);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        EventMonitor.getInstance().fireCustomEvent(new SupportFragmentLifecycleEvent(RxExampleFragment.class, ON_CREATE_VIEW));

        View view = inflater.inflate(R.layout.fragment_rx_example, container, false);

        countDownView = (TextView) view.findViewById(R.id.countdown);

        monitorAddObservablesAndChecks();
        monitorStartVerification();

        return view;
    }

    @Override
    public void onAttach(Context context)
    {
        EventMonitor.getInstance().fireCustomEvent(new SupportFragmentLifecycleEvent(RxExampleFragment.class, ON_ATTACH));

        super.onAttach(context);
        if(context instanceof OnCountDownEnded)
        {
            listener = (OnCountDownEnded) context;
        }
        else
        {
            throw new RuntimeException(context.toString()
                    +" must implement OnCountDownEnded");
        }
    }

    @Override
    public void onDetach()
    {
        EventMonitor.getInstance().fireCustomEvent(new SupportFragmentLifecycleEvent(RxExampleFragment.class, ON_DETACH));

        super.onDetach();
        listener = null;
    }

    @Override
    public void onResume()
    {
        EventMonitor.getInstance().fireCustomEvent(new SupportFragmentLifecycleEvent(RxExampleFragment.class, ON_RESUME));

        super.onResume();
        if(countDownStarted && countDownTimer==null)
        {
            startCountdown();
        }
    }

    @Override
    public void onPause()
    {
        EventMonitor.getInstance().fireCustomEvent(new SupportFragmentLifecycleEvent(RxExampleFragment.class, ON_PAUSE));

        super.onPause();
        if(countDownTimer!=null)
        {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle s)
    {
        EventMonitor.getInstance().fireCustomEvent(new SupportFragmentLifecycleEvent(RxExampleFragment.class, ON_SAVE_INSTANCE_STATE));

        s.putBoolean(COUNT_DOWN_STARTED, countDownStarted);
        super.onSaveInstanceState(s);
    }

    public void startCountdown()
    {
        EventMonitor.getInstance().fireCustomEvent(new CallbackEvent("Activity->Fragment"));

        EventMonitor.getInstance().fireCustomEvent(new GenericEvent(123));

        countDownStarted = true;

        countDownTimer = new CountDownTimer(currentCountDownValue, 1000)
        {
            public void onTick(long millisUntilFinished)
            {
                currentCountDownValue = millisUntilFinished;
                int seconds = Math.round(millisUntilFinished / 1000);
                if(seconds==10) return;
                String newText = getActivity().getString(R.string.seconds, seconds);
                if(newText.equals(countDownView.getText().toString())) return;
                countDownView.setText(newText);
            }

            public void onFinish()
            {
                countDownView.setText(R.string.end);
                if(listener!=null)
                {
                    listener.onCountDownFinished(true);
                }
                countDownStarted = false;
            }
        };

        countDownTimer.start();
    }

    public interface OnCountDownEnded
    {
        void onCountDownFinished(boolean result);
    }









    private void monitorAddObservablesAndChecks()
    {
        EventMonitor eventMonitor = EventMonitor.getInstance();

        Observable<TextChangeEvent> countDownObservable = RxTextView.textChanges(countDownView).map(new Func1<CharSequence, TextChangeEvent>()
        {
            @Override
            public TextChangeEvent call(CharSequence charSequence)
            {
                return new TextChangeEvent(countDownView, charSequence.toString());
            }
        });

        eventMonitor.observe(countDownObservable);


        /********* SUCCESSFUL CHECKS *********/

        eventMonitor.checkThat("Wrong number of countdown text updates",
                exist(exactly(11))
                    .eventsWhereEach(isTextChangeFrom(countDownView)));

        eventMonitor.checkThat("Countdown values are in the wrong order [matchInOrder example]",
                allEventsWhereEach(isTextChange(countDownView, startsWith("seconds remaining: ")))
                    .matchInOrder(isTextChange(countDownView, endsWith("9")), isTextChange(countDownView, endsWith("8")), isTextChange(countDownView, endsWith("7")), isTextChange(countDownView, endsWith("6")), isTextChange(countDownView, endsWith("5")), isTextChange(countDownView, endsWith("4")), isTextChange(countDownView, endsWith("3")), isTextChange(countDownView, endsWith("2")), isTextChange(countDownView, endsWith("1"))));

        eventMonitor.checkThat("Countdown values are in the wrong order [areOrdered example]",
                allEventsWhereEach(isTextChange(countDownView, startsWith("seconds remaining: ")))
                        /****** With Lambda (Java 8) ******/
                        .areOrdered((TextChangeEvent lhs, TextChangeEvent rhs) ->
                        {
                            String t1 = lhs.getText();
                            String t2 = rhs.getText();

                            // Get seconds in string
                            int s1 = Integer.valueOf(t1.substring(19, t1.length()));
                            int s2 = Integer.valueOf(t2.substring(19, t2.length()));

                            // Check if seconds values are in inverse order
                            return Integer.compare(s2, s1);
                        }));
                        /***** With standard comparator *******/
                        /*.areOrdered(new Comparator<Event>()
                        {
                            @Override
                            public int compare(Event lhs, Event rhs)
                            {
                                String t1 = ((TextChangeEvent) lhs).getText();
                                String t2 = ((TextChangeEvent) rhs).getText();

                                // Get seconds in string
                                int s1 = Integer.valueOf(t1.substring(19, t1.length()));
                                int s2 = Integer.valueOf(t2.substring(19, t2.length()));

                                // Check if seconds values are in inverse order
                                return Integer.compare(s2, s1);
                            }
                        }));*/

        eventMonitor.checkThat("'End' is written at the wrong moment",
                anEventThat(isTextChange(countDownView, equalTo("End")))
                    .canHappenOnlyAfter(anEventThat(isTextChange(countDownView, equalTo("seconds remaining: 1")))));

        eventMonitor.checkThat("Countdown text is updated even if the activity is paused/stopped",
                allHold(
                        anEventThat(isTextChange())
                            .canHappenOnlyBefore(anEventThat(isSupportFragmentLifecycleEvent(RxExampleFragment.class, ON_PAUSE))),
                        anEventThat(isTextChange())
                                .canHappenOnlyAfter(anEventThat(isSupportFragmentLifecycleEvent(RxExampleFragment.class, ON_RESUME)))));

        eventMonitor.checkThat("Countdown text is updated before or after the activity callbacks",
                anEventThat(isTextChange(countDownView, startsWith("seconds remaining: ")))
                    .canHappenOnlyBetween(anEventThat(isCallbackEvent("Activity->Fragment")), anEventThat(isCallbackEvent("Fragment->Activity"))));
    }

    private void monitorStartVerification()
    {
        // Only log results
        // [Uncomment for runtime monitoring] EventMonitor.getInstance().startVerification(null, null);

        // Crash if a result fails (AssertionError)
        // EventMonitor.getInstance().startVerification(null, EventMonitor.getAssertionErrorResultsSubscriber());
    }
}

