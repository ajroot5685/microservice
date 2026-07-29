local stock = redis.call('HGET', KEYS[1], 'stock')

if stock == nil then
    return nil
end

if tonumber(stock) <= 0 then
    return nil
end


local user = redis.call('ZPOPMIN', KEYS[2], 1)

if user[1] == nil then
    return nil
end


redis.call('HINCRBY', KEYS[1], 'stock', -1)

return user[1]