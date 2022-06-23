package interaction;

import java.io.Serializable;

public class Response implements Serializable {
    private final ResponseStatus responseStatus;
    private String responseBody;
    private Class type;

    public Response(ResponseStatus responseStatus, String responseBody, Class type) {
        this.responseStatus = responseStatus;
        this.responseBody = responseBody;
        this.type = type;
    }

    public Response(ResponseStatus responseStatus, String responseBody) {
        this.responseStatus = responseStatus;
        this.responseBody = responseBody;
    }

    public Response(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
