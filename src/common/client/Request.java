package common.client;

import common.parameters.Product;

import java.io.Serializable;

public class Request implements Serializable {
    private final String requestName;
    private final String[] parameters;
    private final  Product product;

    private Request(String requestName, String[] parameters, Product product) {
        this.requestName = requestName;
        this.parameters = parameters;
        this.product = product;
    }

    public String getRequestName() {
        return requestName;
    }

    public String[] getParameters() {
        return parameters;
    }

    public Product getProduct() {
        return product;
    }

    public static class Builder {
        private final String requestName;
        private String[] parameters = null;
        private Product product = null;

        public Builder(String requestName) {
            this.requestName = requestName;
        }

        public Builder addParameters(String[] parameters) {
            this.parameters = parameters;
            return this;
        }

        public Builder addWorker(Product product) {
            this.product = product;
            return this;
        }

        public Request build() {
            return new Request(requestName, parameters, product);
        }
    }
}
