sub_count = length(data);

features = [];
for s =1:sub_count
    fprintf(' Features Subject %d\n', s);  
    accel = data(s).accel(:, 2:4);
    segs = segments(s).segs_selected;    
    count = size(segs, 1);
    X = zeros(count, 9);    
    Y = zeros(count, 1); 
    
    for i=1:count
        ix = segs(i, 1);        
        %d = accel(ix-left_size+1:ix+right_size-1, :);
        %X(i,:) = calculate_features(d, left_size);        
        X(i,1:3) = accel(ix, :);        
        X(i,4) = sum(var(accel(ix- 8:ix+ 7, :)), 2);
        X(i,5) = sum(var(accel(ix-16:ix+15, :)), 2);
        X(i,6) = sum(var(accel(ix-24:ix+23, :)), 2);
        X(i,7) = sum(var(accel(ix-32:ix+31, :)), 2);
        X(i,8) = sum(var(accel(ix-40:ix+39, :)), 2);
        X(i,9) = sum(var(accel(ix-48:ix+47, :)), 2);
        
        if segs(i,3) > 0
            Y(i, 1) = 1;
        end        
    end
    
    features(s).X = X;
    features(s).Y = Y;    
end

save('features', 'features');
fprintf('Featrues calculation done\n');

