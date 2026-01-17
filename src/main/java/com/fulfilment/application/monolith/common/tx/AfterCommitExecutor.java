package com.fulfilment.application.monolith.common.tx;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Status;
import jakarta.transaction.Synchronization;
import jakarta.transaction.TransactionSynchronizationRegistry;

@ApplicationScoped
public class AfterCommitExecutor {

  @Inject
  TransactionSynchronizationRegistry txRegistry;

  public void run(Runnable action) {

    if (txRegistry.getTransactionStatus() == Status.STATUS_NO_TRANSACTION) {
      throw new IllegalStateException("No active transaction");
    }

    txRegistry.registerInterposedSynchronization(new Synchronization() {

      @Override
      public void beforeCompletion() {
        // no-op
      }

      @Override
      public void afterCompletion(int status) {
        if (status == Status.STATUS_COMMITTED) {
          action.run();
        }
      }
    });
  }
}
