function f = calculate_features_one_window(d)
    [~, ncol] = size(d);
    f=[];
    for i = 1:ncol
        f = [f, calculate_features_one_axis(d(:,i))];
        if mod(i,3)==0
            c= cov(d(:,i-2:i));
            f = [f, c(1,2), c(1,3), c(2,3)];
        end
    end
    
end

function f = calculate_features_one_axis(d)    
    f = [mean(d), var(d), rms(d), prctile(d, 25), prctile(d, 50), prctile(d, 75)]; 
end