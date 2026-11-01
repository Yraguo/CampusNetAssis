export default {
  async fetch(request, env) {
    const url = new URL(request.url);
    if (url.pathname === "/" && request.method === "GET") {
      return Response.json({ service: "campus-connect-relay", status: "ok" }, {
        headers: { "Cache-Control": "no-store", "X-Content-Type-Options": "nosniff" }
      });
    }
    if (url.pathname === "/ios" && request.method === "GET")
      return Response.redirect(new URL("/ios/", url), 308);
    if (url.pathname.startsWith("/ios/") && request.method === "GET") {
      const response = await env.ASSETS.fetch(request);
      const headers = new Headers(response.headers);
      headers.set("Cache-Control", "no-store");
      headers.set("X-Content-Type-Options", "nosniff");
      headers.set("Content-Security-Policy", "default-src 'self'; connect-src 'self'; script-src 'self'; style-src 'self'; img-src 'self'; object-src 'none'; base-uri 'none'; frame-ancestors 'none'; form-action 'none'");
      headers.set("Referrer-Policy", "no-referrer");
      return new Response(response.body, {status:response.status, headers});
    }
    if (!/^\/v1\/[a-f0-9]{64}$/.test(url.pathname) || url.search || !["GET", "PUT"].includes(request.method)) {
      return Response.json({ error: "not found" }, { status: 404, headers: { "Cache-Control": "no-store" } });
    }
    return env.CAMPUS_WORKER.fetch(request);
  }
};