# UTH Todo

Ứng dụng Android quản lý công việc cá nhân cho sinh viên, viết bằng Kotlin và Jetpack Compose. App chia việc theo hai nhóm chính là **Học tập** và **Cuộc sống**, hỗ trợ theo dõi tiến độ theo ngày, quản lý việc nhỏ (subtask), nhắc nhở trước hạn chót và đổi giao diện Sáng/Tối.

## Thông tin dự án

- **Tác giả:** Nguyễn Tuấn Khang (`Nguyentuankhang62`)
- **Package:** `com.uth.uthtodo`
- **Phiên bản:** `2.1.0`
- **Yêu cầu hệ điều hành:** Android 7.0 (API 24) trở lên (`targetSdk = 36`, `compileSdk = 36`)
- **Kiến trúc:** MVVM kết hợp Navigation Compose

## Chức năng chính

- **Màn hình chào và giới thiệu:** Splash Screen và 3 bước Onboarding giới thiệu nhanh các tính năng.
- **Đăng nhập và tài khoản:** Đăng nhập/đăng ký bằng Email qua Firebase Authentication, dùng thử không cần tài khoản (Guest), luồng Quên mật khẩu (nhập email, mã OTP, đặt mật khẩu mới) và màn Hồ sơ cá nhân.
- **Trang chủ:** Hiển thị tiến độ hoàn thành trong ngày, số việc còn lại theo nhóm Học tập và Cuộc sống, danh sách việc hôm nay và đánh dấu hoàn thành nhanh.
- **Danh sách công việc:** Tìm kiếm theo từ khóa, lọc theo Tất cả / Học tập / Cuộc sống / Chưa xong, phân tách mục Cần làm và Đã xong.
- **Thêm và xem chi tiết công việc:** Tạo việc mới (tên, ghi chú, nhóm, ngày, giờ, nhắc trước 15 phút, mức ưu tiên), xem chi tiết, thêm/tick/xóa việc nhỏ (subtask) và xóa công việc qua REST API.
- **Thông báo:** Hiển thị danh sách nhắc nhở (sắp đến hạn, quá hạn, thành tích), đánh dấu đã đọc và trạng thái trống khi không có thông báo.
- **Cài đặt giao diện:** Đổi chế độ **Sáng / Tối / Tự động**, chọn 4 màu chủ đạo, chỉnh cỡ chữ, bật/tắt thông báo nhắc việc theo ngữ cảnh.
- **Tích hợp Firebase:** Authentication, Analytics, Firestore, Crashlytics, Cloud Messaging (FCM), Remote Config và Cloud Storage.

## Công nghệ sử dụng

- **Ngôn ngữ:** Kotlin
- **Giao diện:** Jetpack Compose, Material 3
- **Điều hướng:** Navigation Compose
- **Mạng và dữ liệu:** `HttpURLConnection` (REST API), Kotlin Coroutines, StateFlow
- **Backend / Dịch vụ:** Firebase BoM (Auth, Firestore, Analytics, Crashlytics, FCM, Remote Config, Storage)
- **Kiểm thử:** JUnit 4 (Unit Test), Android Lint

## Cấu trúc thư mục

```text
UTH__TODO/
├── SoucrCode/                  # Mã nguồn dự án Android (mở thư mục này trong Android Studio)
│   └── app/
│       ├── google-services.json
│       └── src/
│           ├── main/java/com/uth/uthtodo/
│           │   ├── auth/       # Xử lý đăng nhập Firebase Auth
│           │   ├── data/       # Gọi REST API công việc
│           │   ├── firebase/   # Quản lý các dịch vụ Firebase
│           │   ├── model/      # Data class, enum, logic miền nghiệp vụ
│           │   ├── navigation/ # Cấu hình NavHost và các route
│           │   ├── ui/         # Màn hình, component dùng chung, theme Sáng/Tối
│           │   └── viewmodel/  # ViewModel và UiState (MVVM)
│           └── test/           # Unit test kiểm thử logic
└── Tai_lieu/                   # Ảnh chụp màn hình và tài liệu các giai đoạn phát triển
```

## Yêu cầu môi trường

- Android Studio (bản hỗ trợ AGP 9 / JDK 17–21)
- JDK 17 hoặc JDK 21
- Android SDK API 36 và máy ảo (Emulator) hoặc điện thoại Android bật **USB Debugging**

## Cách tải về và chạy thử

### 1. Clone dự án

```bash
git clone https://github.com/Nguyentuankhang62/UTH__TODO.git
cd UTH__TODO
```

### 2. Chạy bằng Android Studio (khuyên dùng)

1. Mở Android Studio, chọn **Open** và trỏ thẳng vào thư mục `UTH__TODO/SoucrCode`.
2. Đợi Gradle đồng bộ thư viện xong.
3. Chọn thiết bị (Emulator hoặc điện thoại Android cắm cáp USB) rồi bấm **Run** (`Shift + F10`).

### 3. Build và cài bằng dòng lệnh (PowerShell trên Windows)

```powershell
cd SoucrCode
.\gradlew.bat :app:assembleDebug
```

File APK sau khi build nằm tại:

```text
SoucrCode\app\build\outputs\apk\debug\app-debug.apk
```

Cài đặt và mở trên thiết bị đang kết nối ADB:

```powershell
adb install -r app\build\outputs\apk\debug\app-debug.apk
adb shell am start -n com.uth.uthtodo/.MainActivity
```

### 4. Chạy Unit Test và Lint

```powershell
cd SoucrCode
.\gradlew.bat :app:testDebugUnitTest :app:lintDebug
```

## Ghi chú khi chạy thử

- Thiết bị cần có kết nối Internet để tải danh sách công việc từ REST API và xác thực Firebase.
- Trên Android 13 trở lên, khi vào Trang chủ lần đầu hoặc bật công tắc **Nhắc việc**, ứng dụng sẽ hỏi quyền thông báo theo ngữ cảnh; bạn có thể cho phép hoặc bỏ qua mà không ảnh hưởng các chức năng khác.
- Nếu muốn gắn sang project Firebase của riêng bạn, hãy thay file `SoucrCode/app/google-services.json` bằng file cấu hình từ Firebase Console (giữ nguyên package `com.uth.uthtodo`).
