package server.api;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.core.MessagePostProcessor;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;

import java.util.Map;

public class TestSimpMessageTemplate implements SimpMessageSendingOperations {

    /**
     * Send a message to the given user.
     *
     * @param user        the user that should receive the message.
     * @param destination the destination to send the message to.
     * @param payload     the payload to send
     */
    @Override
    public void convertAndSendToUser(String user, String destination, Object payload) throws MessagingException {

    }

    /**
     * Send a message to the given user.
     * <p>By default headers are interpreted as native headers (e.g. STOMP) and
     * are saved under a special key in the resulting Spring
     * {@link Message Message}. In effect when the
     * message leaves the application, the provided headers are included with it
     * and delivered to the destination (e.g. the STOMP client or broker).
     * <p>If the map already contains the key
     * {@link NativeMessageHeaderAccessor#NATIVE_HEADERS "nativeHeaders"}
     * or was prepared with
     * {@link SimpMessageHeaderAccessor SimpMessageHeaderAccessor}
     * then the headers are used directly. A common expected case is providing a
     * content type (to influence the message conversion) and native headers.
     * This may be done as follows:
     * <pre class="code">
     * SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create();
     * accessor.setContentType(MimeTypeUtils.TEXT_PLAIN);
     * accessor.setNativeHeader("foo", "bar");
     * accessor.setLeaveMutable(true);
     * MessageHeaders headers = accessor.getMessageHeaders();
     * messagingTemplate.convertAndSendToUser(user, destination, payload, headers);
     * </pre>
     * <p><strong>Note:</strong> if the {@code MessageHeaders} are mutable as in
     * the above example, implementations of this interface should take notice and
     * update the headers in the same instance (rather than copy or re-create it)
     * and then set it immutable before sending the final message.
     *
     * @param user        the user that should receive the message (must not be {@code null})
     * @param destination the destination to send the message to (must not be {@code null})
     * @param payload     the payload to send (may be {@code null})
     * @param headers     the message headers (may be {@code null})
     */
    @Override
    public void convertAndSendToUser(String user, String destination, Object payload, Map<String, Object> headers) throws MessagingException {

    }

    /**
     * Send a message to the given user.
     *
     * @param user          the user that should receive the message (must not be {@code null})
     * @param destination   the destination to send the message to (must not be {@code null})
     * @param payload       the payload to send (may be {@code null})
     * @param postProcessor a postProcessor to post-process or modify the created message
     */
    @Override
    public void convertAndSendToUser(String user, String destination, Object payload, MessagePostProcessor postProcessor) throws MessagingException {

    }

    /**
     * Send a message to the given user.
     * <p>See  for important
     * notes regarding the input headers.
     *
     * @param user          the user that should receive the message
     * @param destination   the destination to send the message to
     * @param payload       the payload to send
     * @param headers       the message headers
     * @param postProcessor a postProcessor to post-process or modify the created message
     */
    @Override
    public void convertAndSendToUser(String user, String destination, Object payload, Map<String, Object> headers, MessagePostProcessor postProcessor) throws MessagingException {

    }

    /**
     * Send a message to a default destination.
     *
     * @param message the message to send
     */
    @Override
    public void send(Message<?> message) throws MessagingException {

    }

    /**
     * Send a message to the given destination.
     *
     * @param destination the target destination
     * @param message     the message to send
     */
    @Override
    public void send(String destination, Message<?> message) throws MessagingException {

    }

    /**
     * Convert the given Object to serialized form, possibly using a
     * {@link MessageConverter},
     * wrap it as a message and send it to a default destination.
     *
     * @param payload the Object to use as payload
     */
    @Override
    public void convertAndSend(Object payload) throws MessagingException {

    }

    /**
     * Convert the given Object to serialized form, possibly using a
     * {@link MessageConverter},
     * wrap it as a message and send it to the given destination.
     *
     * @param destination the target destination
     * @param payload     the Object to use as payload
     */
    @Override
    public void convertAndSend(String destination, Object payload) throws MessagingException {

    }

    /**
     * Convert the given Object to serialized form, possibly using a
     * {@link MessageConverter},
     * wrap it as a message with the given headers and send it to
     * the given destination.
     *
     * @param destination the target destination
     * @param payload     the Object to use as payload
     * @param headers     the headers for the message to send
     */
    @Override
    public void convertAndSend(String destination, Object payload, Map<String, Object> headers) throws MessagingException {

    }

    /**
     * Convert the given Object to serialized form, possibly using a
     * {@link MessageConverter},
     * wrap it as a message, apply the given post processor, and send
     * the resulting message to a default destination.
     *
     * @param payload       the Object to use as payload
     * @param postProcessor the post processor to apply to the message
     */
    @Override
    public void convertAndSend(Object payload, MessagePostProcessor postProcessor) throws MessagingException {

    }

    /**
     * Convert the given Object to serialized form, possibly using a
     * {@link MessageConverter},
     * wrap it as a message, apply the given post processor, and send
     * the resulting message to the given destination.
     *
     * @param destination   the target destination
     * @param payload       the Object to use as payload
     * @param postProcessor the post processor to apply to the message
     */
    @Override
    public void convertAndSend(String destination, Object payload, MessagePostProcessor postProcessor) throws MessagingException {

    }

    /**
     * Convert the given Object to serialized form, possibly using a
     * {@link MessageConverter},
     * wrap it as a message with the given headers, apply the given post processor,
     * and send the resulting message to the given destination.
     *
     * @param destination   the target destination
     * @param payload       the Object to use as payload
     * @param headers       the headers for the message to send
     * @param postProcessor the post processor to apply to the message
     */
    @Override
    public void convertAndSend(String destination, Object payload, Map<String, Object> headers, MessagePostProcessor postProcessor) throws MessagingException {

    }
}
