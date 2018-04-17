sub_count = length(data);

for s =1:sub_count
    %fprintf('Subject %d\n', s);    
    segs_mp = segments(s).segs_by_minx(:,1);
    
    res1 = zeros(length(segs_mp), 2);    
    res2 = zeros(length(segs_mp), 2);    
    a = data(s).annots;    
    if isempty(a)
        segments(s).segs_by_minx = [segments(s).segs_by_minx, res1, res2];        
        continue
    end
    
    a = a(a(:,2)<1000, :);        
    len = length(a);
    for i=1:len        
        p = a(i,3);
        label = a(i,2);       
        
        [m, I] = min(abs(segs_mp-p));        
        if res1(I, 1)==0 || res1(I, 2) > m % m = abs(mp(I)-p)
            res1(I, 2) = abs(segs_mp(I)-p);                    
        end
        res1(I, 1) = label;
    end
    
    len = length(segs_mp);    
    annot_indices = a(:, 3);
    for i=1:len        
        p = segs_mp(i, 1);        
        [m, I] = min(abs(annot_indices - p));
        
        label = a(I,2);
        res2(i, 1) = label;
        res2(i, 2) = m;       
    end   
    
    segments(s).segs_by_minx = [segments(s).segs_by_minx, res1, res2];
end

save('segments', 'segments');
fprintf('Find min point labels done\n');

