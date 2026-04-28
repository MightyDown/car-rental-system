# 驰速租车管理系统 — 生产环境部署指南

## 部署架构

```
用户浏览器 (公网)
       │
       ▼
   Nginx (80/443)
   ├── /api/*        → proxy_pass → localhost:8080 (Spring Boot)
   └── 其他           → 前端静态文件 (/var/www/car-rental)
       │
       ▼
   Spring Boot (127.0.0.1:8080)
       │
       ▼
   MySQL 8.0 (127.0.0.1:3306)
```

生产环境中，Nginx 作为统一入口处理所有请求，前后端同源，无需 CORS 配置。

***

## 一、服务器选购

| 要求  | 最低配置             | 推荐配置             |
| --- | ---------------- | ---------------- |
| CPU | 1 核              | 2 核              |
| 内存  | 1 GB             | 2 GB             |
| 系统盘 | 40 GB            | 60 GB            |
| 系统  | Ubuntu 22.04 LTS | Ubuntu 22.04 LTS |
| 带宽  | 1 Mbps           | 3 Mbps           |

推荐：阿里云 ECS / 腾讯云轻量应用服务器，2C2G 套餐，约 50-80 元/月。

购买后安全组（防火墙）开放端口：

| 端口  | 协议  | 用途            |
| --- | --- | ------------- |
| 22  | TCP | SSH 远程登录      |
| 80  | TCP | HTTP（Web 访问）  |
| 443 | TCP | HTTPS（SSL 证书） |

**不要开放 8080 和 3306**，后端和数据库只监听 127.0.0.1，不对外暴露。

***

## 二、环境安装

SSH 登录服务器后，按顺序执行。

### 2.1 更新系统

```bash
sudo apt update && sudo apt upgrade -y
```

### 2.2 安装 JDK 17

```bash
sudo apt install openjdk-17-jdk -y
java -version
```

### 2.3 安装 MySQL 8.0

```bash
sudo apt install mysql-server -y

# 安全初始化（设置 root 密码、移除匿名用户等）
sudo mysql_secure_installation
```

创建数据库和用户：

```sql
sudo mysql -u root -p
```

```sql
CREATE DATABASE car_rental
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

-- 生产环境建议创建独立用户，不要用 root
CREATE USER 'carrental'@'localhost' IDENTIFIED BY '你的强密码';
GRANT ALL PRIVILEGES ON car_rental.* TO 'carrental'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

导入建表脚本（从项目根目录）：

```bash
mysql -u carrental -p car_rental < schema.sql
```

### 2.4 安装 Nginx

```bash
sudo apt install nginx -y
sudo systemctl enable nginx
sudo systemctl start nginx
```

此时访问 `http://你的服务器IP` 应看到 Nginx 欢迎页。

***

## 三、部署步骤

### 3.1 在本地准备文件

#### 3.1.1 创建生产环境配置

在 `car-rental-backend/src/main/resources/` 下新建 `application-prod.yml`：

```yaml
# ==================== 生产环境配置 ====================

spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/car_rental?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
    username: carrental
    password: ${DB_PASSWORD}          # 通过环境变量传入，不要硬编码
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      idle-timeout: 300000
      max-lifetime: 1200000
      connection-timeout: 30000

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

logging:
  level:
    com.carrental: info                  # 生产关闭 debug
```

修改 `application.yml`，激活 prod 环境：

```yaml
spring:
  profiles:
    active: prod                         # 改为 prod
```

#### 3.1.2 打包后端

```bash
cd car-rental-backend
mvn clean package -DskipTests
```

产物在 `target/car-rental-backend-1.0.0-SNAPSHOT.jar`。

#### 3.1.3 打包前端

```bash
cd car-rental-frontend
npm run build
```

产物在 `dist/` 目录。

### 3.2 上传到服务器

```bash
# 上传后端 jar
scp car-rental-backend/target/car-rental-backend-1.0.0-SNAPSHOT.jar \
    root@你的服务器IP:/opt/car-rental/

# 上传前端文件
scp -r car-rental-frontend/dist/* \
    root@你的服务器IP:/var/www/car-rental/
```

