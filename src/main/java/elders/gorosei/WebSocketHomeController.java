package elders.gorosei;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@Controller
@CrossOrigin
public class WebSocketHomeController {
  @MessageMapping("/hello")
  @SendTo("/topic/greetings")
  public String greeting(String message) throws Exception {
    System.out.println("*****************************");
//    StompCommand.SUBSCRIBE
    return "Hello, " + message + "!";
  }

}
