function f = calculate_features_one_window(data, mid_point)
    f = [data(mid_point, :), mean(data), var(data), skewness(data), kurtosis(data)]; 
end