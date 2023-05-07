package androidx.paging;



import androidx.annotation.AnyThread;
import androidx.annotation.GuardedBy;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;


public class PagingRequestHelper {
    private final Object mLock = new Object();
    private final Executor mRetryService;
    @GuardedBy("mLock")
    private final RequestQueue[] mRequestQueues = new RequestQueue[]
    {new RequestQueue(RequestType.INITIAL),
        new RequestQueue(RequestType.BEFORE),
        new RequestQueue(RequestType.AFTER)};
    @NonNull
    final CopyOnWriteArrayList<Listener> mListeners = new CopyOnWriteArrayList<>();

    public PagingRequestHelper(@NonNull Executor retryService) {
        mRetryService = retryService;
    }

    @AnyThread
    public boolean addListener(@NonNull Listener listener) {
        return mListeners.add(listener);
    }

    public boolean removeListener(@NonNull Listener listener) {
        return mListeners.remove(listener);
    }

    @SuppressWarnings("WeakerAccess")
    @AnyThread
    public boolean runIfNotRunning(@NonNull RequestType type, @NonNull Request request) {
        boolean hasListeners = !mListeners.isEmpty();
        StatusReport report = null;
        synchronized (mLock) {
            RequestQueue queue = mRequestQueues[type.ordinal()];
            if (queue.mRunning != null) {
                return false;
            }
            queue.mRunning = request;
            queue.mStatus = Status.RUNNING;
            queue.mFailed = null;
            queue.mLastError = null;
            if (hasListeners) {
                report = prepareStatusReportLocked();
            }
        }
        if (report != null) {
            dispatchReport(report);
        }
        final RequestWrapper wrapper = new RequestWrapper(request, this, type);
        wrapper.run();
        return true;
    }
    @GuardedBy("mLock")
    private StatusReport prepareStatusReportLocked() {
        Throwable[] errors = new Throwable[]{
            mRequestQueues[0].mLastError,
            mRequestQueues[1].mLastError,
            mRequestQueues[2].mLastError
        };
        return new StatusReport(
            getStatusForLocked(RequestType.INITIAL),
        getStatusForLocked(RequestType.BEFORE),
        getStatusForLocked(RequestType.AFTER),
        errors
        );
    }
    @GuardedBy("mLock")
    private Status getStatusForLocked(RequestType type) {
        return mRequestQueues[type.ordinal()].mStatus;
    }
    @AnyThread
    @VisibleForTesting
    void recordResult(@NonNull RequestWrapper wrapper, @Nullable Throwable throwable) {
        StatusReport report = null;
        final boolean success = throwable == null;
        boolean hasListeners = !mListeners.isEmpty();
        synchronized (mLock) {
            RequestQueue queue = mRequestQueues[wrapper.mType.ordinal()];
            queue.mRunning = null;
            queue.mLastError = throwable;
            if (success) {
                queue.mFailed = null;
                queue.mStatus = Status.SUCCESS;
            } else {
                queue.mFailed = wrapper;
                queue.mStatus = Status.FAILED;
            }
            if (hasListeners) {
                report = prepareStatusReportLocked();
            }
        }
        if (report != null) {
            dispatchReport(report);
        }
    }
    private void dispatchReport(StatusReport report) {
        for (Listener listener : mListeners) {
            listener.onStatusChange(report);
        }
    }

    public boolean retryAllFailed() {
        final RequestWrapper[] toBeRetried = new RequestWrapper[RequestType.values().length];
        boolean retried = false;
        synchronized (mLock) {
            for (int i = 0; i < RequestType.values().length; i++) {
            toBeRetried[i] = mRequestQueues[i].mFailed;
            mRequestQueues[i].mFailed = null;
        }
        }
        for (RequestWrapper failed : toBeRetried) {
            if (failed != null) {
                failed.retry(mRetryService);
                retried = true;
            }
        }
        return retried;
    }
    static class RequestWrapper implements Runnable {
        @NonNull
        final Request mRequest;
        @NonNull
        final PagingRequestHelper mHelper;
        @NonNull
        final RequestType mType;
        RequestWrapper(@NonNull Request request, @NonNull PagingRequestHelper helper,
            @NonNull RequestType type) {
            mRequest = request;
            mHelper = helper;
            mType = type;
        }
        @Override
        public void run() {
            mRequest.run(new Request.Callback(this, mHelper));
        }
        void retry(Executor service) {
            service.execute(new Runnable() {
                @Override
                public void run() {
                    mHelper.runIfNotRunning(mType, mRequest);
                }
            });
        }
    }

