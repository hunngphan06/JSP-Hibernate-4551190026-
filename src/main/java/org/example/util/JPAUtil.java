package org.example.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Utility class để quản lý EntityManager và EntityManagerFactory
 * Sử dụng Singleton pattern để đảm bảo chỉ có một EntityManagerFactory instance
 */
public class JPAUtil {
    // EntityManagerFactory dùng để tạo EntityManager instances
    private static final EntityManagerFactory emFactory;
    
    // Static block sẽ chạy một lần khi class được load
    // Khởi tạo EntityManagerFactory với persistence unit "myPU"
    static {
        emFactory = Persistence.createEntityManagerFactory("myPU");
    }
    
    /**
     * Tạo và trả về một EntityManager mới
     * EntityManager dùng để tương tác với database
     */
    public static EntityManager getEntityManager() {
        return emFactory.createEntityManager();
    }
    
    /**
     * Đóng EntityManagerFactory khi không cần sử dụng nữa
     * Nên gọi method này khi shutdown ứng dụng
     */
    public static void close() {
        emFactory.close();
    }
} 