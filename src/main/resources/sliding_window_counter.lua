local currentKey = KEYS[1]
local previousKey = KEYS[2]

local limit = tonumber(ARGV[1])
local windowSizeMillis = tonumber(ARGV[2])
local now = tonumber(ARGV[3])

local currentWindowStart = math.floor(now / windowSizeMillis) * windowSizeMillis

local currentCount = tonumber(redis.call("GET", currentKey))
local previousCount = tonumber(redis.call("GET", previousKey))

if currentCount == nil then
    currentCount = 0
end

if previousCount == nil then
    previousCount = 0
end

local percentageIntoCurrentWindow = (now - currentWindowStart) / windowSizeMillis

local estimatedCount = (previousCount * (1 - percentageIntoCurrentWindow)) + currentCount
local allowed = 0

if estimatedCount < limit then
    currentCount = currentCount + 1
    redis.call("SET", currentKey, currentCount, "PX", windowSizeMillis * 2)
    allowed = 1
end

return allowed