### 3.3 配置并启动后端

在服务器上：

```bash
# 创建 systemd 服务
sudo tee /etc/systemd/system/car-rental.service << 'EOF'
[Unit]
Description=Car Rental Backend
After=network.target mysql.service

[Service]
Type=simple
User=www-data
WorkingDirectory=/opt/car-rental
Environment="DB_PASSWORD=你的数据库密码"
Environment="JWT_SECRET=$(openssl rand -base64 32)"
ExecStart=/usr/bin/java -jar /opt/car-rental/car-rental-backend-1.0.0-SNAPSHOT.jar
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

sudo systemctl daemon-reload
sudo systemctl enable car-rental
sudo systemctl start car-rental
```

检查是否启动成功：

```bash
sudo systemctl status car-rental
sudo journalctl -u car-rental -f   # 查看实时日志
```

### 3.4 配置 Nginx

```bash
sudo tee /etc/nginx/sites-available/car-rental << 'EOF'
server {
    listen 80;
    server_name _;                        # 替换为你的域名（如 example.com）

    # 前端静态文件
    root /var/www/car-rental;
    index index.html;

    # 后端 API 代理
    location /api/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # Vue History 模式：非 API 请求回退到 index.html
    location / {
        try_files $uri $uri/ /index.html;
    }

    # 静态资源缓存 7 天
    location ~* \.(js|css|png|jpg|jpeg|gif|svg|ico|woff|woff2)$ {
        expires 7d;
        add_header Cache-Control "public, immutable";
    }
}
EOF

# 启用站点
sudo ln -sf /etc/nginx/sites-available/car-rental /etc/nginx/sites-enabled/
sudo rm -f /etc/nginx/sites-enabled/default

# 测试配置并重启
sudo nginx -t
sudo systemctl reload nginx
```

***

## 四、配置 HTTPS（可选但推荐）

使用 Let's Encrypt 免费 SSL 证书：

```bash
sudo apt install certbot python3-certbot-nginx -y

# 确保 Nginx 配置中 server_name 已改为你的域名
sudo certbot --nginx -d your-domain.com
```

证书会自动续期，无需手动操作。

***

## 五、验证部署

```bash
# 测试后端 API
curl http://你的服务器IP/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 应返回：
# {"code":200,"message":"登录成功","data":{...}}
```

浏览器访问 `http://你的服务器IP` 应看到首页。

***

## 六、常用运维命令

```bash
# 查看后端状态
sudo systemctl status car-rental

# 重启后端
sudo systemctl restart car-rental

# 查看后端日志
sudo journalctl -u car-rental -f

# 重启 Nginx
sudo systemctl reload nginx

# 更新部署（替换 jar 后）
sudo systemctl restart car-rental
sudo systemctl reload nginx
```

***

## 七、更新部署流程

每次代码更新后：

```bash
# 1. 本地重新打包
cd car-rental-backend && mvn clean package -DskipTests
cd car-rental-frontend && npm run build

# 2. 上传到服务器
scp car-rental-backend/target/car-rental-backend-1.0.0-SNAPSHOT.jar root@服务器IP:/opt/car-rental/
scp -r car-rental-frontend/dist/* root@服务器IP:/var/www/car-rental/

# 3. 服务器上重启后端
ssh root@服务器IP "sudo systemctl restart car-rental"
```

***

## 八、安全清单

- [ ] MySQL root 密码已修改
- [ ] 应用使用独立数据库用户（非 root）
- [ ] 数据库密码通过环境变量传入，不写在配置文件中
- [ ] JWT\_SECRET 已生成随机强密钥
- [ ] 8080 / 3306 端口不对外开放
- [ ] SSH 端口已修改或配置 fail2ban
- [ ] 已配置 HTTPS（生产环境必须）
- [ ] 定期备份数据库：`mysqldump -u root -p car_rental > backup.sql`

