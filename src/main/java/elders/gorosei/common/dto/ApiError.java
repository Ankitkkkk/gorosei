package elders.gorosei.common.dto;

import elders.gorosei.errors.ResponseException;

public final class ApiError extends ApiResponse{
  public ApiError(ResponseException res, String traceId) {
    super.status = false;
    super.data = new ApiError.Error(res.getMessage(), res.getCode(), traceId, res.getData());
  }

  public static class Error {
    public final String message;
    public final String traceId;
    public final String code;
    public final Object error;

    Error(final String message, String code, final String traceId, final Object error) {
      this.message = message;
      this.traceId = traceId;
      this.error = error;
      this.code = code;
    }

    public String getCode() {
      return code;
    }

    public String getMessage() {
      return message;
    }

    public String getTraceId() {
      return traceId;
    }

    public Object getError() {
      return error;
    }
  }
}
