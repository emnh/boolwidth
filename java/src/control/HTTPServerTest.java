package control;

/**
 * Created by emh on 11/2/2014.
 */

import java.io.IOException;
import java.io.OutputStream;

import com.cedarsoftware.util.io.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import control.http.HTTPResultsServer;
import org.json.simple.JSONObject;

public class HTTPServerTest {

    public static void main(String[] args) throws Exception {
        HTTPResultsServer hrServer = new HTTPResultsServer();
        JSONObject test = new JSONObject();
        test.put("test", "hello");
        hrServer.addResult("test", test);

    }
}
