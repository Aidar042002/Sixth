package common.commands;

import common.client.Request;
import common.exceptions.InputException;

public class SumOfManufacturerCost implements RequestBuilder{
    @Override
    public Request build(String[] tokens) throws InputException {
        return new Request.Builder("sum_of_manufacturer_cost").build();
    }

    @Override
    public String getDescription() {
        return "sum_of_manufacture_cost : вывести сумму значений поля manufactureCost для всех элементов коллекции";
    }
}
