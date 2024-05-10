package elders.gorosei;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.TextInput;
import elders.gorosei.common.dao.Attrs;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import org.apache.catalina.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/webhook")
public class WebhookController {
  private final String SUPER_ADMIN_AUTH = "fa4bca8b-96bd-47ab-a53a-3576efa13c64";
  private final RestTemplate restTemplate;
  private final ObjectMapper mapper;

  @Autowired
  public WebhookController(RestTemplateBuilder builder, ObjectMapper mapper) {
    this.restTemplate = builder.build();
    this.mapper = mapper;
  }
  private final Logger logger = LoggerFactory.getLogger("application.Webhook");
  @PostMapping
  public ResponseEntity<Object> webhookPost(HttpServletRequest request) {
    logger.info("request recieved ....");
    JsonNode node = (JsonNode)  request.getAttribute(Attrs.JSON_BODY);
    logger.info("notty node: {}", node);
    JsonNode queryResult = node.get("queryResult");
    logger.info("query result : {}", queryResult);
    final String sessionId = "";
    if (!isSessionValid(sessionId)) {
      return ResponseEntity.ok(new IPUResponse("You don't have permission to access this resource."));
    }
    final String scenario = ((Supplier<String>)() -> {
      try {
        return queryResult.get("intent").get("displayName").asText();
      } catch (Exception exception) {
        logger.error("something went wrong : {}", exception.getMessage(), exception);
        return Scenario.FAILED;
      }
    }).get();

    final String orderId = ((Supplier<String>)() -> {
      try {
        return Integer.parseInt(queryResult.get("parameters").get("orderId").asText()) + "";
      } catch (Exception exception) {
        logger.error("something went wrong : {}", exception.getMessage(), exception);
        return Scenario.FAILED;
      }
    }).get();
    logger.info("order id is: {}", orderId);


    logger.info("got your scenario: {}", scenario);

    try {
      return ResponseEntity.ok(switch (scenario) {
        case Scenario.TRACK_PAYMENT_STATUS_ID -> getPaymentStatus(orderId);
        case Scenario.TRACK_ORDER_STATUS_ID -> getOrderStatus(orderId);
        default -> anyOther();
      });
    } catch (Exception err) {
      logger.error("Failed to send proper message: {}", err.getMessage(), err);
    }

    return ResponseEntity.ok(new IPUResponse("Something went wrong ..."));
  }
  private boolean isSessionValid(String sessionId) {
    return true;
  }

  private IPUResponse getOrderStatus(String orderId) {
    final Optional<String> data = getDataFromProd(orderId);
    logger.info("response received from stage: {}", data);
    if (data.isEmpty()) {
      return new IPUResponse("cannot find anything related to this orderId: " + orderId + ". Is there Something else I can help you with.");
    }
    Optional<OrderDetails> details = getDetails(data.get());
    logger.info("details fetched : {}", details);
    if (details.isEmpty()) {
      return new IPUResponse("cannot find anything related to this orderId: " + orderId + ". Is there Something else I can help you with.");
    }
    OrderDetails order = details.get();
    if (StringUtils.isEmpty(order.orderStatus)) {
      return new IPUResponse("Not able to get Order Status. Is there any thing else I can help u with?");
    }
    return new IPUResponse("your order status is : " + (StringUtils.capitalize(order.orderStatus)) + ". Is there any thing else I can help u with?");
  }
  private IPUResponse anyOther() {
    throw new RuntimeException("Some thing went wrong .............");
  }
  private IPUResponse getPaymentStatus(String orderId) {
    final Optional<String> data = getDataFromProd(orderId);
    logger.info("response received from stage: {}", data);
    if (data.isEmpty()) {
      return new IPUResponse("cannot find anything related to this orderId: " + orderId + ". Is there Something else I can help you with.");
    }
    Optional<OrderDetails> details = getDetails(data.get());
    logger.info("details fetched : {}", details);
    if (details.isEmpty()) {
      return new IPUResponse("cannot find anything related to this orderId: " + orderId + ". Is there Something else I can help you with.");
    }
      OrderDetails order = details.get();
    if (StringUtils.isEmpty(order.paymentStatus)) {
      return new IPUResponse("Cannot get Order Payment status right now. Is there any thing else I can help u with?");
    }
    return new IPUResponse("your order payment status is : " + (StringUtils.capitalize(order.paymentStatus)) + ", and payment gateway is : " + (StringUtils.capitalize(order.paymentGateway)) + ", and Total Paid Amount was : " + (StringUtils.capitalize(order.PaymentAmount)) + ". Is there any thing else I can help u with?");
  }

  private Optional<String> getDataFromProd(String orderId) {
    final String url = "https://stage-ecatering.ipsator.com/api/v1/order/" + orderId;
    final HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", SUPER_ADMIN_AUTH);
    headers.add("Accept", "application/json");
    headers.add("Content-Type", "application.json");
    HttpEntity<String> entity = new HttpEntity<>(headers);

    ResponseEntity<String> response = null;
    try {
        response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    } catch (Exception err) {
      return Optional.empty();
    }
    if (response == null) {
      return Optional.empty();
    }
    if (HttpStatusCode.valueOf(200).equals(response.getStatusCode())) {
      return Optional.ofNullable(response.getBody());
    }
    return Optional.empty();
  }
  public record IPUResponse(String fulfillmentText) {
  }
  // todo menu items can also be added in next phase
  public record OrderDetails(String orderStatus, String paymentStatus, String paymentGateway, String PaymentType, String PaymentAmount) {
  }
  private Optional<OrderDetails> getDetails(String data) {
    logger.info("data before parse: {}", data);
    JsonNode node = null;
    try {
        node = mapper.readTree(data);
    } catch (Exception ignore) {}
    if (node == null) {
      return Optional.empty();
    }
    final JsonNode json = node;
    final String orderStatus = ((Supplier<String>)() -> {
      try {
        return json.get("result").get("status").asText();
      } catch (Exception ignore) {}
      return null;
    }).get();

    final String paymentStatus = ((Supplier<String>)() -> {
      try {
        return json.get("result").get("orderPayment").get("paymentStatus").asText();
      } catch (Exception ignore) {
        logger.info("here error in payment status: {}", ignore.getMessage(), ignore);
      }
      return null;
    }).get();

    final String paymentGateway = ((Supplier<String>)() -> {
      try {
        return json.get("result").get("orderPayment").get("paymentGateway").asText();
      } catch (Exception ignore) {}
      return null;
    }).get();

    final String paymentType = ((Supplier<String>)() -> {
      try {
        return json.get("result").get("paymentType").asText();
      } catch (Exception ignore) {}
      return null;
    }).get();
    final String paymentAmount = ((Supplier<String>)() -> {
      try {
        return json.get("result").get("orderPayment").get("paymentAmount").asText();
      } catch (Exception ignore) {}
      return null;
    }).get();
    return Optional.of(new OrderDetails(orderStatus, paymentStatus, paymentGateway, paymentType, paymentAmount));
  }
}

