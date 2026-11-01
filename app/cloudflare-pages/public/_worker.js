export default {
  async fetch(request, env) {
    const url = new URL(request.url);
    if (url.pathname === "/" && request.method === "GET") {
      return Response.json({ service: "campus-connect-relay", status: "ok" }, {
        headers: { "Cache-Control": "no-store", "X-Content-Type-Options": "nosniff" }
      });
    }
    if ((url.pathname === "/ios/sw.js" || url.pathname === "/ios/shortcut.txt") && request.method === "GET") {
      const result = await env.ASSETS.fetch(request);
      const headers = new Headers(result.headers);
      headers.set("Cache-Control", "no-store");
      headers.set("X-Content-Type-Options", "nosniff");
      headers.set("Referrer-Policy", "no-referrer");
      return new Response(result.body, { status: result.status, headers });
    }
    if (url.pathname === "/ios" || url.pathname.startsWith("/ios/")) {
      return new Response("iPhone 网页版已停用，请使用快捷指令。", {
        status: 410, headers: { "Content-Type": "text/plain; charset=utf-8", "Cache-Control": "no-store" }
      });
    }
    if (!/^\/v1\/[a-f0-9]{64}$/.test(url.pathname) || url.search || !["GET", "PUT"].includes(request.method)) {
      return Response.json({ error: "not found" }, { status: 404, headers: { "Cache-Control": "no-store" } });
    }
    const result = await env.CAMPUS_WORKER.fetch(request);
    if (request.method === "GET" && request.headers.get("Origin") === "https://tree.buct.edu.cn") {
      const headers = new Headers(result.headers);
      headers.set("Access-Control-Allow-Origin", "https://tree.buct.edu.cn");
      headers.append("Vary", "Origin");
      return new Response(result.body, { status: result.status, headers });
    }
    return result;
  }
};