    @FunctionalInterface
    public interface Request {

        void run(Callback callback);

        class Callback {
            private final AtomicBoolean mCalled = new AtomicBoolean();
            private final RequestWrapper mWrapper;
            private final PagingRequestHelper mHelper;
            Callback(RequestWrapper wrapper, PagingRequestHelper helper) {
                mWrapper = wrapper;
                mHelper = helper;
            }

            @SuppressWarnings("unused")
            public final void recordSuccess() {
                if (mCalled.compareAndSet(false, true)) {
                    mHelper.recordResult(mWrapper, null);
                } else {
                    throw new IllegalStateException(
                        "already called recordSuccess or recordFailure");
                }
            }

            @SuppressWarnings("unused")
            public final void recordFailure(@NonNull Throwable throwable) {
                //noinspection ConstantConditions
                if (throwable == null) {
                    throw new IllegalArgumentException("You must provide a throwable describing"
                        + " the error to record the failure");
                }
                if (mCalled.compareAndSet(false, true)) {
                    mHelper.recordResult(mWrapper, throwable);
                } else {
                    throw new IllegalStateException(
                        "already called recordSuccess or recordFailure");
                }
            }
        }
    }

    public static final class StatusReport {

        @NonNull
        public final Status initial;

        @NonNull
        public final Status before;

        @NonNull
        public final Status after;
        @NonNull
        private final Throwable[] mErrors;
        StatusReport(@NonNull Status initial, @NonNull Status before, @NonNull Status after,
        @NonNull Throwable[] errors) {
            this.initial = initial;
            this.before = before;
            this.after = after;
            this.mErrors = errors;
        }

        public boolean hasRunning() {
            return initial == Status.RUNNING
                || before == Status.RUNNING
                || after == Status.RUNNING;
        }

        public boolean hasError() {
            return initial == Status.FAILED
                || before == Status.FAILED
                || after == Status.FAILED;
        }

        @Nullable
        public Throwable getErrorFor(@NonNull RequestType type) {
            return mErrors[type.ordinal()];
        }
        @Override
        public String toString() {
            return "StatusReport{"
            + "initial=" + initial
            + ", before=" + before
            + ", after=" + after
            + ", mErrors=" + Arrays.toString(mErrors)
            + '}';
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StatusReport that = (StatusReport) o;
            if (initial != that.initial) return false;
            if (before != that.before) return false;
            if (after != that.after) return false;
            // Probably incorrect - comparing Object[] arrays with Arrays.equals
            return Arrays.equals(mErrors, that.mErrors);
        }
        @Override
        public int hashCode() {
            int result = initial.hashCode();
            result = 31 * result + before.hashCode();
            result = 31 * result + after.hashCode();
            result = 31 * result + Arrays.hashCode(mErrors);
            return result;
        }
    }

    public interface Listener {

        void onStatusChange(@NonNull StatusReport report);
    }

    public enum Status {

        RUNNING,

        SUCCESS,

        FAILED
    }

    public enum RequestType {

        INITIAL,

        BEFORE,

        AFTER
    }
    class RequestQueue {
        @NonNull
        final RequestType mRequestType;
        @Nullable
        RequestWrapper mFailed;
        @Nullable
        Request mRunning;
        @Nullable
        Throwable mLastError;
        @NonNull
        Status mStatus = Status.SUCCESS;
        RequestQueue(@NonNull RequestType requestType) {
            mRequestType = requestType;
        }
    }
}