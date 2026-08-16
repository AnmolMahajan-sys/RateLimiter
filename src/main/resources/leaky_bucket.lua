local queueKey=KEYS[1]
local timestampKey=KEYS[2]

local capacity=tonumber(ARGV[1])
local leakRatePerSecond=tonumber(ARGV[2])
local now=tonumber(ARGV[3])

local queueSize=tonumber(redis.call("GET",queueKey))
local lastLeak=tonumber(redis.call("GET",timestampKey))

if queueSize==nil then
    queueSize=0
end

if lastLeak==nil then
    lastLeak=now
end

local secondsElapsed =(now-lastLeak)/1000.0
local leaked=secondsElapsed * leakRatePerSecond

queueSize = math.max(0,queueSize-leaked)

local allowed=0

if queueSize < capacity then
    queueSize = queueSize+1
    allowed = 1
end

redis.call("SET",queueKey,tostring(queueSize))
redis.call("SET",timestampKey,tostring(now))

return allowed