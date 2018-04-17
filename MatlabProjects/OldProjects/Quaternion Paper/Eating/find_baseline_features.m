
baseline_features = [];
for s = 1:12
    
    d = data(s).data;
    Rz = d(:, 2:4);
    accel = d(:, 8:10); 
    a = data(s).annots(:,1);    
    sample_count = length(accel);
    
    feature_count = length(150:150:sample_count-150)     
    f = zeros(feature_count, 54*2+1);
    for i=1:feature_count
        ix = i*150;
        label = sum(a<ix+150 & a>ix-149);        
        if label>0
            label = 1;
        else
            label = 0;
        end

        f3 = get_features(accel(ix-149:ix+150, :));
        f4 = get_features(Rz(ix-149:ix+150, :));        
        f(i, :) = [f3, f4, label];        
    end
    
    baseline_features(s).features = f;    
    
end
save('baseline_features', 'baseline_features');
