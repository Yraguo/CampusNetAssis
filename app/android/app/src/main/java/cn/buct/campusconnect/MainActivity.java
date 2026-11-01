package cn.buct.campusconnect;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends Activity {
    private static final String DEFAULT_ENDPOINT = "https://buct-campus-relay-237239.pages.dev";
    private final int blue=Color.rgb(0,113,227), ink=Color.rgb(28,32,42), muted=Color.rgb(111,119,132);
    private final int canvas=Color.rgb(246,247,250), line=Color.rgb(236,238,242);
    private LinearLayout root, body;
    private JSONObject state = new JSONObject();
    private WebView portal;
    private String ownerCode, ownerToken;
    private final SecureRandom random = new SecureRandom();

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(canvas);
        getWindow().setNavigationBarColor(canvas);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        loadState();
        home();
    }
    private int dp(float n){ return (int)(getResources().getDisplayMetrics().density*n+0.5f); }
    private GradientDrawable bg(int color, int radius){ GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));return d; }
    private android.graphics.drawable.Drawable touch(int color,int radius){
        return new android.graphics.drawable.RippleDrawable(android.content.res.ColorStateList.valueOf(0x220071e3),bg(color,radius),null);
    }
    private TextView text(String s,int size,int color,boolean bold){
        TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);
        t.setTypeface(Typeface.create(bold?"sans-serif-medium":"sans-serif",Typeface.NORMAL));t.setIncludeFontPadding(false);return t;
    }
    private void gap(LinearLayout l,int h){ View v=new View(this);l.addView(v,new LinearLayout.LayoutParams(1,dp(h))); }
    private void label(LinearLayout l,String s){TextView t=text(s,13,muted,true);l.addView(t);gap(l,8);}
    private EditText field(LinearLayout l,String hint,String val,boolean secret){
        EditText e=new EditText(this);e.setSingleLine(true);e.setTextSize(15);e.setTextColor(ink);e.setHintTextColor(0xffa3a9b2);e.setHint(hint);
        e.setPadding(dp(16),0,dp(16),0);e.setBackground(bg(0xfff5f6f8,14));e.setInputType(secret ? 129 : 1);
        e.setTypeface(Typeface.create("sans-serif",Typeface.NORMAL));if(val!=null)e.setText(val);
        l.addView(e,new LinearLayout.LayoutParams(-1,dp(54)));gap(l,19);return e;
    }
    private Button button(LinearLayout l,String s,boolean primary,Runnable action){
        Button b=new Button(this);b.setText(s);b.setTextSize(15);b.setAllCaps(false);b.setTextColor(primary?Color.WHITE:blue);
        b.setTypeface(Typeface.create("sans-serif-medium",Typeface.NORMAL));b.setBackground(touch(primary?blue:0xfff0f5fc,15));
        b.setMinHeight(0);b.setMinimumHeight(0);l.addView(b,new LinearLayout.LayoutParams(-1,dp(52)));gap(l,10);
        b.setOnClickListener(v->action.run());return b;
    }
    private class Symbol extends View {
        private final String kind; private final int color;
        Symbol(String kind,int color){super(MainActivity.this);this.kind=kind;this.color=color;}
        @Override protected void onDraw(android.graphics.Canvas canvas){
            super.onDraw(canvas); float scale=Math.min(getWidth(),getHeight())/48f;canvas.save();
            canvas.translate((getWidth()-48*scale)/2,(getHeight()-48*scale)/2);canvas.scale(scale,scale);
            android.graphics.Paint p=new android.graphics.Paint(3);p.setColor(color);p.setStyle(android.graphics.Paint.Style.STROKE);p.setStrokeWidth(2.6f);p.setStrokeCap(android.graphics.Paint.Cap.ROUND);p.setStrokeJoin(android.graphics.Paint.Join.ROUND);
            if(kind.equals("wifi")){
                android.graphics.Path a=new android.graphics.Path();a.moveTo(8,19);a.cubicTo(17,11,31,11,40,19);canvas.drawPath(a,p);
                android.graphics.Path b=new android.graphics.Path();b.moveTo(13,25);b.cubicTo(19,19,29,19,35,25);canvas.drawPath(b,p);
                android.graphics.Path c=new android.graphics.Path();c.moveTo(19,31);c.cubicTo(22,28,26,28,29,31);canvas.drawPath(c,p);
                p.setStyle(android.graphics.Paint.Style.FILL);canvas.drawCircle(24,36,2,p);
            }else if(kind.equals("person")){
                canvas.drawCircle(24,17,6,p);canvas.drawRoundRect(12,27,36,40,7,7,p);
            }else if(kind.equals("key")){
                canvas.drawCircle(18,20,6,p);canvas.drawLine(22,24,34,36,p);canvas.drawLine(30,32,33,29,p);canvas.drawLine(34,36,37,33,p);
            }else if(kind.equals("check")){
                android.graphics.Path a=new android.graphics.Path();a.moveTo(11,25);a.lineTo(21,34);a.lineTo(37,15);canvas.drawPath(a,p);
            }
            canvas.restore();
        }
    }
    private View tileIcon(String kind,int color,int background){
        android.widget.FrameLayout tile=new android.widget.FrameLayout(this);tile.setBackground(bg(background,14));
        Symbol icon=new Symbol(kind,color);android.widget.FrameLayout.LayoutParams ip=new android.widget.FrameLayout.LayoutParams(dp(29),dp(29),Gravity.CENTER);tile.addView(icon,ip);return tile;
    }
    private void shell(String title,String subtitle){
        portal=null;root=new LinearLayout(this);root.setOrientation(1);root.setBackgroundColor(canvas);setContentView(root);
        ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);scroll.setClipToPadding(false);root.addView(scroll);
        body=new LinearLayout(this);body.setOrientation(1);body.setPadding(dp(22),dp(20),dp(22),dp(30));scroll.addView(body);
        LinearLayout nav=new LinearLayout(this);nav.setOrientation(0);nav.setGravity(Gravity.CENTER_VERTICAL);
        if(title.equals("校园网，一键连接")){
            View mark=tileIcon("wifi",Color.WHITE,blue);nav.addView(mark,new LinearLayout.LayoutParams(dp(38),dp(38)));
            LinearLayout names=new LinearLayout(this);names.setOrientation(1);names.setPadding(dp(11),0,0,0);
            names.addView(text("校园网助手",15,ink,true));gap(names,3);TextView en=text("BUCT  ·  CAMPUS NETWORK",9,muted,true);en.setLetterSpacing(.07f);names.addView(en);
            nav.addView(names,new LinearLayout.LayoutParams(0,-2,1));
            TextView badge=text("BUCT",10,blue,true);badge.setGravity(Gravity.CENTER);badge.setBackground(bg(0xffe8f1fd,11));nav.addView(badge,new LinearLayout.LayoutParams(dp(52),dp(28)));
        }else{
            TextView back=text("‹  返回",16,blue,true);back.setGravity(Gravity.CENTER_VERTICAL);back.setMinHeight(dp(44));back.setOnClickListener(v->home());nav.addView(back);
        }
        body.addView(nav);gap(body,title.equals("校园网，一键连接")?30:24);
        TextView heading=text(title.equals("校园网，一键连接")?"你好，校园网":title,title.equals("校园网，一键连接")?30:28,ink,true);heading.setLetterSpacing(-.025f);body.addView(heading);gap(body,9);
        TextView sub=text(title.equals("校园网，一键连接")?"连接校园网络，从这里开始。":subtitle,14,muted,false);sub.setLineSpacing(dp(4),1);body.addView(sub);gap(body,24);
    }
    private LinearLayout card(){
        LinearLayout c=new LinearLayout(this);c.setOrientation(1);c.setPadding(dp(20),dp(20),dp(20),dp(10));c.setBackground(bg(Color.WHITE,22));body.addView(c);gap(body,16);return c;
    }
    private void accountRow(LinearLayout card,String icon,String title,String detail,Runnable action,boolean divider){
        LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER_VERTICAL);row.setOrientation(LinearLayout.HORIZONTAL);row.setBackground(touch(Color.WHITE,12));row.setPadding(0,dp(11),0,dp(11));row.setOnClickListener(v->action.run());
        row.addView(tileIcon(icon,blue,0xffeaf3ff),new LinearLayout.LayoutParams(dp(44),dp(44)));
        LinearLayout copy=new LinearLayout(this);copy.setPadding(dp(13),0,dp(8),0);copy.setOrientation(1);copy.addView(text(title,15,ink,true));gap(copy,5);copy.addView(text(detail,12,muted,false));row.addView(copy,new LinearLayout.LayoutParams(0,-2,1));
        TextView arrow=text("›",25,0xffb8bec7,false);row.addView(arrow);
        card.addView(row,new LinearLayout.LayoutParams(-1,-2));
        if(divider){View rule=new View(this);rule.setBackgroundColor(line);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,dp(1));lp.leftMargin=dp(57);card.addView(rule,lp);}
    }
    private String get(String k){return state.optString(k,"");}
    private String endpoint(){
        String saved=get("endpoint");
        String normalized=saved.endsWith("/") ? saved.substring(0,saved.length()-1) : saved;
        if(normalized.isEmpty() || normalized.equalsIgnoreCase("https://buct-campus-connect.237239.workers.dev") || normalized.equalsIgnoreCase("https://buct-campus-connect.237239buct.workers.dev")) return DEFAULT_ENDPOINT;
        return saved;
    }
    private void notice(String s){Toast.makeText(this,s,Toast.LENGTH_LONG).show();}
    private void home(){
        shell("校园网，一键连接","");
        LinearLayout c=card();
        LinearLayout top=new LinearLayout(this);top.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout titles=new LinearLayout(this);titles.setOrientation(1);
        TextView small=text("校园网络",12,muted,true);small.setLetterSpacing(.04f);titles.addView(small);gap(titles,8);
        titles.addView(text(get("user").isEmpty()?"准备连接":"随时可以连接",21,ink,true));top.addView(titles,new LinearLayout.LayoutParams(0,-2,1));
        top.addView(tileIcon("wifi",blue,0xffeaf3ff),new LinearLayout.LayoutParams(dp(48),dp(48)));c.addView(top);gap(c,14);
        TextView desc=text(get("user").isEmpty()?"先同步账号，再连接校园 Wi-Fi。":"账号已保存在本机，连接校园 Wi-Fi 后即可登录。",13,muted,false);desc.setLineSpacing(dp(3),1);c.addView(desc);gap(c,19);
        button(c,"一键登录校园网",true,()->openPortal());
        button(c,"打开 Wi-Fi 设置",false,()->startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)));
        TextView section=text("账号管理",17,ink,true);body.addView(section);gap(body,10);
        LinearLayout c2=card();c2.setPadding(dp(18),dp(3),dp(18),dp(3));
        accountRow(c2,"person","我是使用者","使用分享码同步账号",()->userPage(),true);
        accountRow(c2,"key","账户拥有者","上传账号并生成分享码",()->ownerPage(),false);
        if(!get("user").isEmpty()){
            button(body,"清除本机账号",false,()->new AlertDialog.Builder(this).setMessage("清除本机保存的账号、分享码及拥有者信息？").setNegativeButton("取消",null).setPositiveButton("清除",(d,w)->{state=new JSONObject();saveState();home();}).show());
        }
        TextView foot=text("首次同步需连接互联网，校园网认证前可能无法访问云端。",12,muted,false);foot.setLineSpacing(dp(4),1);foot.setPadding(dp(3),dp(4),dp(3),0);body.addView(foot);
    }
    private void userPage(){
        shell("同步共享账号","请先连接移动数据或其他可用网络，再输入账户拥有者提供的信息。");
        LinearLayout c=card();label(c,"Cloudflare 服务地址");
        EditText endpoint=field(c,"https://…workers.dev",endpoint(),false);
        label(c,"账号分享码");EditText code=field(c,"粘贴账户拥有者提供的分享码",get("code"),false);
        button(c,"同步并保存",true,()->{
            String url=endpoint.getText().toString().trim(), share=code.getText().toString().trim();
            try { validateEndpoint(url); byte[] key=un64(share);if(key.length!=32)throw new Exception(); }
            catch(Exception e){notice("请输入有效的 HTTPS 服务地址和分享码");return;}
            async(()->{
                byte[] key=un64(share);String id=hex(sha(key));
                JSONObject data=request(url,"GET",id,null,null);
                JSONObject credentials=new JSONObject(new String(decrypt(key,un64(data.getString("blob"))),StandardCharsets.UTF_8));
                String u=credentials.getString("username"), p=credentials.getString("password");
                if(u.trim().isEmpty()||p.isEmpty())throw new Exception("账号内容为空");
                runOnUiThread(()->{try{state.put("endpoint",url);state.put("code",share);state.put("user",u);state.put("pass",p);state.remove("ownerToken");state.remove("ownerCode");saveState();notice("同步成功，账号已保存在本机");home();}catch(Exception ignored){}});
            });
        });
        button(c,"返回",false,()->home());
        TextView t=text("分享码可解密账号密码，请仅向可信任的人提供。",13,muted,false);t.setLineSpacing(dp(3),1);body.addView(t);
    }
    private void ownerPage(){
        shell("账户拥有者","账号密码会先在手机上加密，再上传到你的 Cloudflare 服务。");
        ownerCode=get("ownerCode");ownerToken=get("ownerToken");
        if(ownerCode.isEmpty()||ownerToken.isEmpty()){ownerCode=b64(bytes(32));ownerToken=b64(bytes(32));}
        LinearLayout c=card();label(c,"Cloudflare 服务地址");EditText endpoint=field(c,"https://…workers.dev",endpoint(),false);
        label(c,"校园网账号");EditText user=field(c,"输入账号",get("user"),false);
        label(c,"校园网密码");EditText pass=field(c,"输入密码",get("pass"),true);
        TextView info=text("分享码在首次上传成功后生成。重新上传会更新该分享码对应的账号。",13,muted,false);info.setLineSpacing(dp(3),1);c.addView(info);gap(c,16);
        button(c,"加密并上传",true,()->{
            String url=endpoint.getText().toString().trim(), u=user.getText().toString().trim(),p=pass.getText().toString();
            try{validateEndpoint(url);if(u.isEmpty()||p.isEmpty())throw new Exception();}catch(Exception e){notice("请填写 HTTPS 服务地址、账号和密码");return;}
            final String code=ownerCode, token=ownerToken;
            async(()->{
                byte[] key=un64(code);String id=hex(sha(key));
                JSONObject payload=new JSONObject();payload.put("username",u);payload.put("password",p);
                JSONObject data=new JSONObject();data.put("blob",b64(encrypt(key,payload.toString().getBytes(StandardCharsets.UTF_8))));data.put("writeToken",token);
                request(url,"PUT",id,data,token);
                runOnUiThread(()->{try{state.put("endpoint",url);state.put("user",u);state.put("pass",p);state.put("ownerCode",code);state.put("ownerToken",token);state.put("code",code);saveState();sharePage(url,code);}catch(Exception ignored){}});
            });
        });
        if(!get("ownerCode").isEmpty())button(c,"查看已上传的分享码",false,()->sharePage(endpoint(),get("ownerCode")));
        button(c,"返回",false,()->home());
    }
    private void sharePage(String url,String code){
        shell("账号已上传","请将服务地址和分享码，通过可信的方式发给使用者。");
        LinearLayout c=card();label(c,"服务地址");TextView a=text(url,14,ink,false);a.setTextIsSelectable(true);c.addView(a);gap(c,18);
        label(c,"账号分享码");TextView b=text(code,15,ink,true);b.setTextIsSelectable(true);c.addView(b);gap(c,18);
        button(c,"复制服务地址和分享码",true,()->{((ClipboardManager)getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("校园网分享信息",url+"\n"+code));notice("已复制，请仅发送给可信任的人");});
        button(c,"返回首页",false,()->home());
        TextView t=text("持有分享码的人可以取回校园网密码。修改校园网密码后，请重新上传。",13,muted,false);t.setLineSpacing(dp(3),1);body.addView(t);
    }
    private void openPortal(){
        if(get("user").isEmpty()||get("pass").isEmpty()){notice("请先同步或上传账号");return;}
        final String username=get("user"),password=get("pass");
        root=new LinearLayout(this);root.setOrientation(1);root.setBackgroundColor(Color.WHITE);setContentView(root);
        TextView bar=text("‹  返回首页          校园网认证",17,ink,true);bar.setPadding(dp(18),dp(16),dp(12),dp(16));bar.setOnClickListener(v->home());root.addView(bar);
        TextView tip=text("正在打开学校认证页面，请确认 Wi-Fi 已连接。",13,muted,false);tip.setPadding(dp(18),dp(4),dp(18),dp(10));root.addView(tip);
        portal=new WebView(this);root.addView(portal,new LinearLayout.LayoutParams(-1,0,1));
        portal.getSettings().setJavaScriptEnabled(true);portal.getSettings().setDomStorageEnabled(true);portal.getSettings().setSavePassword(false);
        portal.setWebViewClient(new WebViewClient(){

            private boolean allowed(Uri u){return "https".equalsIgnoreCase(u.getScheme())&&"tree.buct.edu.cn".equalsIgnoreCase(u.getHost())&&(u.getPort()==-1||u.getPort()==443);}
            @Override public boolean shouldOverrideUrlLoading(WebView view,WebResourceRequest req){return !allowed(req.getUrl());}
            @Override public void onPageFinished(WebView view,String url){
                if(!allowed(Uri.parse(url)))return; String path=Uri.parse(url).getPath(); if(path==null || !(path.equals("/srun_portal_pc") || path.equals("/srun_portal_phone")))return;

                String js="(function(){var tries=0;function go(){var u=document.getElementById('username'),p=document.getElementById('password'),b=document.getElementById('login-account');if(u&&p&&b){u.value="+JSONObject.quote(username)+";p.value="+JSONObject.quote(password)+";u.dispatchEvent(new Event('input',{bubbles:true}));p.dispatchEvent(new Event('input',{bubbles:true}));b.click();return 'ok'}if(++tries<30)setTimeout(go,500);else document.body.insertAdjacentHTML('afterbegin','<div style=\"padding:15px;background:#fff2ce\">未找到登录表单，请手动操作或返回重试。</div>');}go()})()";
                view.evaluateJavascript(js,null);
            }
            @Override public void onReceivedError(WebView v,WebResourceRequest req,android.webkit.WebResourceError error){if(req.isForMainFrame())tip.setText("无法打开认证页，请确认已连接校园 Wi-Fi 后重试。");}
        });
        portal.loadUrl("https://tree.buct.edu.cn/srun_portal_pc?ac_id=20&theme=basic");
    }
    @Override public void onBackPressed(){if(portal!=null){portal.destroy();portal=null;home();}else home();}

    private interface Work { void run() throws Exception; }
    private void async(Work work){notice("正在处理，请稍候…");new Thread(()->{try{work.run();}catch(Exception e){runOnUiThread(()->notice("操作失败："+(e.getMessage()==null?"请检查网络、地址和分享码":e.getMessage())));}}).start();}
    private void validateEndpoint(String s) throws Exception {Uri u=Uri.parse(s);if(!"https".equalsIgnoreCase(u.getScheme())||u.getHost()==null||u.getUserInfo()!=null||u.getQuery()!=null||u.getFragment()!=null)throw new Exception();}
    private JSONObject request(String base,String method,String id,JSONObject data,String token) throws Exception {
        while(base.endsWith("/"))base=base.substring(0,base.length()-1);
        HttpURLConnection c=(HttpURLConnection)new URL(base+"/v1/"+id).openConnection();
        c.setInstanceFollowRedirects(false);c.setRequestMethod(method);c.setConnectTimeout(15000);c.setReadTimeout(15000);c.setRequestProperty("Accept","application/json");
        if(data!=null){c.setDoOutput(true);c.setRequestProperty("Content-Type","application/json");c.getOutputStream().write(data.toString().getBytes(StandardCharsets.UTF_8));}
        int status=c.getResponseCode();if(status<200||status>=300)throw new Exception("服务返回 HTTP "+status+"；请检查服务地址与分享码");
        try(java.io.InputStream in=c.getInputStream();java.io.ByteArrayOutputStream out=new java.io.ByteArrayOutputStream()){byte[] buf=new byte[2048];int n;while((n=in.read(buf))!=-1){out.write(buf,0,n);if(out.size()>16384)throw new Exception("响应过大");}return new JSONObject(out.toString("UTF-8"));}finally{c.disconnect();}
    }
    private byte[] bytes(int n){byte[] b=new byte[n];random.nextBytes(b);return b;}
    private static String b64(byte[] b){return Base64.encodeToString(b,Base64.URL_SAFE|Base64.NO_WRAP|Base64.NO_PADDING);}
    private static byte[] un64(String s){return Base64.decode(s,Base64.URL_SAFE|Base64.NO_WRAP|Base64.NO_PADDING);}
    private static byte[] sha(byte[] b)throws Exception{return MessageDigest.getInstance("SHA-256").digest(b);}
    private static String hex(byte[] b){StringBuilder s=new StringBuilder();for(byte v:b)s.append(String.format(java.util.Locale.ROOT,"%02x",v&255));return s.toString();}
    private byte[] encrypt(byte[] key,byte[] data)throws Exception{byte[] iv=bytes(12);Cipher c=Cipher.getInstance("AES/GCM/NoPadding");c.init(Cipher.ENCRYPT_MODE,new SecretKeySpec(key,"AES"),new GCMParameterSpec(128,iv));byte[] enc=c.doFinal(data),out=new byte[iv.length+enc.length];System.arraycopy(iv,0,out,0,iv.length);System.arraycopy(enc,0,out,iv.length,enc.length);return out;}
    private byte[] decrypt(byte[] key,byte[] data)throws Exception{if(data.length<29)throw new Exception("数据格式错误");Cipher c=Cipher.getInstance("AES/GCM/NoPadding");c.init(Cipher.DECRYPT_MODE,new SecretKeySpec(key,"AES"),new GCMParameterSpec(128,data,0,12));return c.doFinal(data,12,data.length-12);}
    private SecretKey deviceKey()throws Exception{
        KeyStore ks=KeyStore.getInstance("AndroidKeyStore");ks.load(null);String alias="campus-connect-key";
        if(!ks.containsAlias(alias)){KeyGenerator gen=KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore");gen.init(new KeyGenParameterSpec.Builder(alias,KeyProperties.PURPOSE_ENCRYPT|KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).build());gen.generateKey();}
        return (SecretKey)ks.getKey(alias,null);
    }
    private void saveState(){try{Cipher c=Cipher.getInstance("AES/GCM/NoPadding");c.init(Cipher.ENCRYPT_MODE,deviceKey());byte[] iv=c.getIV(),enc=c.doFinal(state.toString().getBytes(StandardCharsets.UTF_8));getPreferences(MODE_PRIVATE).edit().putString("iv",b64(iv)).putString("data",b64(enc)).apply();}catch(Exception e){notice("本机加密保存失败");}}
    private void loadState(){try{String iv=getPreferences(MODE_PRIVATE).getString("iv",null),data=getPreferences(MODE_PRIVATE).getString("data",null);if(iv==null||data==null)return;Cipher c=Cipher.getInstance("AES/GCM/NoPadding");c.init(Cipher.DECRYPT_MODE,deviceKey(),new GCMParameterSpec(128,un64(iv)));state=new JSONObject(new String(c.doFinal(un64(data)),StandardCharsets.UTF_8));}catch(Exception e){state=new JSONObject();}}
}
