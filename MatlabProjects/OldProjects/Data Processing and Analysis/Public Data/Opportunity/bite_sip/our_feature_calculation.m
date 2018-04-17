function features = our_feature_calculation(accel, window_indices)
    wix = window_indices;
    wcount = size(wix,1);    
    
    col_count = length(features_one_window(accel(1:10, :)));
    
    features = zeros(wcount, col_count);
    for i=1:wcount
        s = wix(i, 1);
        e = wix(i, 2);
        %m = round((s+e)/2);
        features(i,:) = features_one_window(accel(s:e, :));
    end
end