load('res');

for test_sub = 1:5    
    x_train = [];
    for train_sub = 1:5
        if train_sub == test_sub
            continue;
        end        
        x_train = [x_train; res(train_sub).annot_point_vals(:,2)];
    end
    
    x_test = res(test_sub).annot_point_vals(:,2);
    
    mu = mean(x_train);
    sigma = std(x_train);    
    mx = max(x_train);
    th_3sigma = mu + 3*sigma;
    th = min(th_3sigma, mx);
    res(test_sub).x_th = th;
    
    total = length(x_test);
    captured = sum(x_test <= th);
    
    fprintf('sub: %d, mx: %.2f, th_3sigma:%.2f, th: %.2f, total:%d, captured:%d\n', test_sub, mx, th_3sigma, th, total, captured);
    
end

save('res','res');