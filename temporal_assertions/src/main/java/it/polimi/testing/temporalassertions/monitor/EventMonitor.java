package it.polimi.testing.temporalassertions.monitor;

import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import it.polimi.testing.temporalassertions.checks.Check;
import it.polimi.testing.temporalassertions.checks.Result;
import it.polimi.testing.temporalassertions.events.Event;
import it.polimi.testing.temporalassertions.operators.EnforceCheck;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.ReplaySubject;
import rx.subjects.Subject;

/**
 * This is the main interface of the library. It allows to register observables to build the event stream,
 * to add consistency checks on the events and to receive the results of the analysis
 */
public class EventMonitor
{
    private final static String TAG = "EventMonitor";

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

    /**
     * Constructor
     */
    private EventMonitor()
    {
        state = State.STOPPED;
    }

    /**
     * Getter for the single instance of the monitor
     * @return the event monitor
     */
    public static synchronized EventMonitor getInstance()
    {
        if(instance==null) instance = new EventMonitor();
        return instance;
    }

    /**
     * This is called to start the monitor: after this call it will be possible to register observables and
     * add consistency checks (in any order)
     */
    public void initialize()
    {
        if(isStopped())
        {
            state = State.INITIALIZED;

            setupInternalListener();
        }
    }

    /**
     * This method starts the verification on the stream, usually called for example during onCreate of a component.
     * After this call it will not be possible to add further observables or consistency checks
     * @param eventsSubscriber the subscriber that will receive in order all the events of the stream (if null the default subscriber will be used)
     * @param resultsSubscriber the subscriber that will receive the results of the consistency checks (if null the default subscriber will be used)
     */
    public void startVerification(@Nullable Subscriber<? super Event> eventsSubscriber, @Nullable Subscriber<Result> resultsSubscriber)
    {
        if(isInitialized())
        {
            state = State.VERIFYING;

            initializeSubject();
            setupEventsSubscriber(eventsSubscriber);
            applyChecks(resultsSubscriber);
        }
    }

    /**
     * Allows to add an observable that will add its events to the monitored stream. The observables can be added
     * in any order. This method can be called only after {@link EventMonitor#initialize()} and before
     * {@link EventMonitor#startVerification(Subscriber, Subscriber)}
     * @param observable the event observable to be added
     */
    public void observe(Observable<? extends Event> observable)
    {
        if(isInitialized())
        {
            if(merged==null) merged = observable;
            else merged = Observable.merge(merged, observable);
        }
    }

    /**
     * Allows to fire an event outside the added observables, useful for example to fire any
     * custom event without creating an observable just for that
     * @param event the event that will be added to the stream
     */
    public void fireCustomEvent(Event event)
    {
        if(isInitialized() || isVerifying())
        {
            if(manualEventListener==null)
            {
                Log.v(TAG, "manualEventListener is null, "+event+" skipped!");
                return;
            }

            manualEventListener.onFireEvent(event);
        }
    }

    /**
     * Allows to add a consistency check that will have to hold in the monitored stream. The checks can be
     * added in any order.. This method can be called only after {@link EventMonitor#initialize()} and before
     * {@link EventMonitor#startVerification(Subscriber, Subscriber)}
     * @param failureMessage the identifying message for the check in case of failure
     * @param check the consistency check to be added
     */
    public void checkThat(String failureMessage, Check check)
    {
        if(isInitialized())
        {
            check.setUserFailureMessage(failureMessage);
            checks.add(check);
        }
    }

    /**
     * Allows to stop the verification, usually called for example during onDestroy of a component.
     * This will send an onCompleted event to the stream and the checks that are still in progress
     * (i.e. not short-circuited) will terminate.
     */
    public void stopVerification()
    {
        if(isVerifying() && subject!=null) subject.onCompleted();

        state = State.STOPPED;

        cleanFields();
    }

    /**
     * Internal helper to get the current state
     * @return true if initialize() has been called
     */
    private boolean isInitialized()
    {
        return State.INITIALIZED.equals(state);
    }

