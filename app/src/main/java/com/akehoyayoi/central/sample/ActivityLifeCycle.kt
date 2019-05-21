package com.akehoyayoi.central.sample

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent

class ActivityLifeCycle(lifeCycleCallback: IActivityLifeCycle) : LifecycleObserver {
    private val mCallback = lifeCycleCallback

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun create() {
        mCallback.onCreated()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun start() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun resume() {
        mCallback.onConnected()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stop() {
        mCallback.onDisconnect()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy() {
    }
}