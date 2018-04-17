function models_mauth = analyze_train_mauth(names, signatures, train_size, weighted, sigma_th)
    
	total_names = length(names);
    total_signatures = length(signatures);  
    
    if exist('grid_mauth.mat','file') == 2
        %fprintf('grid_mauth exists\n');
        load('grid_mauth');   
    else
        %fprintf('grid_mauth doesnt exist. creating a new one.\n');
        grid_mauth = [];        
        for i = 1:total_signatures
            for j = 1:total_signatures
                grid_mauth(i,j).status = 0;
            end
        end        
    end
    
        
    models_mauth=[];    
    sign_allsub_index = [signatures.subject_index];
 
    for i = 1: total_names
        sub_sign_index = find(sign_allsub_index==i);
        %fprintf('Training, subject id: %d, total sign: %d ',i,length(sub_sign_index));

        models_mauth(i).start_index = sub_sign_index(1);
        train_indices = sub_sign_index(1:train_size);
        remain_sign_indices = sub_sign_index(train_size+1:end);
        accepted = false;
        added = 0; 
        
        while accepted==false && added < length(remain_sign_indices)        
            pairs = nchoosek(train_indices,2);
            for j=1:length(pairs)                
                a = pairs(j,1);
                b = pairs(j,2);
                if grid_mauth(a,b).status == 0
                    %fprintf('Calculating mauth_distance for signatures %d %d\n',a,b);
                    [dist, path]= mauth_distance(signatures(a), signatures(b));
                    grid_mauth(a,b).dist = dist;
                    grid_mauth(b,a).dist = dist;
                    grid_mauth(a,b).status = 1;             
                    grid_mauth(b,a).status = 1;               
                end                
            end

            max_mu = 0;
            max_mu_index = 0;
            for j = 1:train_size
                templates = train_indices;
                templates(j)=[];            
                pairs = nchoosek(templates, 2);
                if weighted
                    weight = find_axis_weight(pairs, grid_mauth);
                else
                    weight = ones(8,1)/8;
                end

                d = zeros(length(pairs),1);
                for k = 1:length(pairs)
                    a = pairs(k,1);
                    b = pairs(k,2);  
                    x = grid_mauth(a,b).dist;
                    d(k) = sum( x.*weight);                
                end

                mu_others = mean(d);
                sigma_others = std(d);

                a = train_indices(j); %index of this sign in train indices
                pairs = zeros(length(templates),2);
                d = zeros(length(templates),1);
                for k = 1:length(templates)
                    b = templates(k); 
                    x = grid_mauth(a,b).dist;
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
        
        models_mauth(i).train_indices = train_indices;                
        x = train_indices(train_size) - models_mauth(i).start_index + 1;
        models_mauth(i).train_count = x;        
        %fprintf('  train sign required: %d\n', x);
        
        pairs =  nchoosek(train_indices, 2);
        d = zeros(length(pairs),1);
        for k = 1:length(pairs)
            a = pairs(k,1);
            b = pairs(k,2);
            if grid_mauth(a,b).status == 0                
                [dist, path]= mauth_distance(signatures(a), signatures(b));
                grid_mauth(a,b).dist = dist;
                grid_mauth(b,a).dist = dist;
                grid_mauth(a,b).status = 1;             
                grid_mauth(b,a).status = 1;               
            end      
            
            x = grid_mauth(a,b).dist;
            d(k) = sum( x.*weight); 
        end
        
        models_mauth(i).mu = mean(d);
        models_mauth(i).sigma = std(d);
        models_mauth(i).sigma_th = sigma_th;        
        models_mauth(i).weighted = weighted;
        models_mauth(i).train_size = train_size;
        
    end    % end subject for loop 
    
    save('grid_mauth','grid_mauth');
    %save('models_mauth','models_mauth');
    %train_stat(models_mauth);
    %fprintf('Analyzing train done\n\n');
end
