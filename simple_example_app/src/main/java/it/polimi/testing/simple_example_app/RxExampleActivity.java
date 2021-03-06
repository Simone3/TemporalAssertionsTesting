package it.polimi.testing.simple_example_app;

import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;

import it.polimi.testing.temporalassertions.core.EventMonitor;
import it.polimi.testing.temporalassertions.events.ActivityLifecycleEvent;
import it.polimi.testing.temporalassertions.events.CallbackEvent;
import it.polimi.testing.temporalassertions.events.TextChangeEvent;
import rx.Observable;
import rx.functions.Func1;

import static it.polimi.testing.simple_example_app.custom_check.MyDescriptor.myDescriptor;
import static it.polimi.testing.temporalassertions.core.AnEventThat.anEventThat;
import static it.polimi.testing.temporalassertions.core.Exist.existsAnEventThat;
import static it.polimi.testing.temporalassertions.events.ActivityLifecycleEvent.ON_CREATE;
import static it.polimi.testing.temporalassertions.events.CallbackEvent.isCallbackEvent;
import static it.polimi.testing.temporalassertions.events.TextChangeEvent.isTextChangeFrom;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;


public class RxExampleActivity extends AppCompatActivity implements RxExampleFragment.OnCountDownEnded
{
    @VisibleForTesting
    private
    RxExampleFragment fragment;
    private TextView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_example);

        // [Uncomment for runtime monitoring] EventMonitor.getInstance().initialize();

        EventMonitor.getInstance().fireCustomEvent(new ActivityLifecycleEvent(RxExampleActivity.class, ON_CREATE));

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

        if(result) resultView.setText(R.string.completed);
        else resultView.setText(R.string.failed);
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


        eventMonitor.checkThat("There is no Activity->Fragment callback",
                existsAnEventThat(isCallbackEvent("Activity->Fragment")));

        eventMonitor.checkThat("There is no Fragment->Activity callback",
                existsAnEventThat(isCallbackEvent("Fragment->Activity")));

        eventMonitor.checkThat("The result view is never updated!",
                existsAnEventThat(TextChangeEvent.isTextChange(resultView, is(equalTo("Completed!")))));

        eventMonitor.checkThat("The callback Activity->Fragment happens after the result is written",
                anEventThat(isCallbackEvent("Activity->Fragment"))
                    .canHappenOnlyBefore(anEventThat(isTextChangeFrom(resultView))));

        eventMonitor.checkThat("Custom check not working!",
                myDescriptor()
                    .myCheck());
    }

    @Override
    public void onDestroy()
    {
        // [Uncomment for runtime monitoring] EventMonitor.getInstance().stopVerification();

        super.onDestroy();
    }
}