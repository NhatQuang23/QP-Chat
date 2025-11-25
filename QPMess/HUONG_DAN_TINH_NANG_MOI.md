# 🎉 HƯỚNG DẪN TÍNH NĂNG MỚI - QPMess

## 📋 Tổng quan các tính năng đã thêm

### ✅ Đã hoàn thành:
1. ✨ **Tin nhắn dồn bên phải** - Tin nhắn của bạn hiển thị sát bên phải
2. 👤 **Quản lý tài khoản** - Đổi tên, email, phone, password, avatar
3. 🚪 **Đăng xuất** - Logout khỏi ứng dụng
4. 📎 **Gửi file** - Đính kèm và gửi file trong chat

---

## 1️⃣ CHỨC NĂNG ĐĂNG XUẤT

### 📍 Vị trí
Button **"Đăng xuất"** nằm ở **góc phải trên**, cạnh User Info Box (avatar + tên)

### 🎯 Cách sử dụng
1. Click button **"Đăng xuất"**
2. Xác nhận trong dialog
3. Hệ thống sẽ:
   - ✅ Xóa session hiện tại
   - ✅ Đóng kết nối P2P
   - ✅ Quay về màn hình đăng nhập

### 💡 Lưu ý
- Tất cả dữ liệu đã lưu trong database không bị mất
- Tin nhắn vẫn được giữ nguyên
- Cần đăng nhập lại để tiếp tục sử dụng

---

## 2️⃣ CHỨC NĂNG GỬI FILE

### 📍 Vị trí
Button **📎** (kẹp giấy) nằm ở **thanh nhập tin nhắn**, bên trái ô nhập text

### 🎯 Cách sử dụng

#### Gửi file cho contact (1-1):
1. Chọn một contact trong danh sách
2. Click button **📎**
3. Chọn file từ máy tính
4. File sẽ được gửi ngay lập tức

#### Gửi file trong group:
1. Chuyển sang tab **"Nhóm"**
2. Chọn một nhóm
3. Click button **📎**
4. Chọn file từ máy tính
5. File sẽ được gửi cho tất cả thành viên

### 📊 Thông tin file hiển thị
- 📎 Icon file
- 📝 Tên file
- 💾 Button "Tải xuống"
- 📏 Kích thước file (KB, MB, GB...)
- ⏰ Thời gian gửi

### 💾 Tải file về
1. Tìm tin nhắn có file đính kèm
2. Click button **"💾 Tải xuống"**
3. File sẽ được mở bằng ứng dụng mặc định

### 📁 Lưu trữ file
- File được lưu trong thư mục: `uploads/`
- Tên file: `timestamp_originalname.ext`
- VD: `1732567890123_document.pdf`

---

## 🗄️ CẤU TRÚC DATABASE

### Collection: Message (Đã cập nhật)

```javascript
{
  "message_id": int,
  "sender_id": int,
  "recipient_id": int,
  "recipient_type": "user" | "group",
  "content": String,
  "timestamp": String,
  
  // ===== FIELDS MỚI =====
  "message_type": "text" | "file" | "image",  // MỚI!
  "file_name": String,                         // MỚI! - Tên file gốc
  "file_path": String,                         // MỚI! - Đường dẫn lưu file
  "file_size": long                            // MỚI! - Kích thước file (bytes)
}
```

### Ví dụ tin nhắn text:
```javascript
{
  "message_id": 123,
  "sender_id": 1,
  "recipient_id": 2,
  "recipient_type": "user",
  "content": "Hello!",
  "timestamp": "2024-11-25T22:30:00",
  "message_type": "text"
}
```

### Ví dụ tin nhắn file:
```javascript
{
  "message_id": 124,
  "sender_id": 1,
  "recipient_id": 2,
  "recipient_type": "user",
  "content": "📎 File: document.pdf",
  "timestamp": "2024-11-25T22:31:00",
  "message_type": "file",
  "file_name": "document.pdf",
  "file_path": "uploads/1732567890123_document.pdf",
  "file_size": 524288
}
```

---

## 🔧 THAY ĐỔI KỸ THUẬT

### Files đã chỉnh sửa:

#### 1. **ChatApp.java**
- ✅ Thêm button Đăng xuất
- ✅ Thêm button Gửi file (📎)
- ✅ Method `logout()` - Xử lý đăng xuất
- ✅ Method `sendFileToContact()` - Gửi file cho contact
- ✅ Method `sendFileToGroup()` - Gửi file cho nhóm
- ✅ Method `persistFileMessage()` - Lưu file message
- ✅ Method `storeFileMessageInDatabase()` - Lưu vào MongoDB
- ✅ Method `openFile()` - Mở file đã tải
- ✅ Method `formatFileSize()` - Format kích thước file
- ✅ Cập nhật `addMessageLabel()` - Hiển thị file attachments

#### 2. **Collections/Message.java**
- ✅ Thêm field `messageType`
- ✅ Thêm field `fileName`
- ✅ Thêm field `filePath`
- ✅ Thêm field `fileSize`
- ✅ Constructor mới cho file messages
- ✅ Getters và Setters

