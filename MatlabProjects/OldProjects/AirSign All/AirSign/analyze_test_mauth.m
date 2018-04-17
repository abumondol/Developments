function result = analyze_test(names, signatures, train_size, weighted, sigma_th, models_mauth)
    result = [];
	total_names = length(names);
    total_signatures = length(signatures);
        
    load('grid_mauth');   
     
    
    sign_allsub_index = [signatures.subject_index];
 
    for i = 1: total_names
    %for i = 1: 1
        templates_mu = models_mauth(i).mu;
        templates_sigma = models_mauth(i).sigma;
        
        sub_sign_index = find(sign_allsub_index==i);        

        templates = models_mauth(i).train_indices;
        last_template_index = max(templates);
        remain_sign_indices = sub_sign_index(sub_sign_index > last_template_index);
        
        remain_sign_count = length(remain_sign_indices);
        result =  zeros(remain_sign_count, 5);        
                
        for test_no = 1 : remain_sign_count
            
            if weighted
                pairs = nchoosek(templates, 2);
                weight = find_axis_weight(pairs, grid_mauth);
            else
                weight = ones(8,1)/8;
            end
            
            pairs =  nchoosek(templates, 2);
            d = zeros(length(pairs),1);
            for k = 1:length(pairs)
                a = pairs(k,1);
                b = pairs(k,2);
%                 if grid_mauth(a,b).status == 0                
%                     [dist, path]= mauth_distance(signatures(a), signatures(b));
%                     grid_mauth(a,b).dist = dist;
%                     grid_mauth(b,a).dist = dist;
%                     grid_mauth(a,b).status = 1;             
%                     grid_mauth(b,a).status = 1;               
%                 end

                x = grid_mauth(a,b).dist;
                d(k) = sum( x.*weight); 
            end
            
            templates_mu = mean(d);
            templates_sigma = std(d);
            
            %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
            
                        
            template_count = length(templates); 
            a = remain_sign_indices(test_no);
            d = zeros(template_count, 1);
            for j = 1:template_count
                b = templates(j);
                if grid_mauth(a,b).status == 0
                    %fprintf('Calculating mauth_distance for signatures %d %d\n',a,b);
                    [dist, path]= mauth_distance(signatures(a), signatures(b));
                    grid_mauth(a,b).dist = dist;
                    grid_mauth(b,a).dist = dist;
                    grid_mauth(a,b).status = 1;             
                    grid_mauth(b,a).status = 1;               
                end
                
                x = grid_mauth(a,b).dist;
                d(j) = sum(x.*weight); 
            end

            mu = mean(d);
            sigma_this = (mu - templates_mu)/templates_sigma;
            %result(test_no,1:2) = [sigma_this, mu];
           result(test_no,2:5) = [templates_mu, templates_sigma, mu, sigma_this];
            if sigma_this >  sigma_th                
                result(test_no,1) = 0;
            else                
                result(test_no,1) = 1;
%                 if template_count < train_size
%                    templates = [templates, a];
%                 elseif rand*100 >= 50
                    r = ceil(rand*template_count);
                    templates(r)=[];
                    templates = [templates, a];                    
%                 end
                
            end
            
                
        end
        
        %%%%%%%%%%%%%%%%% Test stat for the subject %%%%%%%%%%%%%%%%%
        %result
        fprintf('Subject %d: ',i);
        for k =1:6
            p = sum(result(:,5)<=(k-1))/remain_sign_count;
            p = round(p*100, 0);
            fprintf(' %d,',p);
        end
        
        p = sum(result(:,5) > 5 )/remain_sign_count;
        p = round(p*100, 0);
        fprintf(' %d :: %.2f %.2f\n',p, mean(result(:,2)), mean(result(:,3)));        
                
    end    % end subject for loop 
    
    save('grid_mauth','grid_mauth');
    
end
