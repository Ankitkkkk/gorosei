package elders.gorosei.common.dto;

public class ApiSuccess extends ApiResponse{
  public ApiSuccess() {
    super.status = true;
  }
  public ApiSuccess(Object data) {
    super.status = true;
    super.data = data;
  }
  public ApiSuccess(String message, Object data) {
    super.status = true;
    super.data = data;
    super.message = message;
  }

}
