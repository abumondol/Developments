function f = get_features(data)
    d = diff(data);
    d = [d; d(end, :)];
    f = [get_features_2(data), get_features_2(d)];
end

function f = get_features_2(data)
    len = length(data);
    d = cell(8,1); 
    d{1} = data(1:len/4, :);
    d{2} = data(len/4+1:len/2, :);
    d{3} = data(len/2+1:3*len/4, :);
    d{4} = data(3*len/4:len, :);
    d{5} = data(1:len/2, :);
    d{6} = data(len/2+1:len, :);
    d{7} = data(len/4+1:3*len/4, :);
    d{8} = data;
    f = [];
    cell_count = length(d);
    for i = 1:cell_count
        f = [f, get_features_sub_window(d{i})]; 
    end    
end

function f = get_features_sub_window(d)
    x = d(:,1);
    [~, I] = min(x);  
    f = [d(I, :), min(d(:,2:3)), max(d), mean(d), var(d), rms(d), kurtosis(d), skewness(d), my_corrcoef(d)]; 
end

function c = my_corrcoef(d)
    c = corrcoef(d);
    c = [c(1,2), c(1,3), c(2,3)];
end
