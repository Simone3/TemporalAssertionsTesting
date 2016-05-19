package it.polimi.testing.simple_example_app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;

import it.polimi.testing.temporalassertions.events.ActivityLifecycleEvent;
import it.polimi.testing.temporalassertions.events.CallbackEvent;
import it.polimi.testing.temporalassertions.events.TextChangeEvent;
import it.polimi.testing.temporalassertions.monitor.EventMonitor;
import rx.Observable;
import rx.functions.Func1;

import static it.polimi.testing.temporalassertions.matchers.AnEventThat.anEventThat;
import static it.polimi.testing.temporalassertions.matchers.Matchers.isCallbackEvent;
import static it.polimi.testing.temporalassertions.matchers.Matchers.isTextChangeEvent;
import static it.polimi.testing.temporalassertions.matchers.Matchers.isTextChangeEventFrom;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;


public class RxExampleActivity extends AppCompatActivity implements RxExampleFragment.OnCountDownEnded
{
    private RxExampleFragment fragment;
    private TextView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_example);

        EventMonitor.getInstance().initialize();
        EventMonitor.getInstance().fireCustomEvent(new ActivityLifecycleEvent(RxExampleActivity.class, "onCreate"));

        if(findViewById(R.id.fragment_container)!=null)
        {
            if(savedInstanceState!=null)
            {
                return;
            }

            fragment = new RxExampleFragment();

            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
        }

        final Button button = (Button) findViewById(R.id.send_event_to_fragment);
        if(button!=null)
        {
            button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    fragment.startCountdown();

                    button.setEnabled(false);
                }
            });

        }

        resultView = (TextView) findViewById(R.id.result);

        monitorAddObservablesAndChecks();
    }

    @Override
    public void onCountDownFinished(boolean result)
    {
        EventMonitor.getInstance().fireCustomEvent(new CallbackEvent("Fragment->Activity"));

        if(result) resultView.setText("Completed!");
        else resultView.setText("Failed!");
    }







    private void monitorAddObservablesAndChecks()
    {
        EventMonitor eventMonitor = EventMonitor.getInstance();

        Observable<TextChangeEvent> resultObservable = RxTextView.textChanges(resultView).map(new Func1<CharSequence, TextChangeEvent>()
        {
            @Override
            public TextChangeEvent call(CharSequence charSequence)
            {
                return new TextChangeEvent(resultView, charSequence.toString());
            }
        });

        eventMonitor.observe(resultObservable);


        eventMonitor.checkThat(anEventThat(isCallbackEvent("Activity->Fragment"))
                                .exists());

        eventMonitor.checkThat(anEventThat(isCallbackEvent("Fragment->Activity"))
                                .exists());

        eventMonitor.checkThat(anEventThat(isTextChangeEvent(resultView, is(equalTo("Completed!"))))
                                .exists());

        eventMonitor.checkThat(anEventThat(isCallbackEvent("Activity->Fragment"))
                                .canOnlyHappenBefore(anEventThat(isTextChangeEventFrom(resultView))));
    }

    @Override
    public void onDestroy()
    {
        EventMonitor.getInstance().stopVerification();

        super.onDestroy();
    }
}