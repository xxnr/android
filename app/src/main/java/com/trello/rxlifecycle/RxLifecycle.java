/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.trello.rxlifecycle;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.View;
import com.jakewharton.rxbinding.view.RxView;
import rx.Observable;
import rx.functions.Func1;

import static com.trello.rxlifecycle.internal.Preconditions.checkNotNull;

public class RxLifecycle {

    private RxLifecycle() {
        throw new AssertionError("No instances");
    }

    /**
     * Deprecated and will be removed in a future release.
     *
     * Use {@link RxLifecycle#bindUntilEvent(Observable, Object)} instead, which does exactly the same thing.
     */
    @Deprecated
    @NonNull
    @CheckResult
    public static <T> LifecycleTransformer<T> bindUntilFragmentEvent(
        @NonNull final Observable<FragmentEvent> lifecycle, @NonNull final FragmentEvent event) {
        return bindUntilEvent(lifecycle, event);
    }

    /**
     * Deprecated and will be removed in a future release.
     *
     * Use {@link RxLifecycle#bindUntilEvent(Observable, Object)} instead, which does exactly the same thing.
     */
    @Deprecated
    @NonNull
    @CheckResult
    public static <T> LifecycleTransformer<T> bindUntilActivityEvent(
        @NonNull final Observable<ActivityEvent> lifecycle, @NonNull final ActivityEvent event) {
        return bindUntilEvent(lifecycle, event);
    }

    /**
     * Binds the given source to a lifecycle.
     * <p>
     * When the lifecycle event occurs, the source will cease to emit any notifications.
     * <p>
     * Use with {@link Observable#compose(Observable.Transformer)}:
     * {@code source.compose(RxLifecycle.bindUntilEvent(lifecycle, ActivityEvent.STOP)).subscribe()}
     *
     * @param lifecycle the lifecycle sequence
     * @param event the event which should conclude notifications from the source
     * @return a reusable {@link Observable.Transformer} that unsubscribes the source at the specified event
     */
    @NonNull
    @CheckResult
    public static <T, R> LifecycleTransformer<T> bindUntilEvent(@NonNull final Observable<R> lifecycle,
                                                                @NonNull final R event) {
        checkNotNull(lifecycle, "lifecycle == null");
        checkNotNull(event, "event == null");

        return new UntilEventObservableTransformer<>(lifecycle, event);
    }

    /**
     * Binds the given source to an Activity lifecycle.
     * <p>
     * Use with {@link Observable#compose(Observable.Transformer)}:
     * {@code source.compose(RxLifecycle.bindActivity(lifecycle)).subscribe()}
     * <p>
     * This helper automatically determines (based on the lifecycle sequence itself) when the source
     * should stop emitting items. In the case that the lifecycle sequence is in the
     * creation phase (CREATE, START, etc) it will choose the equivalent destructive phase (DESTROY,
     * STOP, etc). If used in the destructive phase, the notifications will cease at the next event;
     * for example, if used in PAUSE, it will unsubscribe in STOP.
     * <p>
     * Due to the differences between the Activity and Fragment lifecycles, this method should only
     * be used for an Activity lifecycle.
     *
     * @param lifecycle the lifecycle sequence of an Activity
     * * @return a reusable {@link Observable.Transformer} that unsubscribes the source during the Activity lifecycle
     */
    @NonNull
    @CheckResult
    public static <T> LifecycleTransformer<T> bindActivity(@NonNull final Observable<ActivityEvent> lifecycle) {
        return bind(lifecycle, ACTIVITY_LIFECYCLE);
    }

    /**
     * Binds the given source to a Fragment lifecycle.
     * <p>
     * Use with {@link Observable#compose(Observable.Transformer)}:
     * {@code source.compose(RxLifecycle.bindFragment(lifecycle)).subscribe()}
     * <p>
     * This helper automatically determines (based on the lifecycle sequence itself) when the source
     * should stop emitting items. In the case that the lifecycle sequence is in the
     * creation phase (CREATE, START, etc) it will choose the equivalent destructive phase (DESTROY,
     * STOP, etc). If used in the destructive phase, the notifications will cease at the next event;
     * for example, if used in PAUSE, it will unsubscribe in STOP.
     * <p>
     * Due to the differences between the Activity and Fragment lifecycles, this method should only
     * be used for a Fragment lifecycle.
     *
     * @param lifecycle the lifecycle sequence of a Fragment
     * @return a reusable {@link Observable.Transformer} that unsubscribes the source during the Fragment lifecycle
     */
    @NonNull
    @CheckResult
    public static <T> LifecycleTransformer<T> bindFragment(@NonNull final Observable<FragmentEvent> lifecycle) {
        return bind(lifecycle, FRAGMENT_LIFECYCLE);
    }

