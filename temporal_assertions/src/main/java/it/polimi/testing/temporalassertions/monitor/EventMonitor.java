package it.polimi.testing.temporalassertions.monitor;

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
    private Subscriber<Result> resultSubscriber;

    private boolean needToUpdateSubject = false;

    private EventMonitor()
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

        addObservable(internalObserver);

        subject = ReplaySubject.<Event>create();
    }

    public static synchronized EventMonitor getInstance()
    {
        if(instance==null) instance = new EventMonitor();
        return instance;
    }

    public void addObservable(Observable<? extends Event> observable)
    {
        //!Log.v("[...MONITOR...]", "Added observable "+observable);
        System.out.println(("[...MONITOR...] Added observable "+observable));

        if(merged==null) merged = observable;
        else merged = Observable.merge(merged, observable);

        needToUpdateSubject = true;
    }

    public void checkThat(Check check)
    {
        //!Log.v("[...MONITOR...]", "Added check "+check);
        System.out.println("[...MONITOR...] Added check "+check);

        checks.add(check);
    }

    public void verify()
    {
        verify(new Subscriber<Result>()
        {
            @Override
            public void onCompleted()
            {
                //!Log.v("[-----RESULT-----]", "COMPLETED!");
                System.out.println("[-----RESULT-----] COMPLETED!");
            }

            @Override
            public void onError(Throwable e)
            {
                //!Log.v("[-----RESULT-----]", "ERROR!");
                System.out.println("[-----RESULT-----] ERROR!");
            }

            @Override
            public void onNext(Result result)
            {
                //!Log.v("[-----RESULT-----]", ""+result);
                System.out.println("[-----RESULT-----] "+result);
            }
        });
    }

    public void verify(Subscriber<Result> resultSubscriber)
    {
        this.resultSubscriber = resultSubscriber;

        //!Log.v("[...MONITOR...]", "Verify");
        System.out.println("[...MONITOR...] Verify");

        buildSubject();

        Observable<Result> resultObservable = Observable.empty();
        for(Check check: checks)
        {
            System.out.println("[...MONITOR...] Lifted check "+check);
            resultObservable = Observable.merge(resultObservable, subject.lift(new EnforceCheck<>(check)));
        }
        checks.clear();

        resultObservable.subscribe(resultSubscriber);
    }

    private void buildSubject()
    {
        System.out.println("[...MONITOR...] build subject?");

        if(needToUpdateSubject)
        {
            System.out.println("[...MONITOR...] YES!");

            subject = ReplaySubject.<Event>create();
            merged.subscribe(subject);
            merged = null;

            needToUpdateSubject = false;
        }
    }

    public void stop()
    {
        if(subject!=null) subject.onCompleted();
        subject = null;

        if(subscriber!=null) subscriber.unsubscribe();
        subscriber = null;

        if(resultSubscriber!=null) resultSubscriber.unsubscribe();
        resultSubscriber = null;

        if(checks!=null) checks.clear();

        merged = null;
    }

    public void setSubscriber(Subscriber<? super Event> subscriber)
    {
        //!Log.v("[...MONITOR...]", "Set subscriber");
        System.out.println("[...MONITOR...] Set subscriber");

        this.subscriber = subscriber;

        buildSubject();

        subject.subscribe(subscriber);
    }

    public void fireCustomEvent(Event event)
    {
        if(manualEventListener==null)
        {
            //!Log.v("-----------------------", "manualEventListener is null! "+event+" will not be fired!");
            System.out.println("------------------ manualEventListener is null! "+event+" will not be fired!");

            return;
        }

        manualEventListener.onFireEvent(event);
    }

    private interface ManualEventListener
    {
        void onFireEvent(Event event);
    }
}
