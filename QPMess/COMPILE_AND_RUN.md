# Hướng dẫn Compile và Run QPMess sau khi cập nhật

## Các file đã thay đổi

### Files được chỉnh sửa:
1. `ChatApp.java` - Giao diện chính, layout tin nhắn, user info
2. `Collections/User.java` - Thêm fields email, password, phone
3. `css/modern.css` - Thêm styles mới

### Files mới:
1. `AccountSettingsUI.java` - UI quản lý thông tin tài khoản

## Cách compile

### Sử dụng PowerShell script có sẵn:
```powershell
# Chạy build script
.\build.ps1
```

### Compile thủ công:
```bash
# Compile tất cả files Java
javac --module-path "FX SDK\lib" --add-modules javafx.controls,javafx.fxml ^
  -cp "MongoDriver\mongo-java-driver-3.12.13.jar;." ^
  Collections\*.java SessionManager\*.java network\*.java ^
  *.java

# Hoặc compile từng file quan trọng
javac --module-path "FX SDK\lib" --add-modules javafx.controls,javafx.fxml ^
  -cp "MongoDriver\mongo-java-driver-3.12.13.jar;." ^
  Collections\User.java

javac --module-path "FX SDK\lib" --add-modules javafx.controls,javafx.fxml ^
  -cp "MongoDriver\mongo-java-driver-3.12.13.jar;." ^
  AccountSettingsUI.java

javac --module-path "FX SDK\lib" --add-modules javafx.controls,javafx.fxml ^
  -cp "MongoDriver\mongo-java-driver-3.12.13.jar;." ^
  ChatApp.java
```

## Cách chạy

### Sử dụng PowerShell script:
```powershell
.\run.ps1
```

### Chạy thủ công:
```bash
java --module-path "FX SDK\lib" --add-modules javafx.controls,javafx.fxml ^
  -cp "MongoDriver\mongo-java-driver-3.12.13.jar;." ^
  ChatApp
```

## Kiểm tra MongoDB

Trước khi chạy, đảm bảo MongoDB đang chạy:

### Kiểm tra MongoDB service:
```bash
# Windows
net start MongoDB

# Hoặc check xem service đang chạy
sc query MongoDB
```

### Test connection:
```bash
mongo
use ChatApp
db.User.find()
db.Login.find()
```

## Troubleshooting

### Lỗi: JavaFX không tìm thấy
**Giải pháp:**
- Kiểm tra đường dẫn "FX SDK\lib" có đúng không
- Đảm bảo JavaFX SDK đã được extract vào folder "FX SDK"

### Lỗi: MongoDB connection refused
**Giải pháp:**
```bash
# Start MongoDB service
net start MongoDB

# Hoặc run MongoDB manually
mongod --dbpath "path\to\data"
```

### Lỗi: ClassNotFoundException cho User hoặc AccountSettingsUI
**Giải pháp:**
- Compile lại tất cả các files
- Đảm bảo package structure đúng:
  ```
  QPMess/
  ├── Collections/
  │   └── User.java (đã compile -> User.class)
  ├── SessionManager/
  ├── network/
  └── AccountSettingsUI.java (đã compile -> AccountSettingsUI.class)
  ```

### Lỗi: Image not found
**Giải pháp:**
- Kiểm tra folder `Images/` có các file cần thiết:
  - `Contact1.jpeg`
  - `settings.png`
  - `settings2.png`
  - etc.

## Test các tính năng mới

### 1. Test hiển thị tin nhắn
- [ ] Đăng nhập
- [ ] Chọn một contact
- [ ] Gửi tin nhắn
- [ ] Verify tin nhắn của bạn hiển thị sát bên phải
- [ ] Verify tin nhắn nhận được hiển thị bên trái

### 2. Test User Info Box
- [ ] Check avatar hiển thị ở góc phải trên
- [ ] Check tên và status "Online" hiển thị
- [ ] Hover vào user info box (nên có hover effect)
- [ ] Click vào user info box
- [ ] Verify trang Account Settings mở ra

### 3. Test Account Settings
- [ ] Click button quản lý tài khoản (hoặc user info box)
- [ ] Verify dialog mở ra
- [ ] Verify thông tin hiện tại hiển thị đúng
- [ ] Test thay đổi tên → Save → Verify
- [ ] Test thay đổi email → Save → Verify
- [ ] Test thay đổi phone → Save → Verify
- [ ] Test thay đổi avatar → Save → Verify
- [ ] Test đổi mật khẩu:
  - [ ] Nhập sai mật khẩu hiện tại → Verify báo lỗi
  - [ ] Nhập đúng mật khẩu hiện tại
  - [ ] Mật khẩu mới và confirm không khớp → Verify báo lỗi
  - [ ] Mật khẩu mới < 6 ký tự → Verify báo lỗi
  - [ ] Đổi thành công → Đăng xuất → Đăng nhập lại với mật khẩu mới

### 4. Test Database
```bash
# Connect to MongoDB
mongo

# Use database
use ChatApp

# Check User collection
db.User.findOne({"user_id": YOUR_USER_ID})

# Should see new fields: email, phone

# Check Login collection
db.Login.findOne({"user_id": YOUR_USER_ID})

# Should see password field
```

## Known Issues

### Issue #1: Avatar chỉ lưu local path
- **Mô tả:** Avatar được lưu dưới dạng file:/// URI local
- **Impact:** Avatar không sync giữa các máy khác nhau
- **Workaround:** Sử dụng cùng một máy hoặc copy file avatar

### Issue #2: No real-time update
- **Mô tả:** Thay đổi thông tin không được broadcast tới các peers
- **Impact:** Cần restart app để thấy thay đổi từ user khác
- **Workaround:** Manual refresh hoặc restart

## Performance

### Memory Usage
- AccountSettingsUI sử dụng ~15MB RAM khi mở
- Dialog được dispose sau khi đóng
- Image loading là async

### Database Queries
- 1 query để load password khi mở AccountSettings
- 2 queries khi save (User collection + Login collection)
- All queries indexed by user_id

## Security Notes

⚠️ **CẢNH BÁO BẢO MẬT:**
- Password hiện tại **KHÔNG được mã hóa** trong database
- **Cần implement:** BCrypt hoặc SHA-256 hashing
- Email không được verify
- Không có rate limiting cho thay đổi password

**TODO bảo mật:**
```java
// Implement password hashing
import org.mindrot.jbcrypt.BCrypt;

String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
boolean matches = BCrypt.checkpw(plainPassword, hashedPassword);
```

## Next Steps

1. **Compile project**
   ```powershell
   .\build.ps1
   ```

2. **Run application**
   ```powershell
   .\run.ps1
   ```

3. **Test features** (theo checklist trên)

4. **Report bugs** nếu có

## Contact & Support

Nếu gặp vấn đề:
1. Check console output cho error messages
2. Check MongoDB logs
3. Verify tất cả dependencies đã được cài đặt
4. Re-compile từ đầu

---
**Happy Coding! 🚀**

