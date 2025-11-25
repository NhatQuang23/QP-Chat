# Hướng Dẫn Chạy Dự Án QPMess

## 📋 Yêu Cầu Hệ Thống

### 1. Java Development Kit (JDK)
- **JDK 21** hoặc mới hơn (bắt buộc vì JavaFX SDK đi kèm được biên dịch với version 61)
- Kiểm tra phiên bản: `java -version`
- Tải về: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) hoặc [OpenJDK](https://adoptium.net/)

### 2. MongoDB
- **MongoDB Community Server** phiên bản mới nhất
- Tải về: [MongoDB Download](https://www.mongodb.com/try/download/community)
- MongoDB phải chạy trên `localhost:27017`

### 3. Hệ Điều Hành
- Windows 10/11 (PowerShell)
- Hoặc có thể chạy trên Linux/Mac với script tương ứng

---

## 🚀 Các Bước Chạy Dự Án

### Bước 1: Cài Đặt MongoDB

1. **Tải và cài đặt MongoDB Community Server**
   - Tải từ trang chính thức MongoDB
   - Chạy installer và chọn "Complete" installation
   - Chọn "Install MongoDB as a Service"
   - Đảm bảo MongoDB chạy trên port mặc định: **27017**

2. **Kiểm tra MongoDB đang chạy**
   ```powershell
   # Mở PowerShell và chạy:
   Get-Service MongoDB
   ```
   - Nếu thấy status "Running" → MongoDB đã sẵn sàng
   - Nếu chưa chạy, khởi động bằng:
     ```powershell
     Start-Service MongoDB
     ```

3. **Tạo Database và Collections**
   - Mở MongoDB Compass hoặc MongoDB Shell
   - Tạo database tên: `ChatApp`
   - Tạo các collections sau:
     - `User`
     - `Login`
     - `User_Contacts`
     - `Message`
     - `Group` (nếu cần)
     - `User_Groups` (nếu cần)

### Bước 2: Chuẩn Bị Dữ Liệu Mẫu

#### 2.1. Tạo User Mẫu

Mở MongoDB Compass hoặc MongoDB Shell và chèn dữ liệu mẫu:

```javascript
// Kết nối đến database ChatApp
use ChatApp

// Tạo User 1
db.User.insertOne({
  user_id: 1,
  name: "Nguyễn Văn A",
  profile_picture: "Images/Contact1.jpeg",
  bio: "Xin chào!",
  preferred_language: "vi",
  created_at: "2024-01-01 10:00:00"
})

// Tạo Login cho User 1
db.Login.insertOne({
  login_id: 1,
  user_id: 1,
  username: "user1",
  password: "password1"
})

// Tạo User 2
db.User.insertOne({
  user_id: 2,
  name: "Trần Thị B",
  profile_picture: "Images/Contact2.jpeg",
  bio: "Hello!",
  preferred_language: "vi",
  created_at: "2024-01-01 11:00:00"
})

// Tạo Login cho User 2
db.Login.insertOne({
  login_id: 2,
  user_id: 2,
  username: "user2",
  password: "password2"
})

// Tạo User_Contacts (liên hệ giữa User 1 và User 2)
db.User_Contacts.insertOne({
  user_contact_id: 1,
  user_id: 1,
  contact_id: 2,
  contact_date: "2024-01-01 12:00:00"
})

db.User_Contacts.insertOne({
  user_contact_id: 2,
  user_id: 2,
  contact_id: 1,
  contact_date: "2024-01-01 12:00:00"
})
```

#### 2.2. Hoặc Import từ JSON (Nếu có)

Nếu bạn có file JSON trong thư mục `JSON/`, có thể import:
```powershell
# Sử dụng mongoimport (nếu có)
mongoimport --db ChatApp --collection User --file JSON/User.json --jsonArray
```

### Bước 3: Cấu Hình Đường Dẫn Java

1. **Kiểm tra đường dẫn Java trong script**
   - Mở file `build.ps1` và `run.ps1`
   - Kiểm tra dòng:
     ```powershell
     $javac = "C:\Program Files\Java\jdk-21\bin\javac.exe"
     $java = "C:\Program Files\Java\jdk-21\bin\java.exe"
     ```
   - **Nếu Java của bạn ở đường dẫn khác**, sửa lại cho đúng
   - Hoặc thêm Java vào PATH environment variable

2. **Kiểm tra Java đã cài đặt**
   ```powershell
   java -version
   javac -version
   ```

### Bước 4: Compile Dự Án

1. **Mở PowerShell** (Run as Administrator nếu cần)

2. **Di chuyển đến thư mục dự án**
   ```powershell
   cd "D:\Tran Dang Bao Phuc\Lap trinh mang\QPMess"
   ```

3. **Chạy script build**
   ```powershell
   .\build.ps1
   ```

   Hoặc compile thủ công:
   ```powershell
   & "C:\Program Files\Java\jdk-21\bin\javac.exe" `
      -cp ".;MongoDriver\mongo-java-driver-3.12.13.jar" `
      --module-path "FX SDK\lib" `
      --add-modules javafx.controls,javafx.fxml `
      Collections\*.java SessionManager\*.java Views\*.java network\*.java `
      ChatApp.java Home.java NewContactDialog.java PrivacySettingsUI.java UserRegistrationForm.java
   ```

4. **Kiểm tra kết quả**
   - Nếu thành công, bạn sẽ thấy: `Build finished.`
   - Các file `.class` sẽ được tạo trong các thư mục tương ứng

### Bước 5: Chạy Ứng Dụng

#### Cách 1: Sử dụng Script (Khuyên dùng)

```powershell
.\run.ps1
```

#### Cách 2: Chạy Thủ Công

```powershell
& "C:\Program Files\Java\jdk-21\bin\java.exe" `
   -cp ".;MongoDriver\mongo-java-driver-3.12.13.jar" `
   --module-path "FX SDK\lib" `
   --add-modules javafx.controls,javafx.fxml `
   Home
```

#### Cách 3: Chạy Trực Tiếp từ IDE

1. Mở project trong IntelliJ IDEA hoặc Eclipse
2. Set JDK 21 trong project settings
3. Add JavaFX SDK vào module path
4. Run `Home.java` (main class)

---

## 🎯 Sử Dụng Ứng Dụng

### 1. Đăng Nhập

1. **Mở ứng dụng** → Giao diện đăng nhập hiện ra
2. **Nhập thông tin**:
   - Tên đăng nhập: `user1` (hoặc `user2`)
   - Mật khẩu: `password1` (hoặc `password2`)
3. **Nhấn "Đăng nhập"** hoặc nhấn Enter

### 2. Thiết Lập P2P (Quan Trọng!)

Để chat P2P hoạt động, bạn cần:

#### Trên Máy 1 (User 1):
1. Đăng nhập với `user1`
2. Chọn contact "Trần Thị B" từ danh sách
3. Trong thanh "TCP P2P":
   - **Địa chỉ IP**: `127.0.0.1` (nếu cùng máy) hoặc IP của máy 2
   - **Cổng**: `6002` (vì user_id = 2 → port = 6000 + 2)
4. Nhấn **"Lưu Peer"**
5. Ứng dụng sẽ lắng nghe trên port `6001` (vì user_id = 1)

#### Trên Máy 2 (User 2):
1. Đăng nhập với `user2` (trong cửa sổ PowerShell khác)
2. Chọn contact "Nguyễn Văn A"
3. Trong thanh "TCP P2P":
   - **Địa chỉ IP**: `127.0.0.1` (nếu cùng máy) hoặc IP của máy 1
   - **Cổng**: `6001` (vì user_id = 1 → port = 6000 + 1)
4. Nhấn **"Lưu Peer"**
5. Ứng dụng sẽ lắng nghe trên port `6002`

### 3. Gửi Tin Nhắn P2P

1. **Chọn contact** từ danh sách bên trái
2. **Nhập tin nhắn** vào ô input ở dưới
3. **Nhấn Enter** hoặc nút **"Gửi"**
4. Tin nhắn sẽ:
   - Được gửi qua TCP đến peer
   - Lưu vào MongoDB
   - Hiển thị trong chat area

### 4. Nhận Tin Nhắn

- Khi peer gửi tin nhắn, ứng dụng sẽ tự động:
  - Nhận qua TCP socket
  - Lưu vào MongoDB
  - Hiển thị trong chat nếu đang mở conversation đó

---

## 🔧 Xử Lý Lỗi Thường Gặp

### Lỗi 1: "javac not found"
**Nguyên nhân**: Đường dẫn Java không đúng

**Giải pháp**:
1. Kiểm tra Java đã cài: `java -version`
2. Tìm đường dẫn Java: `where java`
3. Sửa đường dẫn trong `build.ps1` và `run.ps1`

### Lỗi 2: "MongoDB connection failed"
**Nguyên nhân**: MongoDB chưa chạy hoặc sai port

**Giải pháp**:
1. Kiểm tra MongoDB service: `Get-Service MongoDB`
2. Khởi động MongoDB: `Start-Service MongoDB`
3. Kiểm tra port: MongoDB phải chạy trên `localhost:27017`

### Lỗi 3: "Port already in use"
**Nguyên nhân**: Port đã được sử dụng bởi instance khác

**Giải pháp**:
1. Đóng các instance ứng dụng đang chạy
2. Hoặc thay đổi BASE_PORT trong `TcpPeerService.java`

### Lỗi 4: "Peer unreachable"
**Nguyên nhân**: 
- Peer chưa chạy
- Sai địa chỉ IP hoặc port
- Firewall chặn kết nối

**Giải pháp**:
1. Đảm bảo cả 2 peer đều đang chạy
2. Kiểm tra địa chỉ IP và port đúng
3. Tắt firewall tạm thời hoặc cho phép Java qua firewall

### Lỗi 5: "ClassNotFoundException"
**Nguyên nhân**: Thiếu dependencies hoặc classpath sai

**Giải pháp**:
1. Đảm bảo đã compile tất cả file `.java`
2. Kiểm tra `MongoDriver\mongo-java-driver-3.12.13.jar` tồn tại
3. Kiểm tra `FX SDK\lib` có đầy đủ JavaFX jars

---

## 📝 Lưu Ý Quan Trọng

### 1. Chạy Nhiều Instance
- Để test P2P, bạn cần chạy **ít nhất 2 instance** của ứng dụng
- Mỗi instance đăng nhập với user khác nhau
- Mỗi user sẽ lắng nghe trên port khác nhau (6000 + user_id)

### 2. Firewall
- Windows Firewall có thể chặn kết nối TCP
- Cho phép Java qua firewall hoặc tắt tạm thời khi test

### 3. Network
- **Cùng máy**: Dùng `127.0.0.1`
- **Khác máy trong LAN**: Dùng IP LAN (ví dụ: `192.168.1.100`)
- **Internet**: Cần NAT traversal (không hỗ trợ trong phiên bản này)

### 4. MongoDB
- Đảm bảo MongoDB luôn chạy khi sử dụng ứng dụng
- Database `ChatApp` phải tồn tại
- Collections phải có đúng tên và structure

---

## 🎨 Tính Năng Chính

### ✅ Đã Hoàn Thành
- ✅ Đăng nhập/Đăng ký
- ✅ Chat P2P qua TCP
- ✅ Lưu tin nhắn vào MongoDB
- ✅ Tìm kiếm liên hệ
- ✅ Giao diện hiện đại
- ✅ Timestamps cho tin nhắn
- ✅ Connection status indicators

### 🔄 Có Thể Phát Triển Thêm
- Online/Offline status
- Gửi file qua P2P
- End-to-end encryption
- Group chat P2P
- Voice/Video call

---

## 📞 Hỗ Trợ

Nếu gặp vấn đề, kiểm tra:
1. ✅ Java 21 đã cài đặt
2. ✅ MongoDB đang chạy
3. ✅ Database và collections đã tạo
4. ✅ Dữ liệu mẫu đã import
5. ✅ Đã compile thành công
6. ✅ Firewall không chặn

**Chúc bạn chạy dự án thành công! 🎉**


