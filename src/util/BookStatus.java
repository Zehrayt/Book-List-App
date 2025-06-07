
package util;

public enum BookStatus {
    UNREAD("To Read"),
    READING("Reading"),
    READ("Read");

    private final String displayName;

    BookStatus(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
