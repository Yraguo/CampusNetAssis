// The service stores ciphertext only. The URL id is SHA-256 of the random share key.
export default {
  async fetch(request, env) {
    const url = new URL(request.url);
    const match = /^\/v1\/([a-f0-9]{64})$/.exec(url.pathname);
    if (!match || url.search || (request.method !== "GET" && request.method !== "PUT"))
      return reply({error:"not found"}, 404);
    const stub = env.CREDENTIALS.get(env.CREDENTIALS.idFromName(match[1]));
    return stub.fetch(request);
  }
};

function reply(body, status=200) {
  return Response.json(body, {status, headers:{"Cache-Control":"no-store","X-Content-Type-Options":"nosniff"}});
}

export class CredentialStore {
  constructor(state) {
    this.sql = state.storage.sql;
    this.sql.exec("CREATE TABLE IF NOT EXISTS credential (singleton INTEGER PRIMARY KEY CHECK(singleton=1), token_hash TEXT NOT NULL, blob TEXT NOT NULL)");
  }
  async fetch(request) {
    if (request.method === "GET") {
      const rows = this.sql.exec("SELECT blob FROM credential WHERE singleton=1").toArray();
      return rows.length ? reply({blob:rows[0].blob}) : reply({error:"not found"},404);
    }
    let json;
    try {
      const length = Number(request.headers.get("content-length") || 0);
      if (length > 8192) return reply({error:"too large"},413);
      const reader = request.body.getReader();
      let chunks = [], size = 0;
      while (true) {
        const {done,value} = await reader.read();
        if (done) break;
        size += value.length;
        if (size > 8192) { await reader.cancel(); return reply({error:"too large"},413); }
        chunks.push(value);
      }
      const bytes = new Uint8Array(size); let offset=0;
      for (const chunk of chunks) { bytes.set(chunk,offset); offset+=chunk.length; }
      json=JSON.parse(new TextDecoder().decode(bytes));
    } catch { return reply({error:"invalid request"},400); }
    if (typeof json.writeToken !== "string" || !/^[A-Za-z0-9_-]{43}$/.test(json.writeToken) ||
        typeof json.blob !== "string" || !/^[A-Za-z0-9_-]{39,6000}$/.test(json.blob))
      return reply({error:"invalid request"},400);
    const digest=await crypto.subtle.digest("SHA-256",new TextEncoder().encode(json.writeToken));
    const tokenHash=Array.from(new Uint8Array(digest), b=>b.toString(16).padStart(2,"0")).join("");
    // A synchronous SQL statement makes authorization and replacement atomic.
    this.sql.exec("INSERT INTO credential(singleton,token_hash,blob) VALUES(1,?,?) ON CONFLICT(singleton) DO UPDATE SET blob=excluded.blob WHERE credential.token_hash=excluded.token_hash", tokenHash,json.blob);
    if (this.sql.exec("SELECT changes() AS n").one().n !== 1) return reply({error:"forbidden"},403);
    return reply({ok:true});
  }
}
