local logKey=KEYS[1]

local limit=tonumber(ARGV[1])
local windowSizeMillis=tonumber(ARGV[2])
local now=tonumber(ARGV[3])

local windowStart = now-windowSizeMillis

redis.call("ZREMRANGEBYSCORE",logKey,0,windowStart)

local count = redis.call("ZCARD",logKey)

local allowed=0
if count < limit then
    redis.call("ZADD",logKey,now,now)
    allowed = 1
end

redis.call("EXPIRE",logKey,math.ceil(windowSizeMillis/1000)+1)

return allowed