    /**
     * Binds the given source to a View lifecycle.
     * <p>
     * Specifically, when the View detaches from the window, the sequence will be completed.
     * <p>
     * Use with {@link Observable#compose(Observable.Transformer)}:
     * {@code source.compose(RxLifecycle.bindView(lifecycle)).subscribe()}
     * <p>
     * Warning: you should make sure to use the returned Transformer on the main thread,
     * since we're binding to a View (which only allows binding on the main thread).
     *
     * @param view the view to bind the source sequence to
     * @return a reusable {@link Observable.Transformer} that unsubscribes the source during the View lifecycle
     */
    @NonNull
    @CheckResult
    public static <T> LifecycleTransformer<T> bindView(@NonNull final View view) {
        checkNotNull(view, "view == null");

        return bind(RxView.detaches(view));
    }

    /**
     * Deprecated and will be removed in a future release.
     *
     * Use {@link RxLifecycle#bind(Observable)} instead, which does exactly the same thing.
     */
    @Deprecated
    @NonNull
    @CheckResult
    public static <T, E> LifecycleTransformer<T> bindView(@NonNull final Observable<? extends E> lifecycle) {
        return bind(lifecycle);
    }

    /**
     * Binds the given source to a lifecycle.
     * <p>
     * Use with {@link Observable#compose(Observable.Transformer)}:
     * {@code source.compose(RxLifecycle.bind(lifecycle)).subscribe()}
     * <p>
     * This helper automatically determines (based on the lifecycle sequence itself) when the source
     * should stop emitting items. Note that for this method, it assumes <em>any</em> event
     * emitted by the given lifecycle indicates that the lifecycle is over.
     *
     * @param lifecycle the lifecycle sequence
     * @return a reusable {@link Observable.Transformer} that unsubscribes the source whenever the lifecycle emits
     */
    @NonNull
    @CheckResult
    public static <T, R> LifecycleTransformer<T> bind(@NonNull final Observable<R> lifecycle) {
        checkNotNull(lifecycle, "lifecycle == null");

        return new UntilLifecycleObservableTransformer<>(lifecycle);
    }

    /**
     * Binds the given source to a lifecycle.
     * <p>
     * This method determines (based on the lifecycle sequence itself) when the source
     * should stop emitting items. It uses the provided correspondingEvents function to determine
     * when to unsubscribe.
     * <p>
     * Note that this is an advanced usage of the library and should generally be used only if you
     * really know what you're doing with a given lifecycle.
     *
     * @param lifecycle the lifecycle sequence
     * @param correspondingEvents a function which tells the source when to unsubscribe
     * @return a reusable {@link Observable.Transformer} that unsubscribes the source during the Fragment lifecycle
     */
    @NonNull
    @CheckResult
    public static <T, R> LifecycleTransformer<T> bind(@NonNull Observable<R> lifecycle,
                                                      @NonNull final Func1<R, R> correspondingEvents) {
        checkNotNull(lifecycle, "lifecycle == null");
        checkNotNull(correspondingEvents, "correspondingEvents == null");

        // Keep emitting from source until the corresponding event occurs in the lifecycle
        return new UntilCorrespondingEventObservableTransformer<>(lifecycle.share(), correspondingEvents);
    }

    // Figures out which corresponding next lifecycle event in which to unsubscribe, for Activities
    private static final Func1<ActivityEvent, ActivityEvent> ACTIVITY_LIFECYCLE =
        new Func1<ActivityEvent, ActivityEvent>() {
            @Override
            public ActivityEvent call(ActivityEvent lastEvent) {
                switch (lastEvent) {
                    case CREATE:
                        return ActivityEvent.DESTROY;
                    case START:
                        return ActivityEvent.STOP;
                    case RESUME:
                        return ActivityEvent.PAUSE;
                    case PAUSE:
                        return ActivityEvent.STOP;
                    case STOP:
                        return ActivityEvent.DESTROY;
                    case DESTROY:
                        throw new OutsideLifecycleException("Cannot bind to Activity lifecycle when outside of it.");
                    default:
                        throw new UnsupportedOperationException("Binding to " + lastEvent + " not yet implemented");
                }
            }
        };

    // Figures out which corresponding next lifecycle event in which to unsubscribe, for Fragments
    private static final Func1<FragmentEvent, FragmentEvent> FRAGMENT_LIFECYCLE =
        new Func1<FragmentEvent, FragmentEvent>() {
            @Override
            public FragmentEvent call(FragmentEvent lastEvent) {
                switch (lastEvent) {
                    case ATTACH:
                        return FragmentEvent.DETACH;
                    case CREATE:
                        return FragmentEvent.DESTROY;
                    case CREATE_VIEW:
                        return FragmentEvent.DESTROY_VIEW;
                    case START:
                        return FragmentEvent.STOP;
                    case RESUME:
                        return FragmentEvent.PAUSE;
                    case PAUSE:
                        return FragmentEvent.STOP;
                    case STOP:
                        return FragmentEvent.DESTROY_VIEW;
                    case DESTROY_VIEW:
                        return FragmentEvent.DESTROY;
                    case DESTROY:
                        return FragmentEvent.DETACH;
                    case DETACH:
                        throw new OutsideLifecycleException("Cannot bind to Fragment lifecycle when outside of it.");
                    default:
                        throw new UnsupportedOperationException("Binding to " + lastEvent + " not yet implemented");
                }
            }
        };

}
