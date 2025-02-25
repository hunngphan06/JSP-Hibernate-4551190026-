package org.example;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

public class Main {
    public static void main(String[] args) {
        try {

            // Khởi tạo Tomcat Server
            Tomcat tomcat = new Tomcat();
            tomcat.setPort(8080);

            // Thiết lập base directory
            String baseDir = new File("tomcat").getAbsolutePath();
            tomcat.setBaseDir(baseDir);

            // Thiết lập thư mục webapps
            String webappDirLocation = new File("src/main/webapp").getAbsolutePath();
            
            // Tạo context với path rỗng "/"
            Context context = tomcat.addWebapp("", webappDirLocation);
            
            // Thiết lập để tránh lỗi access restriction
            context.setReloadable(true);
            
            // Connector configuration
            tomcat.getConnector(); // Quan trọng: Khởi tạo connector

            // Khởi động server
            tomcat.start();
            System.out.println("=== Server Information ===");
            System.out.println("Server started at http://localhost:8080");
            System.out.println("Web application directory: " + webappDirLocation);
            System.out.println("Base directory: " + baseDir);
            System.out.println("=========================");
            
            // Giữ server chạy
            tomcat.getServer().await();
            
        } catch (Exception e) {
            System.err.println("Server failed to start. Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}