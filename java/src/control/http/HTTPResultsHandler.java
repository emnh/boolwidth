package control.http;

import com.cedarsoftware.util.io.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by emh on 11/2/2014.
 */

public class HTTPResultsHandler implements HttpHandler {

    JSONObject result;

    public HTTPResultsHandler(JSONObject result) {
        this.result = result;
    }

    public void handle(HttpExchange t) throws IOException {
        String response = JsonWriter.formatJson(result.toJSONString());
        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

}