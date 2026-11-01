// 在“在网页上运行 JavaScript”中使用；将引号内替换为拥有者提供的分享码。
const SHARE_CODE = "在此填入分享码";
(async () => {
  try {
    if (location.protocol !== "https:" || location.hostname !== "tree.buct.edu.cn") throw new Error("请先在 Safari 打开学校认证页");
    if (!/^[A-Za-z0-9_-]{43}$/.test(SHARE_CODE)) throw new Error("请先在快捷指令中填写分享码");
    const raw = Uint8Array.from(atob(SHARE_CODE.replace(/-/g,"+").replace(/_/g,"/") + "="), c => c.charCodeAt(0));
    const hash = new Uint8Array(await crypto.subtle.digest("SHA-256", raw));
    const id = Array.from(hash, b => b.toString(16).padStart(2,"0")).join("");
    const response = await fetch("https://buct-campus-relay-237239.pages.dev/v1/" + id, {credentials:"omit"});
    if (!response.ok) throw new Error("云端账号读取失败：HTTP " + response.status);
    const data = await response.json();
    if (typeof data.blob !== "string" || !/^[A-Za-z0-9_-]{39,6000}$/.test(data.blob)) throw new Error("云端数据格式错误");
    const text = data.blob.replace(/-/g,"+").replace(/_/g,"/");
    const encrypted = Uint8Array.from(atob(text.padEnd(Math.ceil(text.length/4)*4,"=")), c => c.charCodeAt(0));
    const key = await crypto.subtle.importKey("raw", raw, "AES-GCM", false, ["decrypt"]);
    const plain = await crypto.subtle.decrypt({name:"AES-GCM",iv:encrypted.slice(0,12)}, key, encrypted.slice(12));
    const credentials = JSON.parse(new TextDecoder().decode(plain));
    if (typeof credentials.username !== "string" || !credentials.username || typeof credentials.password !== "string" || !credentials.password) throw new Error("账号数据不完整");
    const user = document.getElementById("username"), pass = document.getElementById("password"), button = document.getElementById("login-account");
    if (!user || !pass || !button) throw new Error("未找到登录表单，请等页面加载后重试");
    user.value = credentials.username; pass.value = credentials.password;
    user.dispatchEvent(new Event("input",{bubbles:true}));pass.dispatchEvent(new Event("input",{bubbles:true}));
    button.click(); completion("已提交，请查看学校页面的登录结果");
  } catch (error) { completion("登录未完成：" + (error.message || "请检查网络和分享码")); }
})();