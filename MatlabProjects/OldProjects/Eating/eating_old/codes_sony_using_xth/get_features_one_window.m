function f = get_features_one_window(w)    
    [~, min_ind] = min(w(:,1));
    wl = w(1:min_ind, :);
    wr = w(min_ind:end, :);    
    %fprintf('%d, %d\n',length(w), min_ind); 
    f = [min(w), max(w)-min(w), length(w), length(wl), length(wr)];
    f = [f, cal_features(wl), cal_features(wr)];               
end

function f = cal_features(d)   
    cv = cov(d);
    cov_xy = cv(1,2);
    cov_xz = cv(1,3);
    cov_yz = cv(2,3);
    cv = [cov_xy, cov_yz, cov_xz];   
    
    f = [mean(d), std(d), skewness(d), kurtosis(d), cv];    
end

function d = first_derivative(d)
    d = d(2:end, :) - d(1:end-1, :);
end