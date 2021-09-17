package common.commands;

import common.client.*;
import common.exceptions.EmptyFieldException;
import common.exceptions.InputException;
import common.exceptions.RecursiveScriptExecutionException;
import common.managements.FileControl;

import java.util.HashSet;

public class ExecuteScript implements RequestBuilder{
    private final HashSet<String> superCoreScripts;

    public ExecuteScript(HashSet<String> superCoreScripts) {
        this.superCoreScripts = superCoreScripts;
    }

    @Override
    public Request build(String[] tokens) throws InputException {
        if (tokens.length < 2)
            throw new EmptyFieldException("путь к файлу");
        String script = FileControl.readFromFile(tokens[1]);
        if (superCoreScripts.contains(script))
            throw new RecursiveScriptExecutionException(tokens[1]);
        HashSet<String> subCoreScripts = new HashSet<>(superCoreScripts);
        subCoreScripts.add(script);
        ClientCentre subCore = new ClientCentre(subCoreScripts);
        System.out.println("Исполняю скрипт " + tokens[1] + "...\n");
        String[] lines = script.split("\\r?\\n");
        int previousSize = UserScanner.countInterLines();
        for (int i = lines.length-1; i >= 0; i--)
            UserScanner.interfere(lines[i]);
        while (UserScanner.countInterLines() > previousSize) {
            InputProvider<Request> inputProvider = new InputProvider<>("Введите команду: ", subCore::buildRequest,
                    () -> UserScanner.countInterLines() == previousSize);
            Request request = inputProvider.provide();
            if (subCore.checkExitInvocation()) {
                while (UserScanner.countInterLines() != previousSize)
                    UserScanner.nextLine(false);
            }
            if (request == null)
                continue;
            Main.sendRequest(request);
        }
        System.out.println("\nСкрипт " + tokens[1] + " закончил своё выполнение, выхожу из него");
        return null;
    }

    @Override
    public String getDescription() {
        return "execute_script file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.";
    }
}
