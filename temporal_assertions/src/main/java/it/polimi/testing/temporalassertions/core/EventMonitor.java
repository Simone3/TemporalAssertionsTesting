package it.polimi.testing.temporalassertions.core;

import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import it.polimi.testing.temporalassertions.events.Event;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.observers.Subscribers;
import rx.schedulers.Schedulers;
import rx.subjects.ReplaySubject;
import rx.subjects.Subject;

/**
 * This is the main interface of the library. It allows to register observables to build the event stream,
 * to add consistency checks on the events and to receive the results of the analysis.
 *
 * The monitor can be in three states:
 * - initialized (the monitor is ready and can receive observables and checks)
 * - verifying (all observables and checks have been added and the monitor is now processing the stream)
 * - stopped (the monitor is doing nothing and does not accept observables or checks)
 * Any action (except the call to {@link EventMonitor#initialize()} of course) that is performed while the monitor
 * is stopped has no effect on the system. This can for example be useful for debugging: if the call to {@code initialize()}
 * is commented out the monitor will do nothing even if observables and checks are added.
 */
public class EventMonitor
{
    private final static String TAG = "EventMonitor";
    private final static String RESULT_TAG = "EventMonitor-RESULT";
    private final static String EVENT_TAG = "EventMonitor-EVENT";

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
     * After this call it will not be possible to add further observables or consistency checks.
     * @param eventsSubscriber the subscriber that will receive in order all the events of the stream. You can pass:
     *                         - {@link EventMonitor#getLoggerEventsSubscriber()} or null to log all events in the console
     *                         - {@link Subscribers#empty()} to do nothing
     *                         - your own subscriber
     * @param resultsSubscriber the subscriber that will receive the results of the consistency checks. You can pass:
     *                          - {@link EventMonitor#getLoggerResultsSubscriber()} or null to log all results in the console
     *                          - {@link EventMonitor#getAssertionErrorResultsSubscriber()} to make the app crash if a check fails
     *                          - {@link Subscribers#empty()} to do nothing
     *                          - your own subscriber
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
     * custom event without creating an observable just for that. This method can be called
     * only after {@link EventMonitor#initialize()} and before {@link EventMonitor#stopVerification()}
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
            resultsSubscriber = getLoggerResultsSubscriber();
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
     * A simple results subscriber that can be passed to {@link EventMonitor#startVerification(Subscriber, Subscriber)}.
     * It just logs all the results in the console.
     * @return the results subscriber
     */
    public static Subscriber<Result> getLoggerResultsSubscriber()
    {
        return new Subscriber<Result>()
        {
            @Override
            public void onCompleted()
            {
                Log.v(RESULT_TAG, "All results received");
            }

            @Override
            public void onError(Throwable e)
            {
                Log.v(RESULT_TAG, "Error:");
                e.printStackTrace();
            }

            @Override
            public void onNext(Result result)
            {
                Log.v(RESULT_TAG, result.toString());
            }
        };
    }

    /**
     * A simple results subscriber that can be passed to {@link EventMonitor#startVerification(Subscriber, Subscriber)}.
     * It logs results with WARNING outcome in the console and makes the application crash (AssertionError) if a FAILURE result is received
     * @return the results subscriber
     */
    public static Subscriber<Result> getAssertionErrorResultsSubscriber()
    {
        return new Subscriber<Result>()
        {
            @Override
            public void onCompleted()
            {

            }

            @Override
            public void onError(Throwable e)
            {
                if(e instanceof AssertionError)
                {
                    throw (AssertionError) e;
                }
                else
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNext(Result result)
            {
                if(Outcome.WARNING.equals(result.getOutcome()))
                {
                    Log.w(TAG, result.toString());
                }
                else if(Outcome.FAILURE.equals(result.getOutcome()))
                {
                    onError(new AssertionError(TAG+": "+result));
                }
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
            eventsSubscriber = getLoggerEventsSubscriber();
        }
        this.subscriber = eventsSubscriber;

        // Subscribe to the subject
        subject.subscribe(eventsSubscriber);
    }

    /**
     * A simple events subscriber that can be passed to {@link EventMonitor#startVerification(Subscriber, Subscriber)}.
     * It just logs all events in the console.
     * @return the results subscriber
     */
    public static Subscriber<? super Event> getLoggerEventsSubscriber()
    {
        return new Subscriber<Event>()
        {
            @Override
            public void onCompleted()
            {
                Log.v(EVENT_TAG, "All events received");
            }

            @Override
            public void onError(Throwable e)
            {
                Log.v(EVENT_TAG, "Error:");
                e.printStackTrace();
            }

            @Override
            public void onNext(Event event)
            {
                Log.v(EVENT_TAG, event.toString());
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
