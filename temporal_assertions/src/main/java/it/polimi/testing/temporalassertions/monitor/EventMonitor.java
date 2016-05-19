package it.polimi.testing.temporalassertions.monitor;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import it.polimi.testing.temporalassertions.checks.Check;
import it.polimi.testing.temporalassertions.checks.Result;
import it.polimi.testing.temporalassertions.events.Event;
import it.polimi.testing.temporalassertions.operators.EnforceCheck;
import rx.Observable;
import rx.Subscriber;
import rx.subjects.ReplaySubject;
import rx.subjects.Subject;

public class EventMonitor
{
    private Observable<? extends Event> merged;
    private Subscriber<? super Event> subscriber;
    private Subject<? super Event, ? extends Event> subject;

    private List<Check> checks = new ArrayList<>();

    private ManualEventListener manualEventListener;

    private static EventMonitor instance;
    private Subscriber<Result> resultsSubscriber;

    private enum State
    {
        INITIALIZED, VERIFYING, STOPPED
    }
    private State state;

    private EventMonitor()
    {
        System.out.println("[...MONITOR...] Created");

        state = State.STOPPED;
    }

    public static synchronized EventMonitor getInstance()
    {
        System.out.println("[...MONITOR...] Get instance "+instance);

        if(instance==null) instance = new EventMonitor();
        return instance;
    }

    public void initialize()
    {
        if(isStopped())
        {
            System.out.println("[...MONITOR...] Initialized");

            state = State.INITIALIZED;

            setupInternalListener();
        }
    }

    public void startVerification(@Nullable Subscriber<? super Event> eventsSubscriber, @Nullable Subscriber<Result> resultsSubscriber)
    {
        if(isInitialized())
        {
            System.out.println("[...MONITOR...] Started verification");

            state = State.VERIFYING;

            initializeSubject();
            setupEventsSubscriber(eventsSubscriber);
            applyChecks(resultsSubscriber);
        }
    }

    public void observe(Observable<? extends Event> observable)
    {
        if(isInitialized())
        {
            System.out.println("[...MONITOR...] Added observable "+observable);

            if(merged==null) merged = observable;
            else merged = Observable.merge(merged, observable);
        }
    }

    public void fireCustomEvent(Event event)
    {
        if(isInitialized() || isVerifying())
        {
            if(manualEventListener==null)
            {
                System.out.println("------------------- manualEventListener is null, "+event+" skipped!");
                return;
            }

            manualEventListener.onFireEvent(event);
        }
    }

    public void checkThat(Check check)
    {
        if(isInitialized())
        {
            System.out.println("[...MONITOR...] Added check "+check);

            checks.add(check);
        }
    }

    public void stopVerification()
    {
        System.out.println("[...MONITOR...] Stop verification");

        if(isVerifying() && subject!=null) subject.onCompleted();

        state = State.STOPPED;

        cleanFields();
    }





    private boolean isInitialized()
    {
        return State.INITIALIZED.equals(state);
    }

    private boolean isVerifying()
    {
        return State.VERIFYING.equals(state);
    }

    private boolean isStopped()
    {
        return State.STOPPED.equals(state);
    }





    private void applyChecks(Subscriber<Result> resultsSubscriber)
    {
        if(resultsSubscriber==null)
        {
            resultsSubscriber = getDefaultResultsSubscriber();
        }
        this.resultsSubscriber = resultsSubscriber;

        Observable<Result> resultsObservable = Observable.empty();
        for(Check check: checks)
        {
            System.out.println("[...MONITOR...] Lifted check "+check);
            resultsObservable = Observable.merge(resultsObservable, subject.lift(new EnforceCheck<>(check)));
        }
        checks.clear();

        resultsObservable.subscribe(resultsSubscriber);
    }

    private Subscriber<Result> getDefaultResultsSubscriber()
    {
        return new Subscriber<Result>()
        {
            @Override
            public void onCompleted()
            {
                System.out.println("[-----RESULT-----] COMPLETED!");
            }

            @Override
            public void onError(Throwable e)
            {
                System.out.println("[-----RESULT-----] ERROR!");
            }

            @Override
            public void onNext(Result result)
            {
                System.out.println("[-----RESULT-----] "+result);
            }
        };
    }






    private void setupEventsSubscriber(Subscriber<? super Event> subscriber)
    {
        System.out.println("[...MONITOR...] Set subscriber");

        if(subscriber==null)
        {
            subscriber = getDefaultEventsSubscriber();
        }
        this.subscriber = subscriber;

        subject.subscribe(subscriber);
    }

    private Subscriber<? super Event> getDefaultEventsSubscriber()
    {
        return new Subscriber<Event>()
        {
            @Override
            public void onCompleted()
            {
                System.out.println("[---EVENT---] COMPLETED!");
            }

            @Override
            public void onError(Throwable e)
            {
                System.out.println("[---EVENT---] ERROR!");
            }

            @Override
            public void onNext(Event event)
            {
                System.out.println("[---EVENT---] "+event);
            }
        };
    }






    private void initializeSubject()
    {
        System.out.println("[...MONITOR...] Build subject");

        subject = ReplaySubject.<Event>create();
        merged.subscribe(subject);
    }

    private void setupInternalListener()
    {
        Observable<Event> internalObserver = Observable.create(new Observable.OnSubscribe<Event>()
        {
            @Override
            public void call(final Subscriber<? super Event> subscriber)
            {
                manualEventListener = new ManualEventListener()
                {
                    @Override
                    public void onFireEvent(Event event)
                    {
                        if(!subscriber.isUnsubscribed())
                        {
                            subscriber.onNext(event);
                        }
                    }
                };
            }
        });

        observe(internalObserver);
    }

    private void cleanFields()
    {
        subject = null;

        if(subscriber!=null) subscriber.unsubscribe();
        subscriber = null;

        if(resultsSubscriber!=null) resultsSubscriber.unsubscribe();
        resultsSubscriber = null;

        checks = null;

        merged = null;

        instance = null;
    }





    private interface ManualEventListener
    {
        void onFireEvent(Event event);
    }
}
