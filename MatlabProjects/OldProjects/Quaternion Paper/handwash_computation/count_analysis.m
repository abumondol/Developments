dr = 0;
for s = 1:10
    %d = data(s).eat_data;    
    %dr = dr + d(:) - d(1,1);
    
    d = data(s).annot_times;
    d = d(:, 2) - d(:,1);    
    dr = dr + sum(d);
end

dr = dr/52
dr = dr/60