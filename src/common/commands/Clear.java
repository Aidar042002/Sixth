package common.commands;

import common.client.Request;
import common.exceptions.InputException;

public class Clear implements RequestBuilder{
    @Override
    public Request build(String[] tokens) throws InputException {
        return new Request.Builder("clear").build();
    }

    @Override
    public String getDescription() {
        return "clear : очистить коллекцию";
    }
}
