package common.commands;

import common.client.Request;
import common.exceptions.InputException;
import common.managements.ProductProvider;
import common.parameters.Product;

public class Add implements RequestBuilder{
    @Override
    public Request build(String[] tokens) throws InputException {
         Product product = ProductProvider.getProduct();
        return new Request.Builder("add").addWorker(product).build();
    }

    @Override
    public String getDescription() {
        return "add {element} : добавить новый элемент в коллекцию";
    }
}
