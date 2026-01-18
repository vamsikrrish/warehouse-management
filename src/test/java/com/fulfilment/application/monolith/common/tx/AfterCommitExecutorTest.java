package com.fulfilment.application.monolith.common.tx;

import jakarta.transaction.Status;
import jakarta.transaction.Synchronization;
import jakarta.transaction.TransactionSynchronizationRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AfterCommitExecutorTest {

    @Mock
    TransactionSynchronizationRegistry txRegistry;

    @InjectMocks
    AfterCommitExecutor afterCommitExecutor;

    @Test
    void run_NoActiveTransaction_ThrowsException() {
        // Arrange: Simulate no active transaction
        when(txRegistry.getTransactionStatus()).thenReturn(Status.STATUS_NO_TRANSACTION);
        Runnable action = mock(Runnable.class);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> afterCommitExecutor.run(action));
        verify(action, never()).run();
    }

    @Test
    void run_TransactionCommitted_ExecutesAction() {
        // Arrange
        when(txRegistry.getTransactionStatus()).thenReturn(Status.STATUS_ACTIVE);
        Runnable action = mock(Runnable.class);
        ArgumentCaptor<Synchronization> syncCaptor = ArgumentCaptor.forClass(Synchronization.class);

        // Act
        afterCommitExecutor.run(action);

        // Capture the registered synchronization and trigger afterCompletion
        verify(txRegistry).registerInterposedSynchronization(syncCaptor.capture());
        Synchronization capturedSync = syncCaptor.getValue();
        
        // Simulate a successful commit
        capturedSync.afterCompletion(Status.STATUS_COMMITTED);

        // Assert
        verify(action, times(1)).run();
    }

    @Test
    void run_TransactionRolledBack_DoesNotExecuteAction() {
        // Arrange
        when(txRegistry.getTransactionStatus()).thenReturn(Status.STATUS_ACTIVE);
        Runnable action = mock(Runnable.class);
        ArgumentCaptor<Synchronization> syncCaptor = ArgumentCaptor.forClass(Synchronization.class);

        // Act
        afterCommitExecutor.run(action);

        // Capture and trigger afterCompletion with a Rollback status
        verify(txRegistry).registerInterposedSynchronization(syncCaptor.capture());
        Synchronization capturedSync = syncCaptor.getValue();
        
        // Simulate a rollback
        capturedSync.afterCompletion(Status.STATUS_ROLLEDBACK);

        // Assert
        verify(action, never()).run();
    }

    @Test
    void beforeCompletion_DoesNothing() {
        // Arrange: For 100% coverage, we call the no-op method
        when(txRegistry.getTransactionStatus()).thenReturn(Status.STATUS_ACTIVE);
        ArgumentCaptor<Synchronization> syncCaptor = ArgumentCaptor.forClass(Synchronization.class);
        
        afterCommitExecutor.run(() -> {});
        verify(txRegistry).registerInterposedSynchronization(syncCaptor.capture());
        
        // Act & Assert: Should not throw any exception
        syncCaptor.getValue().beforeCompletion();
    }
}