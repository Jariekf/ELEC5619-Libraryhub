# 🚀 LibraryHub 快速启动指南

## 📋 前置要求

确保已安装以下软件：
- ☕ **Java 17+**
- 🔧 **Maven** (或使用项目自带的 mvnw)
- 📦 **Node.js 18+** 和 **npm**
- 🗄️ **MySQL** (运行在 localhost:3306)

## 🗄️ 数据库设置

1. **启动 MySQL 服务**

2. **创建数据库和用户**
```sql
CREATE DATABASE librarydb;
CREATE USER 'libraryuser'@'localhost' IDENTIFIED BY 'mypassword';
GRANT ALL PRIVILEGES ON librarydb.* TO 'libraryuser'@'localhost';
FLUSH PRIVILEGES;
```

## 🚀 快速启动（推荐）

### 方法 1: 使用启动脚本

```bash
# 赋予脚本执行权限（首次运行）
chmod +x start-app.sh stop-app.sh

# 启动应用
./start-app.sh
```

脚本会自动：
- ✅ 检查端口是否可用
- ✅ 启动后端 (8080)
- ✅ 启动前端 (5173)
- ✅ **自动打开浏览器到登录页面**
- ✅ 显示实时日志

**停止应用：**
```bash
# 按 Ctrl+C 停止日志显示，然后运行：
./stop-app.sh
```

## 🔧 手动启动

### 后端（Spring Boot）

```bash
# 进入后端目录
cd libraryhub

# 启动后端
./mvnw spring-boot:run

# 或者使用系统 Maven
mvn spring-boot:run
```

**后端访问地址：** http://localhost:8080

### 前端（React + Vite）

```bash
# 打开新终端，进入前端目录
cd libraryhub-frontend

# 首次运行需要安装依赖
npm install

# 启动前端开发服务器
npm run dev
```

**前端访问地址：** http://localhost:5173

## 👥 登录凭据

### 管理员账户
- **用户名：** `admin`
- **密码：** `admin123`

### 普通用户
访问 http://localhost:5173/register 注册新账户

## 📱 功能测试

### Customer 用户功能：
1. ✅ 浏览书籍列表
2. ✅ 查看书籍详情
3. ✅ 预订书籍
4. ✅ 加入等待列表
5. ✅ 归还书籍
6. ✅ 撰写和删除评论
7. ✅ 使用 AI 聊天助手

### Admin 用户功能：
1. ✅ 所有 Customer 功能
2. ✅ 编辑书籍信息
3. ✅ 删除书籍
4. ✅ 删除任何用户的评论
5. ✅ 管理用户
6. ✅ 查看所有预订

## 🐛 常见问题

### 端口被占用
```bash
# 查找并停止占用 8080 端口的进程
lsof -ti:8080 | xargs kill -9

# 查找并停止占用 5173 端口的进程
lsof -ti:5173 | xargs kill -9
```

### 数据库连接失败
检查 `libraryhub/src/main/resources/application.properties`：
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/librarydb
spring.datasource.username=libraryuser
spring.datasource.password=mypassword
```

### 前端 CORS 错误
确保后端已重启以应用 CORS 配置

### 前端依赖问题
```bash
cd libraryhub-frontend
rm -rf node_modules package-lock.json
npm install
```

## 📝 日志文件

启动脚本会创建日志文件：
- `backend.log` - 后端日志
- `frontend.log` - 前端日志

**查看实时日志：**
```bash
# 后端日志
tail -f backend.log

# 前端日志
tail -f frontend.log
```

## 🔄 重启应用

```bash
# 停止所有服务
./stop-app.sh

# 重新启动
./start-app.sh
```

## 📚 API 文档

后端 REST API 端点：
- `GET /api/books` - 获取所有书籍
- `GET /api/books/{id}` - 获取单个书籍
- `POST /api/books/{id}/edit` - 编辑书籍（Admin）
- `POST /api/books/{id}/delete` - 删除书籍（Admin）
- `POST /books/{id}/reserve` - 预订书籍
- `POST /books/{id}/return` - 归还书籍
- `POST /books/{id}/join-waiting-list` - 加入等待列表
- `POST /books/{id}/reviews` - 提交评论
- `GET /api/user/current` - 获取当前用户信息

## 🎯 下一步

1. 访问 http://localhost:5173
2. 使用 admin/admin123 登录
3. 浏览和管理书籍
4. 测试所有功能

祝使用愉快！ 📚✨

