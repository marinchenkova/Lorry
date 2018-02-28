package name.marinchenko.lorryvision.view.util.feedback;

import android.support.annotation.NonNull;

/**
 * Feedback message to be sent.
 */

public class FeedbackMessage {

    public final String email;
    public final String subject;
    public final String type;
    public final String msg;


    public FeedbackMessage(@NonNull final String email,
                           @NonNull final String subject,
                           @NonNull final String type,
                           @NonNull final String msg) {
        this.email = email;
        this.subject = subject;
        this.type = type;
        this.msg = msg;
    }
}
