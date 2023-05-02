package webshop.konyv;

public class Book {
    private String id;
    private String title;
    private String author;
    private String info;
    private String price;
    private float ratedInfo;
    private int imageResource;
    private int cartedCount;

    public Book(){}
    public Book(String title, String author, String info, String price, float ratedInfo, int imageResource, int cartedCount) {
        this.title = title;
        this.author = author;
        this.info = info;
        this.price = price;
        this.ratedInfo = ratedInfo;
        this.imageResource = imageResource;
        this.cartedCount = cartedCount;
    }

    public String getTitle() {return title;}
    public String getAuthor(){return author;}
    public String getInfo() {return info;}
    public String getPrice() {return price;}
    public float getRatedInfo() {return ratedInfo;}
    public int getImageResource() {return imageResource;}
    public int getCartedCount() {return cartedCount;}
    public String _getId(){
        return id;
    }
    public void setId(String id){
        this.id = id;
    }
}
