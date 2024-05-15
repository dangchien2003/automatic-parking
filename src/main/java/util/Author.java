package util;

public class Author {
    private String Authorization;
    public  Author(String Authorization) {
        this.Authorization = Authorization;
    }

    public String getAuthor() {
        // Kiểm tra nếu tiêu đề Authorization tồn tại
        if (Authorization != null && Authorization.startsWith("Bearer ")) {
            // Lấy token từ tiêu đề Authorization
            String dataAuthorization = Authorization.substring(7);
            return dataAuthorization;
        } else {
            return null;
        }
    }

}
