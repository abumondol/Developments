function f = get_features(d)    
    f = [mean(d), var(d), rms(d), kurtosis(d), skewness(d), my_corrcoef(d), prctile(d, 50), prctile(d, 25), prctile(d, 75)]; 
    d = d(1:end-1, :) - d(2:end, :);
    f = [f, mean(d), var(d), rms(d), kurtosis(d), skewness(d), my_corrcoef(d), prctile(d, 50), prctile(d, 25), prctile(d, 75)]; 
end

function c = my_corrcoef(d)
    c = cov(d);
    %c = corrcoef(d);
    c = [c(1,2), c(1,3), c(2,3)];
end