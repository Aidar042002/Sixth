package common.server;

import common.exceptions.FileDoesNotExistException;
import common.exceptions.InputException;
import common.managements.FileControl;
import common.managements.ProductProvider;
import common.parameters.Organization;
import common.parameters.Product;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.function.Consumer;

public class CollectionManager {
    public final LinkedHashSet<Product> elements = new LinkedHashSet<>();
    public final HashMap<Long, Product> productsByID = new HashMap<>();
    //
    public final HashMap<Long, Organization> organizationByID = new HashMap<>();
    //
    private final String filePath;//?
    private LocalDateTime initializationDate;//?
    private long nextID;

    public CollectionManager(String filePath, Consumer<String> sendMessage) {
        this.filePath = filePath;
        boolean createNew = false;
        try {
            String[] lines = FileControl.readFromFile(filePath).split("\\r?\\n");
            String[] meta = lines[0].split(",");
            initializationDate = LocalDateTime.parse(meta[0], DateTimeFormatter.ofPattern("dd.MM.yyyy.HH.mm"));
            nextID = Long.parseLong(meta[1]);
            for (int i = 1; i < lines.length; i++) {
                Product product;
                try {
                    product = ProductProvider.getProductCSV(lines[i]);
                } catch (InputException exception) {
                    sendMessage.accept("При загрузке возникла ошибка: " + exception.getMessage());
                    continue;
                }
                elements.add(product);
                productsByID.put(product.getId(), product);
            }
        } catch (FileDoesNotExistException exception) {
            sendMessage.accept(" Файла не было, поэтому вот новая коллекция.");
            createNew = true;
        } catch (InputException exception) {
            sendMessage.accept(exception.getMessage());
            createNew = true;
        } catch (RuntimeException exception) {
            exception.printStackTrace();
            sendMessage.accept("\nФайл нет, новая коллекция  - пустая");
            createNew = true;
        }
        if (createNew) {
            initializationDate = LocalDateTime.now();
            clear();
        }
    }


    public String getInfo() {
        return "Тип коллекции: LinkedHashSet" +
                "\nДата инициализации: " + DateTimeFormatter.ofPattern("hh:mm dd.MM.yyyy").format(initializationDate) +
                "\nКоличество элементов: " + elements.size() +
                "\nЗагружена из файла: " + filePath +
                "\nСледующий элемент будет иметь ID " + nextID;
    }

    public long ElementAdding() {
        while (productsByID.containsKey(nextID))
            nextID++;
        Product product = new Product();
        product.setId(nextID);
        product.setCreationDate(ZonedDateTime.now());
        elements.add(product);
        productsByID.put(nextID, product);
        //initiateElementUpdatingProcedure(nextID);
        return nextID++;
    }



    public boolean removeElement(long ID) {
        Product product = productsByID.get(ID);
        if (product == null)
            return false;
        elements.remove(product);
        productsByID.remove(ID);
        if (ID == nextID-1)
            nextID--; // позволяет повторно использовать освободившиеся ID
        return true;
    }

    public void clear() {
        elements.clear();
        productsByID.clear();
        nextID = 1;
    }

    public long add(Product product) {
        while (productsByID.containsKey(nextID))
            nextID++;
        product.setId(nextID);
        product.setCreationDate(ZonedDateTime.now());
        elements.add(product);
        productsByID.put(nextID, product);
        return nextID++;
    }

    public void update(long ID, Product product) {
        Product oldProduct = productsByID.get(ID);
        ZonedDateTime creationDate = ZonedDateTime.now();
        if (oldProduct != null) {
            creationDate = oldProduct.getCreationDate();
            elements.remove(oldProduct);
        }
        product.setId(ID);
        product.setCreationDate(creationDate);
        elements.add(product);
        productsByID.put(ID, product);
    }

    public boolean remove(long ID) {
        Product product = productsByID.get(ID);
        if (product == null)
            return false;
        elements.remove(product);
        productsByID.remove(ID);
        if (ID == nextID-1)
            nextID--;
        return true;
    }

    public void save() throws InputException {
        StringBuilder csv = new StringBuilder();
        csv.append(DateTimeFormatter.ofPattern("dd.MM.yyyy.HH.mm").format(initializationDate));
        csv.append(",").append(nextID).append(",,,,,,,,,,,,\n");
        for (Product product : elements)
            csv.append(ProductProvider.toString(product)).append("\n");
        FileControl.saveToFile(filePath, csv.toString().trim());
    }

    public void saveToFile() throws InputException {
        StringBuilder csv = new StringBuilder();
        csv.append(DateTimeFormatter.ofPattern("dd.MM.yyyy.HH.mm").format(initializationDate));
        csv.append(",").append(nextID).append(",,,,,,,,,,,,\n"); // Запятых ровно столько, чтобы первая строчка имела с остальными одинаковое количество столбцов
        for (Product product : elements)
            csv.append(product.toString()).append("\n");
        FileControl.saveToFile(filePath, csv.toString().trim());
    }
}
