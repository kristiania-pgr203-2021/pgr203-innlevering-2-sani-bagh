package no.kristiania.http;

public class Product {

    private String name;
    private String category;

    public Product () {
        this.name = name;
    }

    public Product(String name, String category) {
        this.name =name;
        this.category=category;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
