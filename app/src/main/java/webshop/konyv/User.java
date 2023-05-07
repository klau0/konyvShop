package webshop.konyv;

public class User {
    private String id;
    private int carted_num;

    public User(String id, int carted_num) {
        this.id = id;
        this.carted_num = carted_num;
    }

    public String getId() {
        return id;
    }

    public int getCarted_num() {
        return carted_num;
    }
}
