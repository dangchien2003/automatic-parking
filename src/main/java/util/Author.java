package util;

public class Author {

    public static String getAuthor(String Authorization) {
        if (Authorization != null && Authorization.startsWith("Bearer ")) {
            String dataAuthorization = Authorization.substring(7);
            return dataAuthorization;
        } else {
            return "";
        }
    }

}
