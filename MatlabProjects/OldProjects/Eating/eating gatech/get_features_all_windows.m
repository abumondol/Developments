function features = get_features_all_windows(a, t, windows)
    window_count = size(windows, 1);
    features = [];    
    for i =1:window_count
        f = get_features_one_window(a, t, windows(i,:));
        features = [features; f];
    end
    
end

function f = get_features_one_window(a, t, window)
    s = window(1,2);
    e = window(1,3);
    w = a(s:e, :);
    
    mid = round((s+e)/2, 0);        
    %[mn, ind] = min(w(:,1));
    %mid = s + ind - 1;
    wl = a(s:mid, :);
    wr = a(mid+1:e, :);
    wl_fd = first_derivative(wl);
    wr_fd = first_derivative(wr);    
    
    f = [window(1,1), t(e) - t(s)]; %cut_point,  duration      
    f = [f, cal_features(w), cal_features(wl), cal_features(wr), cal_features(wl_fd), cal_features(wr_fd)];           
    f = [f, window(1, end)]; % label
end

function f = cal_features(d)
    cv = cov(d);
    cov_xy = cv(1,2);
    cov_xz = cv(1,3);
    cov_yz = cv(2,3);
    cv = [cov_xy, cov_yz, cov_xz];
    
    f = [mean(d), std(d), cv, min(d), range(d)];    
end

function d = first_derivative(data)
    d = data(2:end, :) - data(1:end-1, :);
end