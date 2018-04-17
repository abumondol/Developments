function res = my_normalize(d)
    len = size(d, 1);
    m = mean(d);
    s = std(d);
    
    m = repmat(m, len, 1);
    s = repmat(s, len, 1);
    
    res = (d-m)./s;
end