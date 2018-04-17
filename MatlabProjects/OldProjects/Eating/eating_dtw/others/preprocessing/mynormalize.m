function res = mynormalize(d)
    m = sqrt(sum(d.*d,2));
    res= d./[m, m, m];    
end


