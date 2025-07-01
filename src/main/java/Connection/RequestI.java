package Connection;

import java.util.HashMap;
import java.util.List;

public interface RequestI {

    String getMethod();

    String getPath();

    String getProtocol();

    int getErrorCode();

    boolean isValid();

    String getQueryString();

    HashMap<String, String> getHeaders();

    byte[] getBody();

    String getSegment();

    String getHeader(String name); //To lower case!

    HashMap<String, String> getCookies();
}
