package boolwidth.greedysearch;

import org.json.simple.JSONObject;

/**
 * Created by emh on 11/2/2014.
 */
public interface ToJSONPostProcess {
    void accept(JSONObject obj, SimpleNode parent, SimpleNode node);
}
