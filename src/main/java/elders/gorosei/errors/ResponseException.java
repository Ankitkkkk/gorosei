package elders.gorosei.errors;

public class ResponseException extends RuntimeException{
  private final Integer responseCode;
  private final String code;
  private final String message;
  private final Object data;
  public ResponseException (Integer responseCode, String code, String message, Object data) {
    super(message);
    this.responseCode = responseCode;
    this.code = code;
    this.message = message;
    this.data = data;
  }

  public Integer getResponseCode() {
    return responseCode;
  }

  public String getCode() {
    return code;
  }

  @Override
  public String getMessage() {
    return message;
  }

  public Object getData() {
    return data;
  }
}
