sub_count = length(data);
res = zeros(sub_count, 5);

for s = 1:sub_count
    %fprintf('Subject %d\n', s);        
    res(s,1) = segments(s).gt_eat_count;
    res(s,2) = segments(s).segs_eat_count;
    res(s,3) = segments(s).gt_drink_count;
    res(s,4) = segments(s).segs_drink_count;
    res(s,5) = segments(s).segs_neg_count;
    res(s,6) = res(s,1) - res(s,2);
    
end
sum(res)
save('res', 'res');