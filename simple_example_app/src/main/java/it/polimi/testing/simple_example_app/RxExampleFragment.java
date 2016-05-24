package it.polimi.testing.simple_example_app;


import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;

import it.polimi.testing.temporalassertions.events.CallbackEvent;
import it.polimi.testing.temporalassertions.events.FragmentLifecycleEvent;
import it.polimi.testing.temporalassertions.events.GenericEvent;
import it.polimi.testing.temporalassertions.events.TextChangeEvent;
import it.polimi.testing.temporalassertions.monitor.EventMonitor;
import rx.Observable;
import rx.functions.Func1;

import static it.polimi.testing.temporalassertions.checks.Not.notTrueThat;
import static it.polimi.testing.temporalassertions.descriptors.AllEventsWhereEach.allEventsWhereEach;
import static it.polimi.testing.temporalassertions.descriptors.AnEventThat.anEventThat;
import static it.polimi.testing.temporalassertions.events.CallbackEvent.isCallbackEvent;
import static it.polimi.testing.temporalassertions.events.FragmentLifecycleEvent.isFragmentLifecycleEvent;
import static it.polimi.testing.temporalassertions.events.TextChangeEvent.isTextChangeEvent;
import static it.polimi.testing.temporalassertions.events.TextChangeEvent.isTextChangeEventFrom;
import static it.polimi.testing.temporalassertions.quantifiers.Exactly.exactly;
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
    private CountDownTimer countDownTimer;
    private boolean countDownStarted = false;
    private int rand = 123;

    public RxExampleFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        EventMonitor.getInstance().fireCustomEvent(new FragmentLifecycleEvent(RxExampleFragment.class, "onCreate"));

        super.onCreate(savedInstanceState);
        if(getArguments()!=null)
        {
            countDownStarted = getArguments().getBoolean(COUNT_DOWN_STARTED);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        EventMonitor.getInstance().fireCustomEvent(new FragmentLifecycleEvent(RxExampleFragment.class, "onCreateView"));

        View view = inflater.inflate(R.layout.fragment_rx_example, container, false);

        countDownView = (TextView) view.findViewById(R.id.countdown);

        monitorAddObservablesAndChecks();
        monitorStartVerification();

        return view;
    }

    @Override
    public void onAttach(Context context)
    {
        EventMonitor.getInstance().fireCustomEvent(new FragmentLifecycleEvent(RxExampleFragment.class, "onAttach"));

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
        EventMonitor.getInstance().fireCustomEvent(new FragmentLifecycleEvent(RxExampleFragment.class, "onDetach"));

        super.onDetach();
        listener = null;
    }

    @Override
    public void onResume()
    {
        EventMonitor.getInstance().fireCustomEvent(new FragmentLifecycleEvent(RxExampleFragment.class, "onResume"));

        super.onResume();
        if(countDownStarted && countDownTimer==null)
        {
            startCountdown();
        }
    }

    @Override
    public void onPause()
    {
        EventMonitor.getInstance().fireCustomEvent(new FragmentLifecycleEvent(RxExampleFragment.class, "onPause"));

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
        EventMonitor.getInstance().fireCustomEvent(new FragmentLifecycleEvent(RxExampleFragment.class, "onSaveInstanceState"));

        s.putBoolean(COUNT_DOWN_STARTED, countDownStarted);
        super.onSaveInstanceState(s);
    }

    public void startCountdown()
    {
        EventMonitor.getInstance().fireCustomEvent(new CallbackEvent("Activity->Fragment"));

        EventMonitor.getInstance().fireCustomEvent(new GenericEvent(rand));

        countDownStarted = true;

        countDownTimer = new CountDownTimer(currentCountDownValue, 1000)
        {
            public void onTick(long millisUntilFinished)
            {
                currentCountDownValue = millisUntilFinished;
                countDownView.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish()
            {
                countDownView.setText("End");
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
                allEventsWhereEach(isTextChangeEventFrom(countDownView))
                    .are(exactly(11)));

        eventMonitor.checkThat("Countdown values are in the wrong order",
                allEventsWhereEach(isTextChangeEvent(countDownView, startsWith("seconds remaining: ")))
                    .matchInOrder(isTextChangeEvent(countDownView, endsWith("9")), isTextChangeEvent(countDownView, endsWith("8")), isTextChangeEvent(countDownView, endsWith("7")), isTextChangeEvent(countDownView, endsWith("6")), isTextChangeEvent(countDownView, endsWith("5")), isTextChangeEvent(countDownView, endsWith("4")), isTextChangeEvent(countDownView, endsWith("3")), isTextChangeEvent(countDownView, endsWith("2")), isTextChangeEvent(countDownView, endsWith("1"))));

        eventMonitor.checkThat("'End' is written at the wrong moment",
                anEventThat(isTextChangeEvent(countDownView, equalTo("End")))
                    .canOnlyHappenAfter(anEventThat(isTextChangeEvent(countDownView, equalTo("seconds remaining: 1")))));

        eventMonitor.checkThat("Countdown text is updated even if the activity is paused/stopped",
                notTrueThat(
                    anEventThat(isTextChangeEvent())
                        .existsBetween(anEventThat(isFragmentLifecycleEvent(RxExampleFragment.class, "onPause")), anEventThat(isFragmentLifecycleEvent(RxExampleFragment.class, "onResume")))));

        eventMonitor.checkThat("Countdown text is updated before or after the activity callbacks",
                anEventThat(isTextChangeEvent(countDownView, startsWith("seconds remaining: ")))
                    .canOnlyHappenBetween(anEventThat(isCallbackEvent("Activity->Fragment")), anEventThat(isCallbackEvent("Fragment->Activity"))));


        /********* FAILING CHECKS *********/

        /*eventMonitor.checkThat("Failing Check 1",
                anEventThat(isCallbackEvent("ThisCallbackDoesNotExist"))
                    .exists());

        eventMonitor.checkThat("Failing Check 2",
                allEventsWhereEach(isTextChangeEventFrom(countDownView))
                    .are(atMost(5)));

        eventMonitor.checkThat("Failing Check 3",
                allEventsWhereEach(isTextChangeEvent(countDownView, startsWith("seconds remaining: ")))
                    .matchInOrder(isTextChangeEvent(countDownView, endsWith("9")), isTextChangeEvent(countDownView, endsWith("8")), isTextChangeEvent(countDownView, endsWith("700")), isTextChangeEvent(countDownView, endsWith("6")), isTextChangeEvent(countDownView, endsWith("5")), isTextChangeEvent(countDownView, endsWith("4")), isTextChangeEvent(countDownView, endsWith("3")), isTextChangeEvent(countDownView, endsWith("2")), isTextChangeEvent(countDownView, endsWith("1"))));

        eventMonitor.checkThat("Failing Check 4",
                anEventThat(isTextChangeEvent(countDownView, equalTo("End")))
                    .canOnlyHappenBefore(anEventThat(isTextChangeEvent(countDownView, equalTo("seconds remaining: 1")))));

        eventMonitor.checkThat("Failing Check 5",
                anEventThat(isTextChangeEvent())
                    .existsBetween(anEventThat(isFragmentLifecycleEvent(RxExampleFragment.class, "onPause")), anEventThat(isFragmentLifecycleEvent(RxExampleFragment.class, "onResume"))));

        eventMonitor.checkThat("Failing Check 6",
                anEventThat(isTextChangeEventFrom(countDownView))
                    .canOnlyHappenBefore(anEventThat(isCallbackEvent("Activity->Fragment"))));*/
    }

    private void monitorStartVerification()
    {
        EventMonitor.getInstance().startVerification(null, null);
    }
}

