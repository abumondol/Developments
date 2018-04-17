function f = features_one_window(d)    
    f = [];
    if baseline_features
        f = [mean(d), var(d), skewness(d), kurtosis(d), rms(d)];     
        return
    end
    
    cr = corrcoef(d);
    cor_xy = cr(1,2);
    cor_xz = cr(1,3);
    cor_yz = cr(2,3);
    cr = [cor_xy, cor_yz, cor_xz];    
    q0 = min(d);
    q1 = quantile(d, 0.25);
    q2 = quantile(d, 0.5);
    q3 = quantile(d, 0.75);
    q4 = max(d);
    f = [mean(d), var(d), skewness(d), kurtosis(d), rms(d), mcr(d), q0, q1, q2, q3, q4, q3-q1, cr];     
    
end

function res = mcr(d)
    len = size(d,1);
    d = d - repmat(mean(d), len, 1);
    res = mean(abs(diff(sign(d))));
end
