package common.commands;

import common.client.Request;
import common.exceptions.InputException;
import common.managements.ProductProvider;
import common.parameters.Product;

public class RemoveGreater implements RequestBuilder{
    @Override
    public Request build(String[] tokens) throws InputException {
        Product product = ProductProvider.getProduct();
        return new Request.Builder("remove_greater").addWorker(product).build();
    }

    @Override
    public String getDescription() {
        return "remove_greater {element} : удалить из коллекции все элементы, превышающие заданный";
    }
}
