package common.server;

import common.parameters.Product;

import java.io.Serializable;
import java.util.LinkedHashSet;

public class Response implements Serializable {
    private final String response;
    private final LinkedHashSet<Product> elements;

    public Response(String response, LinkedHashSet<Product> elements) {
        this.response = response;
        this.elements = elements;
    }

    public String getResponse() {
        return response;
    }

    public LinkedHashSet<Product> getElements() {
        return elements;
    }
}
