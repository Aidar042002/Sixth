package common.server;

import common.client.Request;
import common.exceptions.InputException;
import common.managements.ProductProvider;
import common.parameters.Product;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.function.Function;

public class ServerCore {
    private final HashMap<String, Function<Request, String>> availableFunctions = new HashMap<>();
    private final CollectionManager collectionManager;
    private String collectionManagerMessage = "";

    public ServerCore(String filePath) {
        collectionManager = new CollectionManager(filePath, message -> collectionManagerMessage += message + "\n");
        availableFunctions.put("add", request -> {
            long ID = collectionManager.add(request.getProduct());
            return "Элемент успешно добавлен в коллекцию (ID: " + ID + ").";
        });
        availableFunctions.put("add_if_max", request -> {
            Product product = request.getProduct();
            Optional<Product> maxSoFar = collectionManager.elements.stream().max(Product::compareTo);
            if (!maxSoFar.isPresent() || product.compareTo(maxSoFar.get()) > 0) {
                long ID = collectionManager.add(product);
                return "Элемент успешно добавлен в коллекцию (ID: " + ID + ").";
            }
            return "А вот у работника с ID " + maxSoFar.get().getId() + " зарплата не меньше, поэтому я ничего в " +
                    "коллекцию не добавлю.";
        });
        availableFunctions.put("remove_greater", request -> {
            Product product = request.getProduct();
            Optional<Product> minSoFar = collectionManager.elements.stream().min(Product::compareTo);
            if (!minSoFar.isPresent() || product.compareTo(minSoFar.get()) < 0) {
                long ID = collectionManager.add(product);
                return "Элемент успешно добавлен в коллекцию (ID: " + ID + ").";
            }
            return "А вот у работника с ID " + minSoFar.get().getId() + " зарплата не больше, поэтому я ничего в коллекцию " +
                    "не добавлю.";
        });
        availableFunctions.put("clear", request -> {
            collectionManager.clear();
            return "Очистил коллекцию, теперь там пусто.";
        });
        availableFunctions.put("info", request -> collectionManager.getInfo());
        availableFunctions.put("min_by_name", request -> {
            Optional<Product> maxProduct = collectionManager.elements.stream()
                    .filter(product -> product.getUnitOfMeasure() != null).max(Comparator.comparing(Product::getUnitOfMeasure));
            return maxProduct.map(ProductProvider::generateInfoString).orElse("Нет никаких элементов в коллекции.");
        });
        availableFunctions.put("sum_of_manufacturer_cost", request -> {
            TreeSet<Product> treeSet = new TreeSet<>(collectionManager.elements);
            Optional<String> response = treeSet.stream()
                    .map(ProductProvider::generateInfoString).map(s -> s + "\n").reduce((a, b) -> a+b);
            return response.orElse("");
        });
        availableFunctions.put("remove_by_id", request -> {
            long ID = Long.parseLong(request.getParameters()[1]);
            if (collectionManager.remove(ID))
                return "Элемент (ID: " + ID + ") успешно удалён.";
            return "Нет элемента с таким ID.";
        });
        availableFunctions.put("show", request -> {
            if (request.getParameters().length < 2) {
                Optional<String> response = collectionManager.elements.stream()
                        .map(ProductProvider::generateInfoString).map(s -> s + "\n").reduce((a, b) -> a+b);
                return response.orElse("");
            }
            long ID = Long.parseLong(request.getParameters()[1]);
            Product product = collectionManager.productsByID.get(ID);
            if (product == null)
                return "Нет элемента с таким ID.";
            return ProductProvider.generateInfoString(product);
        });
        availableFunctions.put("update", request -> {
            long ID = Long.parseLong(request.getParameters()[1]);
            collectionManager.update(ID, request.getProduct());
            return "Элемент (ID: " + ID + ") успешно обновлён.";
        });
        availableFunctions.put("get_collection_manager_message", request -> collectionManagerMessage);
    }

    public Response handleRequest(Request request) {
        String response = availableFunctions.get(request.getRequestName()).apply(request);
        TreeSet<Product> treeSet = new TreeSet<>((workerA, workerB) -> {
            try {
                ByteArrayOutputStream byteArrayOutputStreamA = new ByteArrayOutputStream();
                ByteArrayOutputStream byteArrayOutputStreamB = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStreamA = new ObjectOutputStream(byteArrayOutputStreamA);
                ObjectOutputStream objectOutputStreamB = new ObjectOutputStream(byteArrayOutputStreamB);
                objectOutputStreamA.writeObject(workerA);
                objectOutputStreamB.writeObject(workerB);
                objectOutputStreamA.flush();
                objectOutputStreamB.flush();
                int sizeA = byteArrayOutputStreamA.toByteArray().length;
                int sizeB = byteArrayOutputStreamB.toByteArray().length;
                return sizeA-sizeB;
            } catch (IOException exception) {
                return 0;
            }
        });
        treeSet.addAll(collectionManager.elements);
        LinkedHashSet<Product> sortedElements = new LinkedHashSet<>(treeSet);
        return new Response(response, sortedElements);
    }

    public void forceSave() {
        try {
            collectionManager.save();
        } catch (InputException exception) {
            System.out.println(exception.getMessage());
            return;
        }
        System.out.println("Сохранил коллекцию.");
    }
}
