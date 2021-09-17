package common.commands;

import common.client.Request;
import common.exceptions.InputException;

public class MinByName implements RequestBuilder {
    @Override
    public Request build(String[] tokens) throws InputException {
        return new Request.Builder("min_by_name").build();
    }

    @Override
    public String getDescription() {
        return "min_by_name : вывести любой объект из коллекции, значение поля name которого является минимальным";
    }
}
