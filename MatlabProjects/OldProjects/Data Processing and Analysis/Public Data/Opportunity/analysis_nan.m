res = [];
for sub = 1:4
    for sess = 1:6        
        res =[res; sub, sess, data(sub).session(sess).total_row, data(sub).session(sess).nan_count];        
    end
end

tc = sum(res(:, 3))
nc = sum(res(:, 4))
ratio = nc/tc*100