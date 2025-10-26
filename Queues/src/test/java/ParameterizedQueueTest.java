import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("QueueInterface Conformance Tests")
public class ParameterizedQueueTest {

    // Providers for ANY queue implementation (bounded or unbounded)
    static Stream<Arguments> queueProviders() {
        return Stream.of(
                Arguments.of("ArrayBoundedQueue", (Supplier<QueueInterface<Integer>>) () -> new ArrayBoundedQueue<>(10)),
                Arguments.of("LinkedQueue", (Supplier<QueueInterface<Integer>>) LinkedQueue::new)
        );
    }

    @Nested
    @DisplayName("Common behavior (all implementations)")
    class CommonBehavior {
        @ParameterizedTest(name = "{0} — create empty")
        @MethodSource("ParameterizedQueueTest#queueProviders")
        void test_create(String name, Supplier<QueueInterface<Integer>> factory) {
            QueueInterface<Integer> queue = factory.get();
            assertTrue(queue.isEmpty());
        }

        @ParameterizedTest(name = "{0} — enqueue")
        @MethodSource("ParameterizedQueueTest#queueProviders")
        void test_enqueue(String name, Supplier<QueueInterface<Integer>> factory) {
            QueueInterface<Integer> queue = factory.get();
            queue.enqueue(42);
            assertFalse(queue.isEmpty());
            assertEquals(42, queue.dequeue());
            
        }

        @ParameterizedTest(name = "{0} — dequeue")
        @MethodSource("ParameterizedQueueTest#queueProviders")
        void test_dequeue(String name, Supplier<QueueInterface<Integer>> factory) {
            QueueInterface<Integer> queue = factory.get();
            queue.enqueue(99);
            Integer val = queue.dequeue();
            assertEquals(99, val);
            assertTrue(queue.isEmpty());
            
        }

        @ParameterizedTest(name = "{0} — isFull")
        @MethodSource("ParameterizedQueueTest#queueProviders")
        void test_isFull(String name, Supplier<QueueInterface<Integer>> factory) {
            QueueInterface<Integer> queue = factory.get();
            if (queue instanceof ArrayBoundedQueue) {
                for (int i = 0; i < 10; i++) {
                    queue.enqueue(i);
                }
                assertTrue(queue.isFull(), "Bounded queue should be full after reaching capacity");
            } else {
                // Unbounded implementation should never be full
                for (int i = 0; i < 5; i++) {
                    queue.enqueue(i);
                }
                assertFalse(queue.isFull(), "Unbounded queue should never report full");
            }
        }

        @ParameterizedTest(name = "{0} — isEmpty")
        @MethodSource("ParameterizedQueueTest#queueProviders")
        void test_isEmpty(String name, Supplier<QueueInterface<Integer>> factory) {
            QueueInterface<Integer> queue = factory.get();
            // TODO: verify isEmpty before and after enqueue
            assertTrue(queue.isEmpty(), "Empty queue should be empty");
        }

        @ParameterizedTest(name = "{0} — size")
        @MethodSource("ParameterizedQueueTest#queueProviders")
        void test_size(String name, Supplier<QueueInterface<Integer>> factory) {
            QueueInterface<Integer> queue = factory.get();
            assertEquals(0, queue.size());
            queue.enqueue(1);
            assertEquals(1, queue.size());
            queue.enqueue(2);
            queue.enqueue(3);
            assertEquals(3, queue.size());
            queue.dequeue();
            assertEquals(2, queue.size());
        }

        @ParameterizedTest(name = "{0} — enqueue/dequeue")
        @MethodSource("ParameterizedQueueTest#queueProviders")
        void test_enqueue_dequeue(String name, Supplier<QueueInterface<Integer>> factory) {
            QueueInterface<Integer> queue = factory.get();
            // TODO: Verify that enqueue(1) makes the queue non-empty,
            //  dequeue() returns 1, and the queue becomes empty again
            queue.enqueue(1);
            assertFalse(queue.isEmpty(), "Empty queue should be empty");
            Integer value = queue.dequeue();
            assertEquals(1, value);
            assertTrue(queue.isEmpty(), "Empty queue should be empty");
        }

        @ParameterizedTest(name = "{0} — underflow on empty dequeue")
        @MethodSource("ParameterizedQueueTest#queueProviders")
        void test_underflow(String name, Supplier<QueueInterface<Integer>> factory) {
            QueueInterface<Integer> queue = factory.get();
            assertThrows(QueueUnderflowException.class, queue::dequeue);
        }
    }

    @Nested
    @DisplayName("ArrayBoundedQueue-specific behavior")
    class ArrayBoundedQueueSpecific {
        @Test
        @DisplayName("ArrayBoundedQueue — overflow exception")
        void test_overflow() {
            ArrayBoundedQueue<Integer> queue = new ArrayBoundedQueue<>(2);
            // TODO: enqueue elements and assert overflow behavior
            // Fill the queue to its limit
            queue.enqueue(1);
            queue.enqueue(2);

            // Now, adding one more should cause an overflow
            assertThrows(QueueOverflowException.class, () -> queue.enqueue(3),
                    "Enqueuing beyond capacity should throw QueueOverflowException");
        }
    }

    @Nested
    @DisplayName("LinkedQueue-specific behavior")
    class LinkedQueueSpecific {
        @Test
        @DisplayName("LinkedQueue — test specific feature")
        void test_linkedQueueSpecific() {
            LinkedQueue<Integer> queue = new LinkedQueue<>();
            // TODO: Add LinkedQueue-specific assertions
            queue.enqueue(1);
            queue.enqueue(2);
            queue.enqueue(3);

            // Should not be full
            assertFalse(queue.isFull());

            // Check FIFO order
            assertEquals(1, queue.dequeue());
            assertEquals(2, queue.dequeue());
            assertEquals(3, queue.dequeue());

            // Should be empty again
            assertTrue(queue.isEmpty());
        }
        // Add more LinkedQueue-specific tests here
    }
}
