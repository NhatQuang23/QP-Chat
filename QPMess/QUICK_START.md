# Quick Start Guide - QPMess

## ⚡ Chạy Nhanh (5 Phút)

### 1. Kiểm Tra Yêu Cầu
```powershell
# Kiểm tra Java
java -version  # Phải là JDK 21+

# Kiểm tra MongoDB
Get-Service MongoDB  # Phải là "Running"
```

### 2. Tạo Database và User Mẫu

Mở MongoDB Compass hoặc MongoDB Shell và chạy:

```javascript
use ChatApp

// User 1
db.User.insertOne({user_id: 1, name: "User 1", profile_picture: "Images/Contact1.jpeg", bio: "Hello", preferred_language: "vi", created_at: "2024-01-01 10:00:00"})
db.Login.insertOne({login_id: 1, user_id: 1, username: "user1", password: "pass1"})

// User 2  
db.User.insertOne({user_id: 2, name: "User 2", profile_picture: "Images/Contact2.jpeg", bio: "Hi", preferred_language: "vi", created_at: "2024-01-01 11:00:00"})
db.Login.insertOne({login_id: 2, user_id: 2, username: "user2", password: "pass2"})

// Contacts
db.User_Contacts.insertOne({user_contact_id: 1, user_id: 1, contact_id: 2, contact_date: "2024-01-01 12:00:00"})
db.User_Contacts.insertOne({user_contact_id: 2, user_id: 2, contact_id: 1, contact_date: "2024-01-01 12:00:00"})
```

### 3. Compile và Chạy

```powershell
# Di chuyển đến thư mục dự án
cd "D:\Tran Dang Bao Phuc\Lap trinh mang\QPMess"

# Compile
.\build.ps1

# Chạy (mở 2 cửa sổ PowerShell)
.\run.ps1
```

### 4. Sử Dụng

**Cửa sổ 1:**
- Đăng nhập: `user1` / `pass1`
- Chọn "User 2"
- TCP P2P: IP=`127.0.0.1`, Port=`6002`
- Nhấn "Lưu Peer"

**Cửa sổ 2:**
- Đăng nhập: `user2` / `pass2`
- Chọn "User 1"  
- TCP P2P: IP=`127.0.0.1`, Port=`6001`
- Nhấn "Lưu Peer"

**Gửi tin nhắn:** Nhập và nhấn Enter!

---

## 🔧 Nếu Gặp Lỗi

### Lỗi: "javac not found"
→ Sửa đường dẫn Java trong `build.ps1` và `run.ps1`

### Lỗi: "MongoDB connection failed"
→ Khởi động MongoDB: `Start-Service MongoDB`

### Lỗi: "Port already in use"
→ Đóng các instance đang chạy

---

Xem hướng dẫn chi tiết trong `HUONG_DAN_CHAY_DU_AN.md`


