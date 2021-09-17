package common.client;

import common.exceptions.InputException;

public interface CheckInterface<T> {
    T get(String input) throws InputException;
}
