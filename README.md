# 校园网助手

面向北京化工大学校园网的账号同步与认证辅助工具，支持 Android 应用和 iPhone 网页版。

学校认证入口：[tree.buct.edu.cn](https://tree.buct.edu.cn/)。本项目为个人工具，与学校官方客户端无隶属关系。

## 功能

- **账户拥有者**：在设备上加密账号密码，上传到 Cloudflare，并生成分享码。
- **使用者**：通过分享码同步账号，在连接校园 Wi-Fi 前保存到本机。
- **Android**：打开学校真实认证页面，尝试自动填入账号密码并点击登录。
- **iPhone**：通过 Safari 网页同步账号，分别复制账号、密码到学校认证页面完成登录。
- Android 使用 Android Keystore 加密本机数据；网页使用 Web Crypto 与 IndexedDB 保存加密数据，并通过 Service Worker 缓存页面。

> 分享码包含解密所需的密钥。持有分享码的人可以取得完整账号和密码，请只分享给可信任且获得授权的使用者。已同步的副本无法远程撤回。

## 使用方法

### Android

1. 在 Releases 中下载 APK，或使用本地构建得到的安装包。
2. 首次同步时，连接可以访问互联网的网络。
3. 拥有者上传账号，或使用者输入分享码同步。
4. 连接校园 Wi-Fi，返回应用点击“一键登录校园网”。
5. 认证结果以学校页面显示为准。

当前版本：1.3；最低 Android 6.0（API 23）。当前交付 APK 是调试签名版，不是应用商店发行版。

### iPhone

当前部署的网页入口：

[打开校园网助手](https://buct-campus-relay-237239.pages.dev/ios/)

1. 使用 Safari 打开入口，通过分享菜单选择“添加到主屏幕”。
2. 建议先添加，再从主屏幕打开并同步账号。
3. 连接校园 Wi-Fi，分别复制账号和密码，在学校认证页面粘贴并登录。

iPhone 网页版目前不支持向学校页面自动填写账号密码。清除网站数据或移除本机存储可能导致账号、分享码和拥有者上传权限丢失。离线缓存是否保留取决于浏览器和系统。

## 项目结构

~~~text
.
├── README.md
├── 未登录界面/                  学校认证页面参考文件
└── app/
    ├── android/                原生 Android Java 项目
    ├── cloudflare/             Worker 与 Durable Object 存储服务
    ├── cloudflare-pages/       Pages 备用入口及 iPhone 网页版
    │   └── public/
    │       ├── _worker.js      转发至原 Worker
    │       └── ios/            网页、样式、脚本及离线缓存
    ├── artifacts/              本地生成的 APK
    └── 使用说明.md
~~~

## 部署自己的 Cloudflare 服务

需要 Node.js、Cloudflare 账户及 Wrangler 登录权限。服务使用 Workers、SQLite Durable Objects 和 Pages，实际用量受 Cloudflare 配额及计费规则约束。

### 1. 部署存储 Worker

~~~powershell
cd app/cloudflare
npx wrangler login
npx wrangler deploy
~~~

配置位于 `wrangler.toml`。默认 Worker 名称为 `buct-campus-connect`，用于保存密文和拥有者写入令牌的哈希。

本地接口检查：

~~~powershell
node test-worker.mjs
~~~

该脚本使用模拟存储验证接口逻辑，不等同于真实云端或手机端测试。

### 2. 部署 Pages 入口

~~~powershell
cd ../cloudflare-pages
npx wrangler pages project create <你的项目名> --production-branch main
npx wrangler pages deploy public --project-name <你的项目名> --branch main
~~~

部署前修改 `cloudflare-pages/wrangler.toml`：

- 将 `name` 改为自己的 Pages 项目名。
- 将 `services.service` 设置为第一步实际部署的 Worker 名称。

如果当前 Wrangler 将新 Pages 项目自动委托到 Workers，且提示缺少入口，请按其提示使用传统 Pages 创建方式，例如在创建命令追加 `--force`。已有项目的后续部署不需要此参数。

Pages 通过服务绑定访问 Worker，不依赖手机直接访问 `workers.dev`。

### 3. 配置应用地址

- Android 默认入口定义在 `MainActivity.java` 的 `DEFAULT_ENDPOINT` 中。
- 部署自己的项目后，将其替换为新的 Pages HTTPS 地址。
- 页面中的上传和同步请求使用同源 `/v1/` 接口。
- iPhone 入口为 Pages 地址后加 `/ios/`。

当前仓库中的公开入口属于现有部署，仅用于说明。自行部署时应使用自己的项目与地址。

## 构建 Android

本项目使用 Android Gradle Plugin 8.2.1、Gradle 8.2.1、Android SDK 34。准备兼容的 JDK（建议 17），并配置 `ANDROID_HOME`。

可使用 Android Studio 打开 `app/android`，或在安装 Gradle 后执行：

~~~powershell
cd app/android
gradle assembleDebug
~~~

仓库当前未包含 Gradle Wrapper，需要自行安装兼容版本的 Gradle。

默认输出：

~~~text
app/android/app/build/outputs/apk/debug/app-debug.apk
~~~

调试构建使用本机调试证书。覆盖安装旧版要求使用相同签名；正式分发应自行管理发行签名，并妥善保管证书。

## 接口概要

| 方法 | 路径 | 用途 |
| --- | --- | --- |
| GET | / | Pages 入口状态检查 |
| GET | /ios/ | iPhone 网页版 |
| GET | /v1/:id | 读取账号密文 |
| PUT | /v1/:id | 创建或更新账号密文 |

`:id` 是分享密钥的 SHA-256。上传内容使用 AES-256-GCM 加密，前 12 字节为 IV，其后为密文及认证标签。更新记录需要拥有者设备保存的独立写入令牌。

服务不提供公开账号列表，也不验证校园网账号密码是否正确。正式公开使用时，请根据规模配置限流并关注用量。

## 限制与验证情况

- 已完成 Android 编译及 APK 签名校验。
- 已使用本地 Cloudflare 模拟环境检查上传、同步、未授权更新拒绝及分享码格式兼容。
- 已验证 Pages 部署与资源访问。
- 未完成所有真实 Android/iPhone 型号及校园网环境下的端到端认证测试。
- 学校页面结构、证书、设备数量限制或网络策略变化可能影响自动登录。
- 默认 Cloudflare 地址能否访问取决于当前网络；部署成功不代表所有校园网或移动网络均可访问。
- 拥有者清除本机数据后会丢失写入令牌，可能需要重新创建并分享新的账号记录。

## 上传 GitHub

建议将 APK 放在 GitHub Releases 中，源码提交到仓库。随附的 `.gitignore` 排除了构建缓存、Wrangler 本地状态、签名文件和 APK。

不要提交：

- Cloudflare OAuth/API Token、密码或包含真实凭据的配置；
- Android 签名证书、签名密码和本地 SDK；
- 浏览器本地数据、真实分享码或包含账号信息的截图。

如果使用 GitHub 网页“上传文件”，忽略规则不会替您筛选手动选择的文件，请自行排除上述内容。

学校参考页面中的图片、脚本等第三方素材，其权利归原权利人所有。仓库目前未指定开源许可证；公开发布前请自行选择适用许可证，并确认参考素材的分发权限。
