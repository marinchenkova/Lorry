package name.marinchenko.lorryvision.view.util.feedback;

import android.support.annotation.NonNull;

import java.util.PriorityQueue;

import name.marinchenko.lorryvision.view.util.ActivityInitializer;

/**
 * FeedbackAgent is a class to operate wirh Feedback messages.
 */

public class FeedbackAgent {

    private final PriorityQueue<FeedbackMessage> messageQueue = new PriorityQueue<>();

    public void add(final FeedbackMessage msg) {
        this.messageQueue.add(msg);
    }

    private static void send() {
        //TODO send feedback response
    }
}
