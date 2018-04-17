load('res');
downsample_factor = 6;
k = 20;
options = statset('MaxIter',500);

for test_sub = 1:5    
    data_train = [];
    for train_sub = 1:5
        if train_sub == test_sub
            continue;
        end 
        
        for sess=1:2
            d = data(train_sub).session(sess).grav;               
            d = d(1:downsample_factor:end, 2:4);            
            data_train = [data_train; d];
        end
    end
    size(data_train)
    x = data_train(:,1);
    data_train = data_train(x<=0, :);    
    data_train = normalize(data_train); 
    size(data_train)
    
    fprintf('Processing subject %d\n', test_sub);    
    [idx, C, sumd, D] = kmedoids(data_train, k);
    res(test_sub).train_cluster.idx = idx;
    res(test_sub).train_cluster.C = C;
    res(test_sub).train_cluster.D = D;
    
end

save('res','res');