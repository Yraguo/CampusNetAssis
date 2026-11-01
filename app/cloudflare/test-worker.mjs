import fs from 'node:fs/promises';
import assert from 'node:assert/strict';
import {webcrypto} from 'node:crypto';

const source=await fs.readFile(new URL('./worker.js',import.meta.url),'utf8');
const {CredentialStore}=await import('data:text/javascript;base64,'+Buffer.from(source).toString('base64'));
let row=null, changes=0;
const sql={exec(q,...args){
 if(q.startsWith('CREATE')) return {};
 if(q.startsWith('SELECT blob')) return {toArray:()=>row?[{blob:row.blob}]:[]};
 if(q.startsWith('INSERT')) { const [hash,blob]=args; changes=0; if(!row){row={hash,blob};changes=1}else if(row.hash===hash){row.blob=blob;changes=1} return {}; }
 if(q.startsWith('SELECT changes')) return {one:()=>({n:changes})};
 throw Error(q);
}};
const store=new CredentialStore({storage:{sql}});
const call=(method,body)=>store.fetch(new Request('https://example.test/v1/record',{method,...(body?{body:JSON.stringify(body)}:{})}));
const token='a'.repeat(43), other='b'.repeat(43), blob='A'.repeat(50), blob2='B'.repeat(50);
assert.equal((await call('GET')).status,404);
assert.equal((await call('PUT',{writeToken:token,blob})).status,200);
assert.equal((await (await call('GET')).json()).blob,blob);
assert.equal((await call('PUT',{writeToken:other,blob:blob2})).status,403);
assert.equal((await (await call('GET')).json()).blob,blob);
assert.equal((await call('PUT',{writeToken:token,blob:blob2})).status,200);
assert.equal((await (await call('GET')).json()).blob,blob2);
assert.equal((await call('PUT',{writeToken:'short',blob})).status,400);
console.log('Worker credential create/read/update and unauthorized update checks passed.');
