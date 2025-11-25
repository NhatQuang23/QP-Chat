package database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

/**
 * Class quản lý kết nối MongoDB Atlas
 * Singleton pattern để đảm bảo chỉ có một kết nối duy nhất
 */
public class DatabaseConnection {
    
    // MongoDB Atlas connection string
    private static final String CONNECTION_STRING = "mongodb+srv://pnnquang:lsSUyrYCu9kFYOel@cluster0.sglllmh.mongodb.net/?appName=Cluster0";
    private static final String DATABASE_NAME = "ChatApp";
    
    // Singleton instance
    private static DatabaseConnection instance;
    private MongoClient mongoClient;
    private MongoDatabase database;
    
    // Private constructor để implement Singleton
    private DatabaseConnection() {
        try {
            // Tạo MongoClientURI từ connection string
            MongoClientURI uri = new MongoClientURI(CONNECTION_STRING);
            
            // Khởi tạo MongoClient
            this.mongoClient = new MongoClient(uri);
            
            // Lấy database
            this.database = mongoClient.getDatabase(DATABASE_NAME);
            
            System.out.println("✓ Đã kết nối thành công tới MongoDB Atlas!");
            
        } catch (Exception e) {
            System.err.println("✗ Lỗi kết nối MongoDB Atlas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Lấy instance của DatabaseConnection (Singleton)
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    /**
     * Lấy MongoDatabase instance
     */
    public MongoDatabase getDatabase() {
        return database;
    }
    
    /**
     * Lấy MongoClient instance
     */
    public MongoClient getMongoClient() {
        return mongoClient;
    }
    
    /**
     * Đóng kết nối (nên gọi khi tắt ứng dụng)
     */
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("✓ Đã đóng kết nối MongoDB Atlas");
        }
    }
    
    /**
     * Kiểm tra kết nối
     */
    public boolean isConnected() {
        try {
            if (database != null) {
                // Thử ping database
                database.runCommand(new org.bson.Document("ping", 1));
                return true;
            }
        } catch (Exception e) {
            System.err.println("Lỗi kiểm tra kết nối: " + e.getMessage());
        }
        return false;
    }
}

