const http = require('http');
const httpProxy = require('http-proxy');
const Redis = require('ioredis');

const redisUrl = process.env.REDIS_URL || 'redis://redis-service:6379';

const redis = new Redis(redisUrl, {
    maxRetriesPerRequest: null,
    enableReadyCheck: true,
    retryStrategy: (times) => {
        const delay = Math.min(times * 50, 1000); // Back off delay
        console.log(`Redis connection attempt ${times} failed. Retrying in ${delay}ms...`);
        return delay;
    },
});

redis.on('connect', () => {
    console.log('Connected to Redis');
});

redis.on('error', (err) => {
    console.error('Redis connection error:', err);
});

const proxy = httpProxy.createProxyServer({
    ws: true,
    xfwd: true,
    changeOrigin: true
});

async function getTarget(hostname) {
    try {
        const targetIp = await redis.get(`route:${hostname}`);
        if (!targetIp) {
            return null;
        }
        return targetIp;
    } catch (error) {
        console.error('Error getting target:', error);
        return null;
    }
}

function getTargetUrl(ip) {
    return ip.includes(':') ? `http://${ip}` : `http://${ip}:5173`;
}

const server = http.createServer(async (req, res) => {
    const rawHost = req.headers.host || '';
    const hostname = rawHost.split(':')[0];

    const targetIp = await getTarget(hostname);

    if (!targetIp) {
        res.writeHead(404, { 'Content-Type': 'text/plain' });
        res.end('Preview not found for hostname: ' + hostname);
        return;
    }

    const targetUrl = getTargetUrl(targetIp);
    console.log('HTTP Proxy Request:', req.method, req.url, '->', targetUrl);

    proxy.web(req, res, { target: targetUrl }, (e) => {
        console.error('HTTP Proxy Error:', e);
        if (!res.headersSent) {
            res.writeHead(502, { 'Content-Type': 'text/plain' });
            res.end('Vite Server Unavailable');
        }
    });
});

server.on('upgrade', async (req, socket, head) => {
    const rawHost = req.headers.host || '';
    const hostname = rawHost.split(':')[0];

    const targetIp = await getTarget(hostname);

    if (!targetIp) {
        socket.destroy();
        return;
    }

    const target = getTargetUrl(targetIp);
    console.log('WebSocket Upgrade Request:', req.method, req.url, '->', target);

    proxy.ws(req, socket, head, { target: target }, (e) => {
        console.error('WebSocket Upgrade Error:', e);
        socket.destroy();
    });
});

server.listen(80, () => {
    console.log('Wildcard Proxy server listening on port 80');
});
