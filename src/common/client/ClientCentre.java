package common.client;

import common.commands.*;
import common.exceptions.CommandNotFoundException;
import common.exceptions.InputException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ClientCentre {
    private boolean exitInvoked = false;
    private final HashMap<String, RequestBuilder> availableRequestBuilder = new HashMap<>();
    private final BiConsumer<String, String> addDescription;
    private final Consumer<String[]> addHistoryLine;

    public ClientCentre(){
        this(new HashSet<>());
    }

    public ClientCentre(HashSet<String> superCoreScripts){
        Help help = new Help();
        History history = new History();
        addDescription = help::addDescription;
        addHistoryLine = history::addHistoryLine;
        addRequestBuilder(help);
        addRequestBuilder(new Info());
        addRequestBuilder(new Show());
        addRequestBuilder(new Add());
        addRequestBuilder(new Update());
        addRequestBuilder(new RemoveByID());
        addRequestBuilder(new Clear());
        addRequestBuilder(new ExecuteScript(superCoreScripts));
        addRequestBuilder(new Exit(()->{exitInvoked = true;}));
        addRequestBuilder(new AddIfMax());
        addRequestBuilder(new RemoveGreater());
        addRequestBuilder(new MinByName());
        addRequestBuilder(new SumOfManufacturerCost());
        addRequestBuilder(history);
    }

    public void addRequestBuilder(RequestBuilder requestBuilder){
        String description = requestBuilder.getDescription();
        String requestName = description.split(" ")[0];
        availableRequestBuilder.put(requestName, requestBuilder);
        addDescription.accept(requestName, description);
    }

    public boolean checkExitInvocation(){
        return exitInvoked;
    }

    public Request buildRequest(String input) throws InputException {
        if(input.equals(""))
            return null;
        String[] tokens = input.split(" ");
        for(String requestName : availableRequestBuilder.keySet()){
            if (requestName.equals(tokens[0])) {
                RequestBuilder requestBuilder= availableRequestBuilder.get(tokens[0]);
                Request request = requestBuilder.build(tokens);
                addHistoryLine.accept(tokens);
                return request;
            }
        }
        throw new CommandNotFoundException(tokens[0]);
    }
}