    /**
     * Internal helper to get the current state
     * @return true if startVerification() has been called
     */
    private boolean isVerifying()
    {
        return State.VERIFYING.equals(state);
    }

    /**
     * Internal helper to get the current state
     * @return true if stopVerification() has been called
     */
    private boolean isStopped()
    {
        return State.STOPPED.equals(state);
    }

    /**
     * This allows to apply the checks to the current stream and send them to the given subscriber
     * @param resultsSubscriber the subscriber that will receive the results of the consistency checks (if null the default subscriber will be used)
     */
    private void applyChecks(@Nullable Subscriber<Result> resultsSubscriber)
    {
        // Get default subscriber if needed
        if(resultsSubscriber==null)
        {
            resultsSubscriber = getDefaultResultsSubscriber();
        }
        this.resultsSubscriber = resultsSubscriber;

        // Apply "EnforceCheck" operator for each check and merge the returned observables
        Observable<Result> resultsObservable = Observable.empty();
        Observable<Result> singleResultObservable;
        for(final Check check: checks)
        {
            singleResultObservable = subject.lift(new EnforceCheck<>(check))
                                            .map(new Func1<Result, Result>()
                                            {
                                                @Override
                                                public Result call(Result result)
                                                {
                                                    result.setLinkedCheckDescription(check.getDescription());
                                                    result.setUserFailureMessage(check.getUserFailureMessage());
                                                    return result;
                                                }
                                            });

            resultsObservable = Observable.merge(resultsObservable, singleResultObservable);
        }
        checks.clear();

        // The given subscriber will receive all results
        resultsObservable.subscribe(resultsSubscriber);
    }

    /**
     * Getter
     * @return the default results subscriber
     */
    private Subscriber<Result> getDefaultResultsSubscriber()
    {
        return new Subscriber<Result>()
        {
            @Override
            public void onCompleted()
            {
                Log.v(TAG, "[-----RESULT-----] All results received");
            }

            @Override
            public void onError(Throwable e)
            {
                Log.v(TAG, "[-----RESULT-----] Error:");
                e.printStackTrace();
            }

            @Override
            public void onNext(Result result)
            {
                Log.v(TAG, "[-----RESULT-----] "+result);
            }
        };
    }

    /**
     * This allows to set the subscriber that will receive all events in the stream
     * @param eventsSubscriber the subscriber that will receive the results of the consistency checks (if null the default subscriber will be used)
     */
    private void setupEventsSubscriber(Subscriber<? super Event> eventsSubscriber)
    {
        // Get default subscriber if needed
        if(eventsSubscriber==null)
        {
            eventsSubscriber = getDefaultEventsSubscriber();
        }
        this.subscriber = eventsSubscriber;

        // Subscribe to the subject
        subject.subscribe(eventsSubscriber);
    }

    /**
     * Getter
     * @return the default events subscriber
     */
    private Subscriber<? super Event> getDefaultEventsSubscriber()
    {
        return new Subscriber<Event>()
        {
            @Override
            public void onCompleted()
            {
                Log.v(TAG, "[---EVENT---] All events received");
            }

            @Override
            public void onError(Throwable e)
            {
                Log.v(TAG, "[---EVENT---] Error:");
                e.printStackTrace();
            }

            @Override
            public void onNext(Event event)
            {
                Log.v(TAG, "[---EVENT---] "+event);
            }
        };
    }

    /**
     * Helper to create a subject from the given "merged" observable
     *
     * The subject is needed because during stopVerification we need to call onCompleted
     * to tell that the stream ended (in Android many streams never complete, e.g. text change
     * on a TextView can potentially generate events forever)
     */
    private void initializeSubject()
    {
        subject = ReplaySubject.<Event>create();
        merged.subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.newThread())
                .subscribe(subject);
    }

    /**
     * Helper to create an internal listener that will be used to fire custom events
     */
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

    /**
     * Helper to wipe the class fields after the monitor is stopped
     */
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

    /**
     * Internal listener that will be used to fire custom events
     */
    private interface ManualEventListener
    {
        /**
         * Called when a custom event is fired
         * @param event the fired event
         */
        void onFireEvent(Event event);
    }
}
