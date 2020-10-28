package Main;

public enum Messages {
    IAMBOB("I am bob"),
    IAMALICE("I am alice"),
    A("a"),
    B("b"),
    HASH("hash"),
    MESSAGE("message");

    private String text;

    Messages(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
