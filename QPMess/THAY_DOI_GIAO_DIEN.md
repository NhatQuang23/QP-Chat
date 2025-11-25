# Tài liệu Thay đổi Giao diện QPMess

## Tổng quan
Đã thực hiện các cải tiến về giao diện và tính năng quản lý tài khoản cho ứng dụng QPMess.

## Các thay đổi chính

### 1. Khắc phục hiển thị tin nhắn
**File:** `ChatApp.java` - Method `addMessageLabel()`

**Thay đổi:**
- Đã sửa lỗi tin nhắn của bản thân không dồn sát bên phải
- Thêm `HBox messageWrapper` để đảm bảo tin nhắn gửi đi hiển thị đúng vị trí bên phải màn hình
- Tin nhắn nhận được vẫn hiển thị bên trái

**Code cải tiến:**
```java
HBox messageWrapper = new HBox();
messageWrapper.setAlignment(isSent ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
HBox.setHgrow(messageWrapper, Priority.ALWAYS);
```

### 2. Thêm tính năng quản lý tài khoản
**File mới:** `AccountSettingsUI.java`

**Tính năng:**
- Thay đổi tên hiển thị
- Cập nhật email
- Cập nhật số điện thoại
- Thay đổi mật khẩu (yêu cầu nhập mật khẩu hiện tại)
- Thay đổi ảnh đại diện (avatar)
- Giao diện hiện đại với modal dialog

**Các method chính:**
- `chooseAvatar()` - Chọn ảnh đại diện từ máy tính
- `saveChanges()` - Lưu thông tin đã chỉnh sửa
- `loadPasswordFromDatabase()` - Load mật khẩu từ Login collection
- `updateUserInDatabase()` - Cập nhật thông tin vào MongoDB

**Validation:**
- Kiểm tra tên không được để trống
- Kiểm tra định dạng email
- Kiểm tra mật khẩu hiện tại trước khi đổi
- Kiểm tra mật khẩu mới và xác nhận mật khẩu phải khớp
- Mật khẩu mới tối thiểu 6 ký tự

### 3. Cải thiện giao diện người dùng
**File:** `ChatApp.java`

**Thay đổi:**
- Thêm button quản lý tài khoản vào leftPanel
- Thêm userInfoBox hiển thị avatar và thông tin người dùng ở topBar
- Thêm trạng thái "Online" với màu xanh
- userInfoBox có thể click để mở trang quản lý tài khoản
- Avatar có hiệu ứng shadow đẹp mắt

**Code mới:**
```java
// User info box với avatar
HBox userInfoBox = new HBox(10);
userInfoBox.getStyleClass().add("user-info-box");
userInfoBox.setCursor(javafx.scene.Cursor.HAND);
userInfoBox.setOnMouseClicked(e -> {
    AccountSettingsUI accountSettingsUI = new AccountSettingsUI();
    accountSettingsUI.start(new Stage());
});
```

### 4. Cập nhật Model User
**File:** `Collections/User.java`

**Thêm các trường mới:**
- `email` - Địa chỉ email
- `password` - Mật khẩu (được lưu trong Login collection thực tế)
- `phone` - Số điện thoại

**Thêm getters/setters:**
- `getEmail()` / `setEmail()`
- `getPassword()` / `setPassword()`
- `getPhone()` / `setPhone()`

### 5. Cải thiện CSS
**File:** `css/modern.css`

**Thêm classes mới:**
```css
/* User Info Box */
.user-info-box {
    -fx-background-color: rgba(255, 255, 255, 0.05);
    -fx-background-radius: 10px;
    -fx-padding: 8px 12px;
}

.user-info-box:hover {
    -fx-background-color: rgba(255, 255, 255, 0.08);
    -fx-cursor: hand;
}

/* Avatar Round */
.avatar-round {
    -fx-effect: dropshadow(gaussian, rgba(0, 168, 132, 0.3), 8, 0, 0, 0);
}

/* Tooltip */
.tooltip {
    -fx-background-color: #2a3942;
    -fx-text-fill: #e9edef;
    -fx-background-radius: 6px;
    -fx-font-size: 12px;
    -fx-padding: 6px 10px;
}
```

## Hướng dẫn sử dụng

### Mở trang quản lý tài khoản
Có 2 cách:
1. Click vào button quản lý tài khoản (icon settings) ở leftPanel
2. Click vào userInfoBox (avatar + tên) ở góc phải trên của topBar

### Thay đổi thông tin
1. Mở trang quản lý tài khoản
2. Chỉnh sửa các trường thông tin mong muốn
3. Nếu muốn đổi mật khẩu:
   - Nhập mật khẩu hiện tại
   - Nhập mật khẩu mới
   - Xác nhận mật khẩu mới
4. Click "Lưu thay đổi"

### Thay đổi avatar
1. Click "Thay đổi ảnh đại diện"
2. Chọn file ảnh từ máy tính (PNG, JPG, JPEG, GIF)
3. Avatar sẽ được preview ngay lập tức
4. Click "Lưu thay đổi" để lưu

## Database Schema

### Collection User
```javascript
{
  "user_id": int,
  "name": String,
  "profile_picture": String,
  "email": String,
  "phone": String,
  "bio": String,
  "preferred_language": String,
  "created_at": String
}
```

### Collection Login
```javascript
{
  "login_id": int,
  "user_id": int,
  "username": String,
  "password": String
}
```

## Cải tiến trong tương lai
- [ ] Upload avatar lên server thay vì lưu đường dẫn local
- [ ] Thêm xác thực email
- [ ] Thêm xác thực OTP cho đổi mật khẩu
- [ ] History thay đổi thông tin tài khoản
- [ ] Thêm 2FA (Two-Factor Authentication)
- [ ] Crop và resize ảnh avatar tự động
- [ ] Thêm thông báo real-time khi thông tin được cập nhật

## Lưu ý kỹ thuật
- Password được lưu trong collection `Login`, không phải `User`
- Avatar path được lưu dưới dạng URI string
- Email validation chỉ kiểm tra format cơ bản
- Tất cả thay đổi được sync với Session object ngay lập tức
- MongoDB connection sử dụng localhost:27017

## Testing
Để test các tính năng mới:
1. Đăng nhập vào ứng dụng
2. Click vào icon quản lý tài khoản
3. Thử thay đổi từng trường thông tin
4. Verify thay đổi được lưu trong database
5. Đăng xuất và đăng nhập lại để verify

---
**Phiên bản:** 2.0  
**Ngày cập nhật:** 25/11/2025  
**Tác giả:** QPMess Development Team

