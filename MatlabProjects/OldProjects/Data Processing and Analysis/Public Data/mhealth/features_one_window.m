function f = features_one_window(d)    
    cr = corrcoef(d);
    cor_xy = cr(1,2);
    cor_xz = cr(1,3);
    cor_yz = cr(2,3);
    cr = [cor_xy, cor_yz, cor_xz];    
    f = [mean(d), std(d), rms(d), mcr(d), cr];    
end

function res = mcr(d)
    len = size(d,1);
    d = d - repmat(mean(d), len, 1);
    res = mean(abs(diff(sign(d))));
end