#### 3. **Collections/MessageList.java**
- ✅ Load thông tin file từ database
- ✅ Set file properties vào Message object

#### 4. **Collections/UserGroupMessagesList.java**
- ✅ Load thông tin file từ database cho group messages
- ✅ Set file properties vào Message object

---

## 🎨 GIAO DIỆN

### Top Bar (Thanh trên cùng)
```
[Liên hệ] [Nhóm] [Cộng đồng] [+] [Tin nhắn: X]  ___  [Đăng xuất] [👤 User Info]
                                                  ^^^
                                                Spacer
```

### Bottom Bar (Thanh nhập tin nhắn)
```
[📎]  [________________________]  [Gửi]
 ^^^         Nhập tin nhắn...      ^^^
 File                              Send
```

### Message Bubble với File
```
┌──────────────────────────┐
│ 📎 File: document.pdf    │
│ ┌────────────────┐       │
│ │ 💾 Tải xuống   │       │
│ └────────────────┘       │
│ 512.0 KB                 │
│                    22:31 │
└──────────────────────────┘
```

---

## 🧪 TESTING

### Test Đăng xuất:
```
☑ Click "Đăng xuất"
☑ Xác nhận logout
☑ Verify quay về màn hình login
☑ Thử đăng nhập lại → OK
☑ Verify session mới được tạo
```

### Test Gửi file:
```
☑ Chọn contact
☑ Click button 📎
☑ Chọn file (.pdf, .docx, .jpg, .png...)
☑ Verify file xuất hiện trong chat
☑ Verify hiển thị đúng tên file
☑ Verify hiển thị đúng kích thước
☑ Click "Tải xuống"
☑ Verify file mở được
☑ Check database có message_type="file"
☑ Check folder uploads/ có file
```

### Test Gửi file trong group:
```
☑ Chuyển tab "Nhóm"
☑ Chọn một nhóm
☑ Click button 📎
☑ Chọn file
☑ Verify file xuất hiện trong group chat
☑ Tất cả member nhìn thấy file
```

---

## 🚀 CÁCH SỬ DỤNG

### Bước 1: Compile
```powershell
cd "D:\LTM(3)\QPMess\QPMess"
.\build.ps1
```

### Bước 2: Chạy
```powershell
.\run.ps1
```

### Bước 3: Đăng nhập

### Bước 4: Test tính năng mới
1. ✅ Gửi file cho một contact
2. ✅ Tải file về
3. ✅ Gửi file trong nhóm
4. ✅ Test đăng xuất
5. ✅ Đăng nhập lại

---

## 📊 THỐNG KÊ THAY ĐỔI

| File | Lines Added | Changes |
|------|------------|---------|
| ChatApp.java | ~200 | Logout, Send File, Display File |
| Message.java | ~40 | File fields, constructors |
| MessageList.java | ~20 | Load file data |
| UserGroupMessagesList.java | ~15 | Load file data |
| **TOTAL** | **~275 lines** | **4 files** |

---

## 🎯 TÍNH NĂNG TRONG TƯƠNG LAI

### Có thể thêm:
- [ ] Preview ảnh trực tiếp trong chat
- [ ] Tải nhiều file cùng lúc
- [ ] Progress bar khi upload file lớn
- [ ] Giới hạn kích thước file (VD: max 10MB)
- [ ] Lưu file lên cloud storage (AWS S3, Google Drive...)
- [ ] Nén file trước khi gửi
- [ ] Encryption file
- [ ] Share file với nhiều người cùng lúc
- [ ] File preview cho PDF, Word...
- [ ] Audio/Video player cho media files

---

## 💡 TIPS & TRICKS

### 1. Kiểm tra folder uploads
```powershell
Get-ChildItem uploads\ | Sort-Object LastWriteTime -Descending | Select-Object -First 10
```

### 2. Xem file trong database
```javascript
mongo
use ChatApp
db.Message.find({"message_type": "file"}).pretty()
```

### 3. Xóa tất cả file (cẩn thận!)
```powershell
Remove-Item uploads\* -Force
```

### 4. Backup uploads folder
```powershell
Copy-Item uploads\ uploads_backup\ -Recurse
```

---

## ❓ TROUBLESHOOTING

### Lỗi: "Không thể gửi file"
**Nguyên nhân:** Không có quyền ghi vào folder uploads
**Giải pháp:**
```powershell
# Tạo thư mục uploads
New-Item -ItemType Directory -Path uploads -Force
```

### Lỗi: "File không tồn tại"
**Nguyên nhân:** File đã bị xóa khỏi folder uploads
**Giải pháp:** File cần được giữ trong folder uploads

### Lỗi: "Không thể mở file"
**Nguyên nhân:** Không có ứng dụng mở file type này
**Giải pháp:** Cài đặt ứng dụng phù hợp (PDF reader, Office...)

---

## 📞 HỖ TRỢ

Nếu gặp vấn đề:
1. Check console output cho error messages
2. Verify folder `uploads/` tồn tại
3. Check database có field `message_type`
4. Re-compile và chạy lại

---

**Version:** 3.0  
**Date:** 25/11/2025  
**Status:** ✅ READY TO USE

🎉 **Chúc bạn sử dụng vui vẻ!**

