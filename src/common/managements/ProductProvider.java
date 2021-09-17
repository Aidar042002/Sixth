package common.managements;

import common.exceptions.*;
import common.parameters.*;
import common.parameters.Product;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ProductProvider {
    public static Product getProduct(){
        Product product = new Product();
        product.setName(FieldInputProviders.productNameInputProvider.provide());
        Coordinates coordinates = new Coordinates();
        coordinates.setX(FieldInputProviders.coordinatesXInputProvider.provide());
        coordinates.setY(FieldInputProviders.coordinatesYInputProvider.provide());
        product.setCoordinates(coordinates);
        product.setPrice(FieldInputProviders.priceInputProvider.provide());
        product.setManufactureCost(FieldInputProviders.manufacturerCostProvider.provide());
        product.setPartNumber(FieldInputProviders.partNumberProvider.provide());
        if (FieldInputProviders.unitOfMeasureConfirmationInputProvider.provide())
            product.setUnitOfMeasure(FieldInputProviders.unitOfMeasureInputProvider.provide());
        if (FieldInputProviders.organizationTypeConfirmationInputProvider.provide()) {
            Organization organization = new Organization();
            organization.setId(product.getId());
            organization.setName(FieldInputProviders.organizationNameInputProvider.provide());
            organization.setFullName(FieldInputProviders.organizationFullNameInputProvider.provide());
            if(FieldInputProviders.organizationTypeConfirmationInputProvider.provide())
                organization.setType(FieldInputProviders.organizationTypeInputProvider.provide());
            if (FieldInputProviders.addressConfirmationInputProvider.provide()) {
                Address address = new Address();
                address.setZipCode(FieldInputProviders.zipCodeInputProvider.provide());
                if(FieldInputProviders.locationConfirmationInputProvider.provide()){
                    Location location = new Location();
                    location.setX(FieldInputProviders.locationXInputProvider.provide());
                    location.setY(FieldInputProviders.locationYInputProvider.provide());
                    location.setZ(FieldInputProviders.locationZInputProvider.provide());
                    address.setLocation(location);
                }
            }
            product.setManufacturer(organization);
        }
        return product;
    }

    public static Product getProductCSV(String line) throws InputException{
        Product product = new Product();
        ArrayList<String> tokens = CSVBuilder.parse(line);
        product.setId(Long.parseLong(tokens.get(0)));
        product.setName(tokens.get(1));
        Coordinates coordinates = new Coordinates();
        coordinates.setX(FieldCheck.coordinatesXValidator.get(tokens.get(2)));
        coordinates.setY(FieldCheck.coordinatesYValidator.get(tokens.get(3)));
        product.setCreationDate(ZonedDateTime.of(LocalDateTime.parse(tokens.get(4),
                DateTimeFormatter.ofPattern("dd.MM.yyyy.HH.mm")), ZoneId.systemDefault()));
        /*creationDate = Date.from(LocalDateTime(tokens.get(4), DateTimeFormatter.ofPattern("dd.MM.yyyy.HH.mm"));*/
        product.setPrice(FieldCheck.priceValidator.get(tokens.get(5)));
        product.setManufactureCost(FieldCheck.manufacturerCostValidator.get(tokens.get(6)));
        product.setPartNumber(FieldCheck.partNumberValidator.get(tokens.get(7)));
        if (!tokens.get(8).equals(""))
            product.setUnitOfMeasure(FieldCheck.unitOfMeasureValidator.get(tokens.get(8)));
        if (!tokens.get(9).equals("")) {
            Organization manufacturer = new Organization();
            manufacturer.setId(Long.parseLong(tokens.get(9)));
            manufacturer.setName(FieldCheck.productNameValidator.get(tokens.get(10)));
            manufacturer.setFullName(FieldCheck.fullNameValidator.get(tokens.get(11)));
            if (!tokens.get(12).equals(""))
                manufacturer.setType(FieldCheck.organizationTypeValidator.get(tokens.get(12)));
            if (!tokens.get(13).equals("")) {
                Address address = new Address();
                address.setZipCode(tokens.get(13));
                if (!tokens.get(14).equals("")) {
                    Location location = new Location();
                    location.setX(FieldCheck.locationXValidator.get(tokens.get(14)));
                    location.setY(FieldCheck.locationYValidator.get(tokens.get(15)));
                    location.setZ(FieldCheck.locationZValidator.get(tokens.get(16)));
                }
            }
        }
        return product;
    }

    public static String toString(Product product) {
        CSVBuilder csvStringBuilder = new CSVBuilder();
        csvStringBuilder.append(String.valueOf(product.getId()));
        csvStringBuilder.append(product.getName());
        csvStringBuilder.append(String.valueOf(product.getCoordinates().getX()));
        csvStringBuilder.append(String.valueOf(product.getCoordinates().getY()));
        csvStringBuilder.append(DateTimeFormatter.ofPattern("dd.MM.yyyy.HH.mm").format(product.getCreationDate()));
        csvStringBuilder.append(String.valueOf(product.getPrice()));
        csvStringBuilder.append(String.valueOf(product.getManufactureCost()));
        csvStringBuilder.append(String.valueOf(product.getPartNumber()));
        csvStringBuilder.append(product.getUnitOfMeasure().name());
        if (product.getManufacturer() == null) {
            for (int i = 0; i < 8; i++)
                csvStringBuilder.append("");
        } else {
            csvStringBuilder.append(String.valueOf(product.getManufacturer().getId()));
            csvStringBuilder.append(product.getManufacturer().getName());
            csvStringBuilder.append(product.getManufacturer().getFullName());
            csvStringBuilder.append(product.getManufacturer().getType().name());
            if (product.getManufacturer().getPostalAddress() == null) {
                for (int i = 0; i < 4; i++)
                    csvStringBuilder.append("");
            } else {
                csvStringBuilder.append(product.getManufacturer().getPostalAddress().getZipCode());
                if(product.getManufacturer().getPostalAddress().getLocation() == null){
                    csvStringBuilder.append(String.valueOf(product.getManufacturer().getPostalAddress().getLocation().getX()));
                    csvStringBuilder.append(String.valueOf(product.getManufacturer().getPostalAddress().getLocation().getY()));
                    csvStringBuilder.append(String.valueOf(product.getManufacturer().getPostalAddress().getLocation().getZ()));
                }
            }
        }
        return csvStringBuilder.line;
    }
    public static String generateInfoString(Product product) {
        return "\nID: " + product.getId() +
                "\nИмя : " + product.getName() +
                "\nКоординаты:" +
                "\n\tX: " + product.getCoordinates().getX() +
                "\n\tY: " + product.getCoordinates().getY() +
                "\nВремя создания: " + DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy").format(product.getCreationDate()) + //?????
                "\nЦена: " + product.getPrice() +
                "\nСтоимость изготовления: " + product.getManufactureCost() +
                "\nНомер части: " + product.getPartNumber() +
                "\nЕдиница измерения: " + product.getUnitOfMeasure() +
                "\nПроизводитель: " + (product.getManufacturer() == null ? "null" :
                "\n\tId: " + product.getManufacturer().getId() +
                        "\n\tИмя: " + product.getManufacturer().getName() +
                        "\n\tПолное имя: " + product.getManufacturer().getFullName() +
                        "\n\tТип: " + product.getManufacturer().getType() +
                        "\n\tПочтовый адрес: " + (product.getManufacturer().getPostalAddress() == null ? "null" :
                        "\n\t\tИндекс: " + product.getManufacturer().getPostalAddress().getZipCode() +
                                "\n\tМесто расположения: " + (product.getManufacturer().getPostalAddress().getLocation() == null ?
                                "null" :
                                "\n\t\tX: " + product.getManufacturer().getPostalAddress().getLocation().getX() +
                                        "\n\t\tY: " + product.getManufacturer().getPostalAddress().getLocation().getY() +
                                        "\n\t\tZ: " + product.getManufacturer().getPostalAddress().getLocation().getZ()))) + "\n";
    }
}
