package Router;

import Connection.Request;

public class MockRequest extends Request {
    private String method;
    private String path;
    private int errorCode;

    public MockRequest(String method, String path, int errorCode) {
        this.method = method;
        this.path = path;
        this.errorCode = errorCode;
    }
//Request already has getters, just needed to be able to set it
}
