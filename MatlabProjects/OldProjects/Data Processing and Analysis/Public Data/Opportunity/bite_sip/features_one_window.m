function f = features_one_window(d)        
        f = [mean(d), var(d), skewness(d), kurtosis(d), rms(d)];             
end

