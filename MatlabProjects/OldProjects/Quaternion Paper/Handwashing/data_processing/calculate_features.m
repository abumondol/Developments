function f = calculate_features(d, window_size, slide)
    sample_count = length(d);    
    d = d(:, 4:12);
    f_count = floor((sample_count - window_size)/slide);    
    
    f = zeros(f_count, 63);
    for i = 1:f_count
        s = (i-1)*slide+1;
        e = s + window_size-1;
        f(i, :) = calculate_features_one_window(d(s:e, :));        
    end
end