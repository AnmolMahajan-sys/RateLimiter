local tokensKey = KEYS[1]
local timestampKey = KEYS[2]

local capacity = tonumber(ARGV[1]);
local refillRatePerSecond = tonumber(ARGV[2])
local now = tonumber(ARGV[3])

local currentTokens = tonumber(redis.call("GET",tokensKey))
local lastRefill =  tonumber(redis.call("GET",timestampKey))

if currentTokens == nil then
    currentTokens = capacity
end

if lastRefill == nil then
    lastRefill = now
end

local secondsElapsed = (now - lastRefill)/1000.0
currentTokens = math.min(capacity, currentTokens + secondsElapsed * refillRatePerSecond)

local allowed = 0

if currentTokens >= 1 then
    currentTokens = currentTokens-1
    allowed = 1
end

redis.call("SET", tokensKey,tostring(currentTokens))
redis.call("SET", timestampKey,tostring(now))

return allowed
