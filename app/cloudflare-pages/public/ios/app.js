"use strict";
const $ = id => document.getElementById(id);
const encoder = new TextEncoder(), decoder = new TextDecoder();
let state = {}, db;
let ownerDraft = null, messageTimer;
const b64 = bytes => btoa(String.fromCharCode(...new Uint8Array(bytes))).replace(/\+/g,"-").replace(/\//g,"_").replace(/=+$/,"");
function un64(value) {
  if (!/^[A-Za-z0-9_-]{43}$/.test(value)) throw new Error("分享码格式不正确");
  const binary = atob(value.replace(/-/g,"+").replace(/_/g,"/") + "=");
  return Uint8Array.from(binary, c => c.charCodeAt(0));
}
const random = () => b64(crypto.getRandomValues(new Uint8Array(32)));
const hex = bytes => Array.from(new Uint8Array(bytes), x=>x.toString(16).padStart(2,"0")).join("");
function notify(message) {
  const box=$("message"); box.textContent=message; box.hidden=false;
  clearTimeout(messageTimer);messageTimer=setTimeout(()=>box.hidden=true,4500);
}
function openDb() {
  return new Promise((resolve,reject)=>{
    const request=indexedDB.open("campus-connect-ios",1);
    request.onupgradeneeded=()=>request.result.createObjectStore("secure");
    request.onsuccess=()=>resolve(request.result);
    request.onerror=()=>reject(new Error("无法使用本机存储，请检查 Safari 设置"));
  });
}
function read(key) { return new Promise((resolve,reject)=>{const r=db.transaction("secure","readonly").objectStore("secure").get(key);r.onsuccess=()=>resolve(r.result);r.onerror=()=>reject(r.error);}); }
function write(values) { return new Promise((resolve,reject)=>{const tx=db.transaction("secure","readwrite");for(const [k,v] of Object.entries(values))tx.objectStore("secure").put(v,k);tx.oncomplete=()=>resolve();tx.onerror=()=>reject(tx.error);tx.onabort=()=>reject(tx.error);}); }
async function deviceKey() {
  let key=await read("deviceKey");
  if (!key) {
    key=await crypto.subtle.generateKey({name:"AES-GCM",length:256},false,["encrypt","decrypt"]);
    await write({deviceKey:key});
  }
  return key;
}
async function save(next) {
  const key=await deviceKey(), iv=crypto.getRandomValues(new Uint8Array(12));
  const cipher=await crypto.subtle.encrypt({name:"AES-GCM",iv},key,encoder.encode(JSON.stringify(next)));
  await write({record:{iv,cipher}});state=next;
}
async function load() {
  const rec=await read("record");if(!rec)return {};
  const key=await read("deviceKey");if(!key)throw new Error("本机账号无法解密，请重新同步");
  return JSON.parse(decoder.decode(await crypto.subtle.decrypt({name:"AES-GCM",iv:rec.iv},key,rec.cipher)));
}
async function sharedKey(code, usages) {
  return crypto.subtle.importKey("raw",un64(code),{name:"AES-GCM"},false,usages);
}
async function recordId(code) { return hex(await crypto.subtle.digest("SHA-256",un64(code))); }
async function encryptShare(code, payload) {
  const iv=crypto.getRandomValues(new Uint8Array(12));
  const encrypted=new Uint8Array(await crypto.subtle.encrypt({name:"AES-GCM",iv},await sharedKey(code,["encrypt"]),encoder.encode(JSON.stringify(payload))));
  const result=new Uint8Array(iv.length+encrypted.length);result.set(iv);result.set(encrypted,12);return b64(result);
}
async function decryptShare(code, blob) {
  if(!/^[A-Za-z0-9_-]{39,6000}$/.test(blob))throw new Error("云端数据格式错误");
  const binary=atob(blob.replace(/-/g,"+").replace(/_/g,"/").padEnd(Math.ceil(blob.length/4)*4,"="));
  const bytes=Uint8Array.from(binary,c=>c.charCodeAt(0));
  try { return JSON.parse(decoder.decode(await crypto.subtle.decrypt({name:"AES-GCM",iv:bytes.slice(0,12)},await sharedKey(code,["decrypt"]),bytes.slice(12)))); }
  catch { throw new Error("分享码不正确，或账号数据已损坏"); }
}
async function api(code, method, data) {
  let response;
  try { response=await fetch("/v1/"+await recordId(code),{method,headers:data?{"Content-Type":"application/json"}:{},body:data?JSON.stringify(data):undefined,cache:"no-store",redirect:"error",signal:AbortSignal.timeout ? AbortSignal.timeout(15000) : undefined}); }
  catch { throw new Error("无法连接云端，请检查网络后重试"); }
  if(!response.ok) throw new Error(response.status===404?"未找到账号，请检查分享码":response.status===403?"没有上传权限，请使用原上传设备":"云端请求失败（"+response.status+"）");
  return response.json();
}
function screen(id) { document.querySelectorAll(".screen").forEach(el=>el.hidden=el.id!==id);window.scrollTo(0,0); }
function home() {
  $("statusTitle").textContent=state.username?"随时可以连接":"准备连接";
  $("statusDesc").textContent=state.username?"账号已保存在本机，连接校园 Wi-Fi 后即可登录。":"先同步账号，再连接校园 Wi-Fi。";
  $("clear").hidden=!state.username&&!state.ownerCode;screen("home");
}
function copy(value) {
  if (!value) return;
  if (!navigator.clipboard || !navigator.clipboard.writeText) { window.prompt("请长按并复制以下内容",value);return; }
  navigator.clipboard.writeText(value).then(()=>notify("已复制")).catch(()=>window.prompt("请长按并复制以下内容",value));
}
async function action(form, fn) {
  const button=form.querySelector("button[type=submit]");button.disabled=true;
  try { await fn(); } catch(e) { notify(e.message||"操作失败，请稍后重试"); }
  finally { button.disabled=false; }
}
function showShare() { $("showCode").textContent=state.ownerCode;screen("sharePage"); }
async function init() {
  if(!window.isSecureContext||!window.crypto?.subtle||!window.indexedDB){notify("此设备不支持安全存储，请使用 Safari 打开");return;}
  try { db=await openDb();state=await load(); }
  catch { notify("本机数据无法读取，请检查 Safari 数据设置");return; }
  home();
  if("serviceWorker" in navigator)navigator.serviceWorker.register("/ios/sw.js",{scope:"/ios/"}).catch(()=>{});
}
$("toUser").onclick=()=>{ $("share").value=state.code||"";screen("user"); };
$("toOwner").onclick=()=>{
  ownerDraft=state.ownerCode&&state.ownerToken?{code:state.ownerCode,token:state.ownerToken}:{code:random(),token:random()};
  $("username").value=state.username||"";$("password").value=state.password||"";
  $("existingShare").hidden=!state.ownerCode||!state.ownerUploaded;screen("owner");
};
document.querySelectorAll(".back").forEach(el=>el.onclick=home);
$("login").onclick=()=>{if(!state.username||!state.password){notify("请先同步账号，或由账户拥有者上传");return;}$("maskedUser").textContent=state.username;screen("connectPage");};
$("copyUser").onclick=()=>copy(state.username);
$("copyPass").onclick=()=>copy(state.password);
$("copyCode").onclick=()=>copy(state.ownerCode);
$("existingShare").onclick=showShare;
$("clear").onclick=async()=>{if(confirm("清除本机保存的账号和上传权限？清除后将无法用此设备更新之前的分享码。")){try{await save({});home();notify("本机数据已清除");}catch{notify("本机数据清除失败");}}};
$("userForm").onsubmit=event=>{event.preventDefault();action(event.currentTarget,async()=>{
  const code=$("share").value.trim();un64(code);const response=await api(code,"GET");
  const credentials=await decryptShare(code,response.blob);
  if(typeof credentials.username!=="string"||!credentials.username.trim()||typeof credentials.password!=="string"||!credentials.password)throw new Error("账号内容为空");
  const keepOwner=state.ownerCode===code;
  const next={username:credentials.username,password:credentials.password,code};
  if(keepOwner){next.ownerCode=state.ownerCode;next.ownerToken=state.ownerToken;next.ownerUploaded=state.ownerUploaded;}
  await save(next);$("share").value="";home();notify("同步成功，账号已保存在本机");
});};
$("ownerForm").onsubmit=event=>{event.preventDefault();action(event.currentTarget,async()=>{
  const username=$("username").value.trim(),password=$("password").value;
  if(!username||!password)throw new Error("请填写校园网账号和密码");
  const {code,token}=ownerDraft;
  const next={...state,username,password,code,ownerCode:code,ownerToken:token,ownerUploaded:!!(state.ownerUploaded&&state.ownerCode===code)};
  // Persist the write token before the request so a timeout never strands an uploaded record.
  await save(next);
  const blob=await encryptShare(code,{username,password});
  await api(code,"PUT",{blob,writeToken:token});
  await save({...next,ownerUploaded:true});$("password").value="";showShare();
});};
init();
