function transformation(names, signatures)   
	total_names = length(names);
    %total_signatures = length(signatures);
    
    sub_data = [];
    load('grid_airsign');
    load('grid_mauth');

    sign_allsub_index = [signatures.subject_index];
 
    for i = 1: total_names
       
        sub_sign_index = find(sign_allsub_index==i);        
        sign_count = length(sub_sign_index);
        
        min_count = min(sign_count, 75);
        fprintf('Processing subject: %d, sign_count:%d\n', i, min_count);
        sub_data(i).name = names(i);
        sub_data(i).sign_count = min_count;
        for j=1:min_count
            sign_index = sub_sign_index(j);
            sub_data(i).sign(j) = signatures(sign_index);
        end
        
        
        for j=1:min_count
            for k=1:min_count
                sub_data(i).grid_type(1).dist_grid(j,k).status = 0;
                sub_data(i).grid_type(2).dist_grid(j,k).status = 0;
            end
        end
        
    end
    
    save('sub_data','sub_data');    
end
        
    
        
        
        
