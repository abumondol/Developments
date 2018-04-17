function [result, mu_s, model] = airsign_test_sign(model, s, train_size)
    weight = model.weight;

    ds = zeros(train_size, 8); 
    for i=1:train_size
      %[dist, dtw_path] = airsign_distance(model.signatures(i), s);
      [dist, dtw_path] = mauth_distance(model.signatures(i), s);
      ds(i,:) = dist';
    end
    
    mu_s = mean(ds*weight);    
    %mu_s = mean(mean(ds,2));
    
    if mu_s > min([model.max_mu; model.mu+3*model.sigma])
    %if mu_s >  model.mu+3*model.sigma
        result = 0;    
        return;
    end
    
    result = 1;
    
    %======== update template ==========
    
    all_mu2 = model.all_mu;
    for i=1:train_size
        dist_with_s = mean(ds(i,:), 2);
        %dist_with_s = ds(i,:)*weight;
        total_dist_with_other_train = all_mu2(i,1)*(train_size-1);
        all_mu2(i,1) = (total_dist_with_other_train+dist_with_s)/train_size;
    end
    
    [m, i] = max(all_mu2); % i will be removed
    
    i = ceil(rand*train_size)
            
    for j =1:train_size
        if i~=j
            a = min([i;j]);
            b = max([i;j]);
            model.grid(a,b).dist = ds(j,:)'; 
        end
    end
    
    ds(i,:)=[];
    all_mu = model.all_mu;
    all_mu(i,1) = mean(ds*weight);
    %all_mu(i,1) = mean(mean(ds,2));    
    model.all_mu = all_mu;
    model.signatures(i) = s;
    model.mu = mean(all_mu);
    model.sigma = std(all_mu);
    model.max_mu = max(all_mu);
    model.weight = find_axis_weight(model, train_size);
    
end