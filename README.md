# Luồng hoạt động cơ bản của app demo sau:

## 1. Đăng ký tài khoản
- Người dùng truy cập trang đăng ký (/register)
- Điền thông tin username và password
- Hệ thống kiểm tra username đã tồn tại chưa
- Nếu hợp lệ, tạo tài khoản mới với role mặc định là USER
- Chuyển hướng về trang đăng nhập

## 2. Đăng nhập
- Người dùng truy cập trang đăng nhập (/login) 
- Điền thông tin username và password
- Hệ thống kiểm tra thông tin đăng nhập
- Nếu hợp lệ:
  + Tạo session lưu thông tin user
  + Nếu role là ADMIN -> chuyển đến trang admin
  + Nếu role là USER -> chuyển đến trang home
- Nếu không hợp lệ -> hiển thị thông báo lỗi

## 3. Phân quyền truy cập (AuthFilter)
- Kiểm tra session của mọi request
- Nếu chưa đăng nhập:
  + Cho phép truy cập trang login, register và resource files
  + Chuyển hướng các request khác về trang login
- Nếu đã đăng nhập:
  + Không cho phép truy cập lại trang login/register
  + Kiểm tra quyền truy cập trang admin
  + Cho phép truy cập các trang phù hợp với role

## 4. Đăng xuất
- Người dùng click nút đăng xuất
- Hệ thống hủy session
- Chuyển hướng về trang đăng nhập
