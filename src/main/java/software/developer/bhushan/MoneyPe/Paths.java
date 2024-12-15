package software.developer.bhushan.MoneyPe;

import java.nio.file.FileSystems;


public class Paths {
    public static final String RESOURCES_FOLDER_PATH = getAbsolutePath()+"/src/main/resources/database/";

    public static void main(String[] args) {
        System.out.println(getAbsolutePath());
    }

    public static String getAbsolutePath(){
        return FileSystems.getDefault().getPath("").toAbsolutePath().toString();
    }
}
