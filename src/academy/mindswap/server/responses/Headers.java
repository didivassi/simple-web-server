package academy.mindswap.server.responses;

public final class Headers {

    public static final String HTML_200="HTTP/1.0 200 Document Follows\r\n";

    public static final String HTML_404="HTTP/1.0 404 Not Found\r\n";

    public static final String CONTENT_TYPE = "Content-Type: %s; charset=UTF-8\r\n" +
                                              "Content-Length: %d\r\n\r\n";


}
