# Xây dựng ứng dụng Chat QPMess dựa trên giao thức TCP và mô hình Peer-to-Peer (P2P)

Ứng dụng hiện tại chạy theo mô hình ngang hàng (P2P) trên nền JavaFX. Mỗi người dùng vừa là **server** (lắng nghe TCP) vừa là **client** (gửi tin nhắn đến peer khác). Thực hiện theo các bước sau để chạy được toàn bộ hệ thống trên cùng một máy hoặc nhiều máy trong mạng LAN.

---

## 1. Chuẩn bị môi trường
1. **Java 21** (hoặc mới hơn) – cần thiết vì JavaFX SDK đi kèm được biên dịch với version 61.
2. **MongoDB** chạy ở `mongodb://localhost:27017` và đã có sẵn database `ChatApp`.
3. Thư mục dự án: `Chat-App-Clone-master` (đã bao gồm JavaFX SDK và Mongo driver).

> Nếu cần tài khoản mẫu, hãy tham khảo dữ liệu JSON trong thư mục `JSON/`.

---

## 2. Biên dịch
Mở PowerShell, chuyển vào thư mục dự án và biên dịch bằng JDK 21:

```powershell
cd "D:\Tran Dang Bao Phuc\Lap trinh mang\QPMess"
& "C:\Program Files\Java\jdk-21\bin\javac.exe" `
   -cp ".;MongoDriver\mongo-java-driver-3.12.13.jar" `
   --module-path "FX SDK\lib" `
   --add-modules javafx.controls,javafx.fxml `
   Collections\*.java SessionManager\*.java Views\*.java network\*.java `
   ChatApp.java Home.java NewContactDialog.java PrivacySettingsUI.java UserRegistrationForm.java
```

---

## 3. Khởi chạy mỗi peer (server + client)
1. Trong mỗi cửa sổ PowerShell mới, chạy:
   ```powershell
   cd "D:\Tran Dang Bao Phuc\Lap trinh mang\Chat-App-Clone-master"
   & "C:\Program Files\Java\jdk-21\bin\java.exe" `
      -cp ".;MongoDriver\mongo-java-driver-3.12.13.jar" `
      --module-path "FX SDK\lib" `
      --add-modules javafx.controls,javafx.fxml `
      Home
   ```
2. Đăng nhập bằng tài khoản MongoDB tương ứng. Khi đăng nhập thành công, mỗi người dùng sẽ:
   - Lắng nghe TCP ở port `6000 + user_id` (ví dụ user 1 → port 6001).
   - Hiển thị các liên hệ, nhóm, và khung cấu hình peer mới (host + port).

---

## 4. Thiết lập thông tin peer (client side)
1. Chọn liên hệ trong danh sách Contacts.
2. Trong thanh “TCP P2P” phía trên:
   - `Peer host`: nhập địa chỉ IP của máy đối tác (mặc định `127.0.0.1` nếu cùng máy).
   - `Peer port`: nhập port mà người kia đang lắng nghe (ví dụ `6002` cho user_id = 2).
3. Nhấn **Save Peer** để lưu thông tin endpoint cho liên hệ đang chọn.

---

## 5. Gửi/nhận tin nhắn
1. Nhập nội dung ở ô chat, nhấn **Send**:
   - Tin nhắn sẽ được gửi qua TCP đến peer đã lưu.
   - Đồng thời được ghi lại vào MongoDB để đồng bộ lịch sử.
2. Khi peer kia gửi tin, ứng dụng sẽ nhận qua socket và tự động hiển thị nếu đang mở đúng cuộc hội thoại.

---

## 6. Lưu ý
- Cần mở nhiều phiên bản ứng dụng (mỗi phiên bản đăng nhập tài khoản khác) để thử P2P thực sự.
- Đảm bảo firewall cho phép kết nối TCP tại các port `6001`, `6002`, …
- Nếu chạy trên các máy khác nhau, thay địa chỉ `127.0.0.1` bằng IP LAN tương ứng.
- Ứng dụng hiện chưa hỗ trợ NAT traversal hay mã hóa đường truyền; chỉ nên dùng trong mạng tin cậy hoặc phát triển.

---

Sau khi hoàn tất các bước trên, bạn đã có thể vận hành server/client cho từng người dùng và thử nghiệm chức năng chat P2P qua TCP. Chúc bạn thành công!

