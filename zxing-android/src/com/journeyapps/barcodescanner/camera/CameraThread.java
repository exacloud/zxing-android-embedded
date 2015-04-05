package com.journeyapps.barcodescanner.camera;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

/**
 * Singleton thread that is started and stopped on demand.
 *
 * Any access to Camera / CameraManager should happen on this thread, through CameraInstance.
 */
class CameraThread {
  private static final String TAG = CameraThread.class.getSimpleName();

  private static CameraThread instance;

  public static CameraThread getInstance() {
    if(instance == null) {
      instance = new CameraThread();
    }
    return instance;
  }


  private Handler handler;
  private HandlerThread thread;

  private int openCount = 0;

  private final Object LOCK = new Object();


  private CameraThread() {
  }

  /**
   * Call from main thread.
   *
   * @param runnable
   */
  protected void enqueue(Runnable runnable) {
    synchronized (LOCK) {
      if (this.handler == null) {
        if(openCount <= 0) {
          throw new IllegalStateException("CameraThread is not open");
        }
        Log.d(TAG, "Opening thread");
        this.thread = new HandlerThread("CameraThread");
        this.thread.start();
        this.handler = new Handler(thread.getLooper());
      }
      this.handler.post(runnable);
    }
  }

  /**
   * Call from camera thread.
   */
  private void quit() {
    Log.d(TAG, "Closing thread");
    synchronized (LOCK) {
      this.thread.quit();
      this.thread = null;
      this.handler = null;
    }
  }

  /**
   * Call from camera thread
   */
  protected void decrementInstances() {
    synchronized (LOCK) {
      openCount -= 1;
      if (openCount == 0) {
        quit();
      }
    }
  }

  /**
   * Call from main thread.
   *
   * @param runner
   */
  protected void incrementAndEnqueue(Runnable runner) {
    synchronized (LOCK) {
      openCount += 1;
      enqueue(runner);
    }
  }
}