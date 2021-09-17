package common.client;

import common.exceptions.ShutdownException;
import common.parameters.Product;
import common.server.Response;

import java.io.IOException;
import java.io.StreamCorruptedException;

public class Main {
    private static ConnectionSender connectionSender;
    private static ObjectSender<Request> requestSender;
    private static ObjectReceiver<Response> responseReceiver;

    public static void main(String[] args) {
        if (!connect())
            return;
        ClientCentre clientCore = new ClientCentre();
        sendRequest(new Request.Builder("get_collection_manager_message").build());
        InputProvider<Request> inputProvider = new InputProvider<>("Соединение установлено. Введите команду: ",
                clientCore::buildRequest);
        while (true) {
            try {
                Request request = inputProvider.provide();
                if (clientCore.checkExitInvocation())
                    break;
                if (request == null)
                    continue;
                sendRequest(request);
            } catch (ShutdownException exception) {
                break;
            }
        }
    }

    public static boolean reconnect() {
        try {
            connectionSender.close();
        } catch (IOException exception) {
            System.out.println("Не смог закрыть отправитель соедниения:");
            exception.printStackTrace();
            return false;
        }
        return connect();
    }

    private static boolean connect() {
        connectionSender = null;
        while (connectionSender == null) {
            try {
                connectionSender = new ConnectionSender(1022);
            } catch (IOException exception) {
                System.out.println("Не смог установить соединение с сервером:");
                exception.printStackTrace();
                if (needToStop())
                    return false;
            }
        }
        requestSender = new ObjectSender<>(connectionSender.getDataOutputStream());
        responseReceiver = new ObjectReceiver<>(connectionSender.getDataInputStream());
        return true;
    }

    public static void sendRequest(Request request) {
        boolean success = false;
        while (!success) {
            success = true;
            try {
                requestSender.send(request);
            } catch (IOException exception) {
                success = false;
                System.out.println("Не смог отправить запрос на сервер:");
                exception.printStackTrace();
                if (needToStop())
                    break;
            }
            if (success) {
                try {
                    Product product = new Product();
                    Response response = responseReceiver.receive();
                    System.out.println(response.getResponse());
                    if (request.getParameters() != null && request.getParameters().length == 7) {
                        response.getElements().forEach(worker -> System.out.print(product.getId()));
                        System.out.println();
                    }
                } catch (StreamCorruptedException exception) {
                    success = false;
                    exception.printStackTrace();
                    System.out.println(" Сервер отключился, переподключиться? [Y/n] ");
                    if (UserScanner.nextLineForced().equals("n") || !reconnect())
                        throw new ShutdownException();
                } catch (IOException|ClassNotFoundException exception) {
                    success = false;
                    System.out.println("Не смог получить ответ:");
                    exception.printStackTrace();
                    if (needToStop())
                        break;
                }
            }
        }
    }

    private static boolean needToStop() {
        System.out.println("Попробовать ещё раз переподключиться? [Y/n] ");
        return UserScanner.nextLineForced().equals("n");
    }
}
