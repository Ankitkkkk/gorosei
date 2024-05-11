package elders.gorosei.ws;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@EnableScheduling
public class WebSocketHandler extends TextWebSocketHandler {
  private final Logger logger = LoggerFactory.getLogger("application.websocket.handler");
  private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

  AtomicLong counter = new AtomicLong(0);
  @Scheduled(fixedRate = 20000)
  public void heatBeat() {
    counter.incrementAndGet();
    try {
      sessions.forEach(session -> {
        try {
          session.sendMessage(new TextMessage("connected you are ... " + counter.get()));
        } catch (IOException ignore) {}
      });
    } catch (Exception ignore) {}
  }
  // <main>
  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    final String payload = message.getPayload();
    logger.info("incomming message: {}", payload);
    logger.info("connection count: {}", sessions.size());
    session.sendMessage(new TextMessage("hey did you say: " + payload));
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    sessions.add(session);
    logger.info("connection created: {}", session);
    logger.info("all sessions: {}", sessions);
    logger.info("connection count: {}", sessions.size());
    // Optionally send a welcome message
    session.sendMessage(new TextMessage("Welcome to the user WebSocket!"));
  }
  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    logger.info("connection closed: {}", session);
    sessions.remove(session);
    logger.info("all sessions: {}", sessions);
    logger.info("connection count: {}", sessions.size());
  }
  // </main>


  @Override
  public void handleMessage(WebSocketSession session, WebSocketMessage<?> message)
      throws Exception {
    super.handleMessage(session, message);
  }


  @Override
  protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
    super.handlePongMessage(session, message);
  }

  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    super.handleTransportError(session, exception);
  }



  @Override
  public boolean supportsPartialMessages() {
    return super.supportsPartialMessages();
  }
}
/* html code to connect to this ws://localhost:8080/user
<!DOCTYPE html>
<html>
<head>
    <title>WebSocket Example</title>
    <script>
        var socket;

        function connectWebSocket() {
            // Create a new WebSocket connection
            socket = new WebSocket("ws://localhost:8080/user");

            // Handle WebSocket open event
            socket.onopen = function() {
                console.log("WebSocket connection opened.");
            };

            // Handle WebSocket message event
            socket.onmessage = function(event) {
                console.log("Received message: " + event.data);
                var messages = document.getElementById("messages");
                var messageElement = document.createElement("div");
                messageElement.textContent = "Received message: " + event.data;
                messages.appendChild(messageElement);
                // You can update the UI or perform other actions here
            };

            // Handle WebSocket close event
            socket.onclose = function(event) {
                console.log("WebSocket connection closed with code: " + event.code + ", reason: " + event.reason);
            };
        }

        function closeWebSocket() {
            // Close the WebSocket connection
            socket.close(1000, "Normal closure");
        }

        function sendMessage() {
            // Get the message from the input field
            var messageInput = document.getElementById("messageInput");
            var message = messageInput.value;

            // Send the message to the server
            socket.send(message);

            // Clear the input field
            messageInput.value = "";
        }
    </script>
</head>
<body>
    <h1>WebSocket Example</h1>
    <button onclick="connectWebSocket()">Connect</button>
    <button onclick="closeWebSocket()">Close</button>
    <br><br>
    <input type="text" id="messageInput" placeholder="Enter a message">
    <button onclick="sendMessage()">Send</button>
    <div id="messages"></div>
</body>
</html>
 */
