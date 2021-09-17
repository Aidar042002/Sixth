package common.commands;

import common.client.Request;
import common.exceptions.InputException;

public interface RequestBuilder {
    Request build(String[] tokens) throws InputException;
    String getDescription();
}
