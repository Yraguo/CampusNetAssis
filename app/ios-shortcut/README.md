# iPhone 快捷指令

这个方案不需要 Mac 或 Apple 开发者账号。它使用 iPhone 的 Safari 和快捷指令，在校园网认证页面尝试自动填入已分享的账号密码。

## 准备

- 账户拥有者先使用 Android 应用上传账号，获取分享码。
- iPhone 需要能访问部署的 Cloudflare Pages 服务。
- 将分享码仅提供给可信任的人。分享码可以解密出完整账号和密码；隐藏输入和按钮无法防止技术人员提取密码。
- 不要把填入真实分享码的脚本、截图或快捷指令上传到公开仓库。

## 创建快捷指令

1. 用 iPhone 的 Safari 打开 [脚本模板](https://buct-campus-relay-237239.pages.dev/ios/shortcut.txt)，全选并复制。
2. 打开“快捷指令”，新建快捷指令，添加“在网页上运行 JavaScript”操作。
3. 清空默认脚本，粘贴模板。
4. 将开头的 `在此填入分享码` 替换成账户拥有者提供的分享码，保留引号。
5. 打开快捷指令详情，开启“在共享表单中显示”，接收类型选择“Safari 网页”，保存。
6. 按系统提示在快捷指令的高级设置中允许运行脚本。

参考：[Apple 快捷指令官方说明](https://support.apple.com/guide/shortcuts/apdb71a01d93/ios)。

## 使用

1. 连接校园 Wi-Fi。
2. 在 Safari 打开 `https://tree.buct.edu.cn`，等待登录表单加载。
3. 点击分享按钮，选择刚创建的快捷指令，按系统提示允许其运行。
4. 以学校页面显示的结果为准。

快捷指令需要从 Safari 分享菜单运行，不能保证从主屏幕一次点击即完成。校园网认证前如果不能访问 Cloudflare，脚本无法取得云端密文；学校页面的安全策略、页面结构或网络变化也可能导致脚本不能运行。尚未在真实 iPhone 和校园网环境完成端到端测试。

## 自行部署

脚本中的 Pages 地址默认指向现有部署。如使用自己的 Cloudflare 服务，请将脚本内的 `https://buct-campus-relay-237239.pages.dev` 替换为自己的 Pages HTTPS 地址。

Pages 中转接口只对来源为 `https://tree.buct.edu.cn` 的跨域 GET 添加浏览器 CORS 响应头；CORS 不是身份认证或访问控制。原 Worker 仍负责保存密文和验证写入令牌。

## 本地测试

在本目录执行：

```powershell
node test-shortcut.cjs
```

测试使用模拟账号和模拟接口，不会连接或验证真实校园网。
