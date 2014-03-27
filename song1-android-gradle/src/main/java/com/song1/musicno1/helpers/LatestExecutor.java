package com.song1.musicno1.helpers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * Created by windless on 3/27/14.
 */
public class LatestExecutor {
  protected final ExecutorService executorService;
  protected       Runnable        currentRunnable;

  public LatestExecutor() {
    executorService = Executors.newSingleThreadExecutor();
  }

  public void submit(Runnable runnable) {
    currentRunnable = runnable;
    try {
      executorService.submit(() -> {
        if (currentRunnable == runnable) {
          runnable.run();
        }
      });
    } catch (RejectedExecutionException ignored) {
    }
  }

  public void shutdown() {
    executorService.shutdown();
  }
}
