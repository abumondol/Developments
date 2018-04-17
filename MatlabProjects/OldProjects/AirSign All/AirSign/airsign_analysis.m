function airsign_analysis(signatures, grid, train_size)
    len = length(signatures);    
    model = struct;
    model.grid = grid;
    weight = find_axis_weight(model, train_size);
    model.weight = weight;
    
    all_mu = zeros(train_size, 1);
    for i = 1:train_size
        d = zeros(train_size, 1);
        for j = 1:train_size
            if i~=j
                a = min([i;j]);
                b = max([i;j]);                
                distances_all_axis = grid(a,b).dist;                
                %d(j,1) = mean(distances_all_axis);
                d(j,1) = sum(weight.*distances_all_axis);
            end            
        end        
        d(i,:)=[];        
        all_mu(i,1) = mean(d);
    end
    
    model.all_mu = all_mu;
    model.mu = mean(all_mu);
    model.sigma = std(all_mu);
    model.max_mu = max(all_mu);
    model.signatures = signatures(1:train_size);
    fprintf('mu=%0.3f, sigma=%0.3f, max_mu=%0.3f\n',model.mu, model.sigma, model.max_mu);
        
    count = 0;
    for i= train_size+1:len
        [result, d, model] = airsign_test_sign(model, signatures(i), train_size);
        count= count + result;
        fprintf('Airsign Signature %d: %d, distance:%.2f, New Mean:%.2f, New Sigma:%.2f\n',i, result, d, model.mu, model.sigma);
    end
    
    fprintf('Airsign Signature Total:%d, Accepted:%d\n\n', (len-train_size), count);
    
end