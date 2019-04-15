package com.client.tok.rx;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class RxBus {
    private static PublishSubject publisher = PublishSubject.create();

    public static void publish(Object event) {
        publisher.onNext(event);
    }

    public static <T> Observable<T> listen(Class<T> eventType) {
        return publisher.ofType(eventType);
    }
}
