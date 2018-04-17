function result = analyze_train(names, signatures, train_size, weighted, sigma_th)
    result = true;
	total_names = length(names);
    total_signatures = length(signatures);
    
    if exist('grid_airsign.mat','file')==2
        %fprintf('grid_airsign exists\n');
        load('grid_airsign');   
    else
        %fprintf('grid-airsign doesnt exists. creating new one.\n');
        grid_airsign = [];        
        for i = 1:total_signatures
            for j = 1:total_signatures
                grid_airsign(i,j).status = 0;
            end
        end        
    end
        
    models_airsign=[];    
    sign_allsub_index = [signatures.subject_index];
 
    for i = 1: total_names
        sub_sign_index = find(sign_allsub_index==i);
        %fprintf('Training, subject id: %d, total sign: %d ',i,length(sub_sign_index));

        models_airsign(i).start_index = sub_sign_index(1);
        train_indices = sub_sign_index(1:train_size);
        remain_sign_indices = sub_sign_index(train_size+1:end);
        accepted = false;
        added = 0; 
        
        while accepted==false && added < length(remain_sign_indices)        
            pairs = nchoosek(train_indices,2);
            for j=1:length(pairs)                
                a = pairs(j,1);
                b = pairs(j,2);
                if grid_airsign(a,b).status == 0
                    %fprintf('Calculating airsign_distance for signatures %d %d\n',a,b);
                    [dist, path]= airsign_distance(signatures(a), signatures(b));
                    grid_airsign(a,b).dist = dist;
                    grid_airsign(b,a).dist = dist;
                    grid_airsign(a,b).status = 1;             
                    grid_airsign(b,a).status = 1;               
                end                
            end

            max_mu = 0;
            max_mu_index = 0;
            for j = 1:train_size
                templates = train_indices;
                templates(j)=[];            
                pairs = nchoosek(templates, 2);
                if weighted
                    weight = find_axis_weight(pairs, grid_airsign);
                else
                    weight = ones(8,1)/8;
                end

                d = zeros(length(pairs),1);
                for k = 1:length(pairs)
                    a = pairs(k,1);
                    b = pairs(k,2);  
                    x = grid_airsign(a,b).dist;
                    d(k) = sum( x.*weight);                
                end

                mu_others = mean(d);
                sigma_others = std(d);

                a = train_indices(j); %index of this sign in train indices
                pairs = zeros(length(templates),2);
                d = zeros(length(templates),1);
                for k = 1:length(templates)
                    b = templates(k); 
                    x = grid_airsign(a,b).dist;
                    d(k) = sum( x.*weight);                
                end

                mu = mean(d);
                if mu > mu_others + sigma_th*sigma_others && mu > max_mu                    
                    max_mu = mu;
                    max_mu_index = a;
                    replace_index_in_train = j;
                end
            end
            
            if max_mu_index == 0
                accepted = true;
            else
                added = added + 1;
                train_indices(replace_index_in_train) = [];
                train_indices(train_size) = remain_sign_indices(added);
            end        
        end % End of WHILE
        
        models_airsign(i).train_indices = train_indices;                
        x = train_indices(train_size) - models_airsign(i).start_index + 1;
        models_airsign(i).train_count = x;        
        %fprintf('  train sign required: %d\n', x);
        
        pairs =  nchoosek(train_indices, 2);
        d = zeros(length(pairs),1);
        for k = 1:length(pairs)
            a = pairs(k,1);
            b = pairs(k,2);
            if grid_airsign(a,b).status == 0                
                [dist, path]= airsign_distance(signatures(a), signatures(b));
                grid_airsign(a,b).dist = dist;
                grid_airsign(b,a).dist = dist;
                grid_airsign(a,b).status = 1;             
                grid_airsign(b,a).status = 1;               
            end      
            
            x = grid_airsign(a,b).dist;
            d(k) = sum( x.*weight); 
        end
        
        models_airsign(i).mu = mean(d);
        models_airsign(i).sigma = std(d);
        models_airsign(i).sigma_th = sigma_th;        
        models_airsign(i).weighted = weighted; 
        models_airsign(i).train_size = train_size;
        
    end    % end subject for loop 
    
    save('grid_airsign','grid_airsign');
    %save('models_airsign','models_airsign');
    train_stat(models_airsign);
    %fprintf('Analyzing train done\n\n');
end
