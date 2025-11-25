# 🚀 HƯỚNG DẪN CHẠY DỰ ÁN QPMess CHI TIẾT

## 📋 Mục lục
1. [Yêu cầu hệ thống](#yêu-cầu-hệ-thống)
2. [Cài đặt MongoDB](#cài-đặt-mongodb)
3. [Kiểm tra Java và JavaFX](#kiểm-tra-java-và-javafx)
4. [Compile dự án](#compile-dự-án)
5. [Chạy ứng dụng](#chạy-ứng-dụng)
6. [Khắc phục lỗi thường gặp](#khắc-phục-lỗi-thường-gặp)
7. [Test tính năng mới](#test-tính-năng-mới)

---

## 📦 Yêu cầu hệ thống

### ✅ Phần mềm cần thiết:
- **Java JDK 11+** (đang dùng JDK 21)
- **JavaFX SDK** (đã có trong folder `FX SDK`)
- **MongoDB** (Community Edition)
- **PowerShell** (Windows có sẵn)

### 📁 Cấu trúc thư mục cần có:
```
D:\LTM(3)\QPMess\QPMess\
├── Collections\           ← Package các model
├── SessionManager\        ← Quản lý session
├── network\              ← Network P2P
├── css\                  ← CSS files
├── Images\               ← Hình ảnh
├── FX SDK\              ← JavaFX SDK (QUAN TRỌNG!)
├── MongoDriver\         ← MongoDB driver JAR
├── Home.java            ← Màn hình đăng nhập
├── ChatApp.java         ← Màn hình chat chính
├── AccountSettingsUI.java ← Quản lý tài khoản (MỚI!)
├── build.ps1            ← Script compile
└── run.ps1              ← Script chạy
```

---

## 🗄️ Cài đặt MongoDB

### Bước 1: Kiểm tra MongoDB đã cài chưa
```powershell
# Mở PowerShell và chạy:
mongo --version
# hoặc
mongod --version
```

### Bước 2: Cài đặt MongoDB (nếu chưa có)
1. Download MongoDB Community Edition từ: https://www.mongodb.com/try/download/community
2. Chạy installer và làm theo hướng dẫn
3. Chọn "Install MongoDB as a Service" ✓

### Bước 3: Khởi động MongoDB Service
```powershell
# Mở PowerShell với quyền Administrator
net start MongoDB
```

### Bước 4: Kiểm tra MongoDB đang chạy
```powershell
# Kết nối vào MongoDB shell
mongo

# Nếu thành công, bạn sẽ thấy prompt:
# >

# Kiểm tra database ChatApp
use ChatApp
show collections

# Kết quả nên có:
# - User
# - Login
# - Message
# - Group
# ...
```

### Bước 5: Tạo dữ liệu test (nếu chưa có)
```javascript
// Trong MongoDB shell:
use ChatApp

// Tạo user test
db.User.insertOne({
  "user_id": 1,
  "name": "Test User",
  "profile_picture": "file:///D:/LTM(3)/QPMess/QPMess/Images/Contact1.jpeg",
  "bio": "Hello World",
  "preferred_language": "vi",
  "created_at": "2024-01-01T00:00:00",
  "email": "test@example.com",
  "phone": "0123456789"
})

// Tạo login test
db.Login.insertOne({
  "login_id": 1,
  "user_id": 1,
  "username": "test",
  "password": "123456"
})

// Verify
db.User.find()
db.Login.find()
```

---

## ☕ Kiểm tra Java và JavaFX

### Bước 1: Kiểm tra Java
```powershell
# Mở PowerShell
java -version

# Kết quả mong đợi:
# java version "21.x.x"
# hoặc version 11 trở lên
```

### Bước 2: Kiểm tra đường dẫn Java
```powershell
# Xem đường dẫn Java hiện tại
$env:JAVA_HOME

# Hoặc kiểm tra trong build.ps1
Get-Content .\build.ps1 | Select-String "javac"
```

**❗ Quan trọng:** Nếu đường dẫn Java trong `build.ps1` và `run.ps1` không đúng:

**Mở `build.ps1` và sửa dòng 5:**
```powershell
$javac = "C:\Program Files\Java\jdk-21\bin\javac.exe"
# Thay bằng đường dẫn Java của bạn
```

**Mở `run.ps1` và sửa dòng 5:**
```powershell
$java = "C:\Program Files\Java\jdk-21\bin\java.exe"
# Thay bằng đường dẫn Java của bạn
```

### Bước 3: Kiểm tra JavaFX SDK
```powershell
# Kiểm tra folder FX SDK có tồn tại
Test-Path "FX SDK\lib"

# Kết quả: True (nếu có)

# Kiểm tra các file JAR
Get-ChildItem "FX SDK\lib" -Filter *.jar
```

**Kết quả mong đợi:**
```
javafx.base.jar
javafx.controls.jar
javafx.fxml.jar
javafx.graphics.jar
javafx.media.jar
javafx.swing.jar
javafx.web.jar
```

---

## 🔨 Compile dự án

### Cách 1: Sử dụng script tự động (KHUYẾN NGHỊ)
```powershell
# Mở PowerShell tại folder QPMess
cd "D:\LTM(3)\QPMess\QPMess"

# Chạy build script
.\build.ps1
```

**Kết quả thành công:**
```
Compiling Java sources...
Compiling Collections ( X files )...
Compiling SessionManager ( X files )...
Compiling network ( X files )...
Compiling top-level classes...
Build finished.
```

### Cách 2: Compile thủ công (nếu script lỗi)

**Bước 1: Compile Collections**
```powershell
javac -cp ".;MongoDriver\mongo-java-driver-3.12.13.jar" `
  --module-path "FX SDK\lib" `
  --add-modules javafx.controls,javafx.fxml `
  Collections\*.java
```

**Bước 2: Compile SessionManager**
```powershell
javac -cp ".;MongoDriver\mongo-java-driver-3.12.13.jar" `
  --module-path "FX SDK\lib" `
  --add-modules javafx.controls,javafx.fxml `
  SessionManager\*.java
```

**Bước 3: Compile network**
```powershell
javac -cp ".;MongoDriver\mongo-java-driver-3.12.13.jar" `
  --module-path "FX SDK\lib" `
  --add-modules javafx.controls,javafx.fxml `
  network\*.java
```

**Bước 4: Compile UI classes**
```powershell
javac -cp ".;MongoDriver\mongo-java-driver-3.12.13.jar" `
  --module-path "FX SDK\lib" `
  --add-modules javafx.controls,javafx.fxml `
  Home.java ChatApp.java AccountSettingsUI.java `
  NewContactDialog.java PrivacySettingsUI.java UserRegistrationForm.java
```

### Kiểm tra compile thành công
```powershell
# Kiểm tra file .class đã được tạo
Get-ChildItem -Recurse -Filter *.class | Measure-Object

# Kết quả: Nên có nhiều files .class
```

---

## ▶️ Chạy ứng dụng

### Cách 1: Sử dụng script (KHUYẾN NGHỊ)
```powershell
# Chạy run script
.\run.ps1
```

### Cách 2: Chạy thủ công
```powershell
java -cp ".;MongoDriver\mongo-java-driver-3.12.13.jar" `
  --module-path "FX SDK\lib" `
  --add-modules javafx.controls,javafx.fxml `
  Home
```

### Kết quả mong đợi:
1. **Cửa sổ đăng nhập** xuất hiện
2. Title: "QPMess - Đăng nhập"
3. Form đăng nhập với 2 nút: "Đăng ký" và "Đăng nhập"

---

## 🎯 Test ứng dụng

### Test 1: Đăng nhập
1. Nhập username: `test`
2. Nhập password: `123456`
3. Click "Đăng nhập"
4. **Kết quả:** Màn hình chat chính xuất hiện

### Test 2: Giao diện chat
1. ✅ Kiểm tra User Info Box ở góc phải trên
   - Avatar hiển thị
   - Tên user hiển thị
   - Status "● Online" màu xanh
2. ✅ Kiểm tra left panel có button Account (icon settings)
3. ✅ Kiểm tra danh sách contacts

### Test 3: Gửi tin nhắn
1. Chọn một contact trong danh sách
2. Nhập tin nhắn trong text field dưới
3. Click "Gửi" hoặc nhấn Enter
4. **Kết quả:**
   - ✅ Tin nhắn của bạn hiển thị **BÊN PHẢI**
   - ✅ Background màu xanh đậm (#005c4b)
   - ✅ Có timestamp

### Test 4: Quản lý tài khoản (TÍNH NĂNG MỚI!)

**Cách 1: Click User Info Box**
1. Click vào avatar + tên ở góc phải trên
2. Dialog "Quản lý thông tin tài khoản" mở ra

**Cách 2: Click button Account**
1. Click button Account ở left panel (icon settings)
2. Dialog mở ra

**Test các chức năng:**
```
□ Thay đổi tên:
  - Sửa tên → "Lưu thay đổi"
  - Verify trong database:
    > use ChatApp
    > db.User.findOne({"user_id": 1})

□ Thay đổi email:
  - Nhập email mới → "Lưu thay đổi"
  - Verify trong database

□ Thay đổi phone:
  - Nhập số điện thoại → "Lưu thay đổi"
  - Verify trong database

□ Thay đổi avatar:
  - Click "Thay đổi ảnh đại diện"
  - Chọn file ảnh (PNG, JPG, JPEG, GIF)
  - Preview hiển thị ngay
  - "Lưu thay đổi"
  - Verify avatar cập nhật ở User Info Box

□ Đổi mật khẩu:
  ☑ Test validation:
    - Để trống mật khẩu hiện tại → Báo lỗi ✓
    - Nhập sai mật khẩu hiện tại → Báo lỗi ✓
    - Mật khẩu mới < 6 ký tự → Báo lỗi ✓
    - Confirm password không khớp → Báo lỗi ✓
  
  ☑ Test đổi thành công:
    - Nhập mật khẩu hiện tại: 123456
    - Nhập mật khẩu mới: newpass123
    - Confirm: newpass123
    - "Lưu thay đổi" → Thành công!
    - Đăng xuất
    - Đăng nhập lại với mật khẩu mới ✓
```

---

## 🔧 Khắc phục lỗi thường gặp

### ❌ Lỗi 1: MongoDB connection refused
**Nguyên nhân:** MongoDB service chưa chạy

**Giải pháp:**
```powershell
# Administrator PowerShell
net start MongoDB

# Kiểm tra
mongo
```

### ❌ Lỗi 2: JavaFX runtime components missing
**Nguyên nhân:** Đường dẫn JavaFX sai hoặc thiếu

**Giải pháp:**
```powershell
# Kiểm tra folder
Test-Path "FX SDK\lib"

# Nếu False, download JavaFX SDK:
# https://gluonhq.com/products/javafx/
# Extract vào folder "FX SDK"
```

### ❌ Lỗi 3: ClassNotFoundException: User
**Nguyên nhân:** Chưa compile hoặc compile không đúng

**Giải pháp:**
```powershell
# Clean và compile lại
Remove-Item -Recurse -Filter *.class
.\build.ps1
```

### ❌ Lỗi 4: Image not found
**Nguyên nhân:** Thiếu file ảnh trong folder Images

**Giải pháp:**
```powershell
# Kiểm tra Images folder
Get-ChildItem Images\

# Cần có:
# - Contact1.jpeg
# - settings.png
# - settings2.png
# - story.png
# - pro.png
# - linkeddevices.png
# - rep.png
```

### ❌ Lỗi 5: "javac is not recognized"
**Nguyên nhân:** Java chưa được thêm vào PATH hoặc đường dẫn sai

**Giải pháp:**
```powershell
# Option 1: Update PATH (Administrator)
$env:Path += ";C:\Program Files\Java\jdk-21\bin"

# Option 2: Sửa đường dẫn trong build.ps1
# Xem mục "Kiểm tra Java và JavaFX"
```

### ❌ Lỗi 6: User không có method getEmail()
**Nguyên nhân:** File User.java chưa được compile lại

**Giải pháp:**
```powershell
# Compile lại User.java
javac -cp ".;MongoDriver\mongo-java-driver-3.12.13.jar" `
  Collections\User.java

# Rồi compile lại AccountSettingsUI
javac -cp ".;MongoDriver\mongo-java-driver-3.12.13.jar" `
  --module-path "FX SDK\lib" `
  --add-modules javafx.controls,javafx.fxml `
  AccountSettingsUI.java
```

---

## 📊 Verify trong MongoDB

### Kiểm tra dữ liệu sau khi test:
```javascript
// Mở MongoDB shell
mongo

use ChatApp

// Xem User collection
db.User.findOne({"user_id": 1})

// Kết quả mong đợi:
{
  "_id": ObjectId("..."),
  "user_id": 1,
  "name": "Updated Name",        // ← Đã cập nhật
  "email": "newemail@test.com",  // ← Có email mới
  "phone": "0987654321",         // ← Có phone mới
  "profile_picture": "file:///...",
  "bio": "...",
  "preferred_language": "vi",
  "created_at": "..."
}

// Xem Login collection (nếu đã đổi password)
db.Login.findOne({"user_id": 1})

// Kết quả:
{
  "_id": ObjectId("..."),
  "login_id": 1,
  "user_id": 1,
  "username": "test",
  "password": "newpass123"  // ← Password mới
}
```

---

## 🎨 Các tính năng mới đã thêm

### 1. ✨ Tin nhắn dồn sang phải
- Tin nhắn gửi đi: **sát bên phải**
- Tin nhắn nhận được: **bên trái**

### 2. 🎭 User Info Box
- Avatar tròn với shadow effect
- Tên người dùng
- Status "● Online" màu xanh
- Click để mở Account Settings

### 3. ⚙️ Account Settings UI
- Thay đổi thông tin cá nhân
- Đổi mật khẩu với validation
- Upload avatar
- Modern UI với dark theme

### 4. 🎨 CSS cải tiến
- Hover effects
- Smooth animations
- Tooltip styling
- User info box styling

---

## 🚀 Quick Start (TL;DR)

```powershell
# 1. Start MongoDB
net start MongoDB

# 2. Compile
cd "D:\LTM(3)\QPMess\QPMess"
.\build.ps1

# 3. Run
.\run.ps1

# 4. Login
Username: test
Password: 123456

# 5. Test tính năng mới
- Click vào avatar ở góc phải trên
- Thử thay đổi thông tin
- Gửi tin nhắn và kiểm tra hiển thị
```

---

## 📞 Liên hệ & Hỗ trợ

Nếu gặp vấn đề:
1. Check console output
2. Check MongoDB logs
3. Re-compile từ đầu
4. Check file tài liệu khác:
   - `THAY_DOI_GIAO_DIEN.md` - Chi tiết thay đổi
   - `COMPILE_AND_RUN.md` - Troubleshooting
   - `TOM_TAT_THAY_DOI.txt` - Tóm tắt ngắn

---

**✅ Done! Chúc bạn code vui vẻ! 🎉**

