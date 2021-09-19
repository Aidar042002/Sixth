package common.server;

import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        boolean multithread = false;
        System.out.print("Сервер запущен. ");
        RequestHandler requestHandler = new RequestHandler();
        ConnectionReceiver connectionReceiver;
        try {
            connectionReceiver = new ConnectionReceiver(1092, requestHandler::handle);
        } catch (IOException exception) {
            System.out.println("Не смог инициализировать модуль приёма подключений:");
            exception.printStackTrace();
            return;
        }
        if (multithread) {
            Thread thread = new Thread(() -> {
                Scanner scanner = new Scanner(System.in);
                while (true) {
                    String line = scanner.nextLine();
                    if (line.equals("save"))
                        requestHandler.forceSave();
                    if (line.equals("show"))
                        connectionReceiver.showKeyHashCodes();
                    if (line.equals("exit")) {
                        requestHandler.forceSave();
                        connectionReceiver.shutdown();
                        break;
                    }
                }
            });
            thread.start();
        }
        while (connectionReceiver.isWorking()) {
            try {
                connectionReceiver.run();
            } catch (IOException exception) {
                System.out.println("Произошла ошибка при исполнении основного цикла:");
                exception.printStackTrace();
                break;
            } catch (CancelledKeyException exception) {
                System.out.println("-");
            }
        }
    }
}
