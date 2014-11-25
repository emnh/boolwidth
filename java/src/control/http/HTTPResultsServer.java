package control.http;

import com.sun.net.httpserver.HttpServer;
import org.json.simple.JSONObject;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by emh on 11/2/2014.
 */
public class HTTPResultsServer {
    HttpServer server = null;
    int port = 8000;

    public HTTPResultsServer() {
        while (server == null || port > 8050) {
            try {
                server = HttpServer.create(new InetSocketAddress(port), 0);
            } catch (IOException e) {
                System.out.printf("port %d in use, trying %d\n", port, port + 1);
                port += 1;
            }
        }
        JSONObject test = new JSONObject();
        test.put("hello", "world");
        server.createContext("/", new HTTPResultsHandler(test));
        String staticRelative = "/static";
        File root = new File((new File(".")).getAbsoluteFile().getParentFile().getParentFile(), "explorer");
        //System.out.printf("static root: %s\n", root);
        server.createContext(staticRelative, new StaticFilesHandler(staticRelative, root));
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    public void openBrowser(String page) {
        String url = String.format("http://localhost:%d/%s", port, page);
        System.out.printf("Opening results server on %s\n", url);
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException e) {
            System.out.printf("failed to launch browser at %s\n", url);
        } catch (URISyntaxException e) {
            System.out.printf("failed to create URI at %s\n", url);
        }
    }

    public void addResult(String name, JSONObject value) {
        server.createContext("/" + name, new HTTPResultsHandler(value));
    }
}
