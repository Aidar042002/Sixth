package common.commands;

import common.client.Request;
import common.exceptions.EmptyFieldException;
import common.exceptions.InputException;
import common.exceptions.InvalidFieldException;
import common.managements.ProductProvider;
import common.parameters.Product;

public class Update implements RequestBuilder{
    @Override
    public Request build(String[] tokens) throws InputException {
        if (tokens.length > 1) {
            try {
                Long.parseLong(tokens[1]);
            } catch (NumberFormatException exception) {
                throw new InvalidFieldException("ID");
            }
        } else {
            throw new EmptyFieldException("ID");
        }
        Product product = ProductProvider.getProduct();
        return new Request.Builder("update").addParameters(tokens).addWorker(product).build();
    }

    @Override
    public String getDescription() {
        return "update id {element} : обновить значение элемента коллекции, id которого равен заданному";
    }
}
