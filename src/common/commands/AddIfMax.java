package common.commands;

import common.client.Request;
import common.exceptions.InputException;
import common.managements.ProductProvider;
import common.parameters.Product;

public class AddIfMax implements RequestBuilder{
    @Override
    public Request build(String[] tokens) throws InputException {
        Product product = ProductProvider.getProduct();
        return new Request.Builder("add_if_max").addWorker(product).build();
    }

    @Override
    public String getDescription() {
        return "add_if_max {element} : добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции";
    }
}
