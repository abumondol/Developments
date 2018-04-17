clear; load('oppdata');

actchunks = cell(4, 6);
for sub = 1:4    
    for sess = 1:6
        labels = oppdata{sub,sess}.labels;       
             
        count = length(labels);            
        
        chunks_loco = [];
        start_index_loco = 1;
        activity_loco = labels(1, 1);
        
        chunks_hl = [];
        start_index_hl = 1;
        activity_hl = labels(1, 1);        
        
        chunks_ml = [];            
        start_index_ml = 1;
        activity_ml = labels(1, 7);        
        
        chunks_ll = [];            
        start_index_ll = 1;
        activity_ll_1 = labels(1, 3);        
        activity_ll_2 = labels(1, 4);        
        
        chunks_lr = [];            
        start_index_lr = 1;
        activity_lr_1 = labels(1, 5);        
        activity_lr_2 = labels(1, 6);        
        
        for i =2:count
            
            %%%%%%%%%%%%%%%%% for locomotion %%%%%%%%%%%%%%%
            if labels(i, 1) ~= activity_loco
                chunks_loco = [chunks_loco; start_index_loco, i-1,  activity_loco];                
                start_index_loco = i;
                activity_loco = labels(i, 1);
            end
            
            %%%%%%%%%%%%%%%%% for high level activity %%%%%%%%%%%%%%%
            if labels(i, 2) ~= activity_hl
                chunks_hl = [chunks_hl; start_index_hl, i-1,  activity_hl];                
                start_index_hl = i;
                activity_hl = labels(i, 2);                
            end            
            
             %%%%%%%%%%%%%%%%% for midlevel activity %%%%%%%%%%%%%%%
            if labels(i, 7) ~= activity_ml
                left = 0;
                right = 0;                
                if sum(labels(start_index_ml:i-1, 3))~=0
                    left = 1;
                end
                
                if sum(labels(start_index_ml:i-1, 5))~=0
                    right = 1;
                end                
                
                chunks_ml = [chunks_ml; start_index_ml, i-1, activity_ml, left, right];
                start_index_ml = i;
                activity_ml = labels(i, 7);                                
            end
            
            %%%%%%%%%%%%%%%%% for left hand low level %%%%%%%%%%%%%%%
            if labels(i, 3) ~= activity_ll_1 || labels(i, 4) ~= activity_ll_2                               
                chunks_ll = [chunks_ll; start_index_ll, i-1,  activity_ll_1, activity_ll_2];                
                start_index_ll = i;
                activity_ll_1 = labels(i, 3); 
                activity_ll_2 = labels(i, 4);                                
            end
            
            %%%%%%%%%%%%%%%%% for right hand low level %%%%%%%%%%%%%%%
            if labels(i, 5) ~= activity_lr_1 || labels(i, 6) ~= activity_lr_2                
                chunks_lr = [chunks_lr; start_index_lr, i-1,  activity_lr_1, activity_lr_2];                
                start_index_lr = i;
                activity_lr_1 = labels(i, 5); 
                activity_lr_2 = labels(i, 6);                                
            end
            
        end
        
        chunks_loco = [chunks_loco; start_index_loco, count,  activity_loco];
        actchunks{sub,sess}.chunks_loco = chunks_loco;
        
        chunks_hl = [chunks_hl; start_index_hl, count,  activity_hl];
        actchunks{sub,sess}.chunks_hl = chunks_hl;        
        
        left = 0;
        right = 0;
        if sum(labels(start_index_ml:count, 3))~=0
            left = 1;
        end
        
        if sum(labels(start_index_ml:count, 5))~=0
            right = 1;
        end

        chunks_ml = [chunks_ml;  start_index_ml, count, activity_ml, left, right];
        actchunks{sub,sess}.chunks_ml = chunks_ml;
        
        chunks_ll = [chunks_ll; start_index_ll, count,  activity_ll_1, activity_ll_2];        
        actchunks{sub,sess}.chunks_ll = chunks_ll;
        
        chunks_lr = [chunks_lr; start_index_lr, count,  activity_lr_1, activity_lr_2];
        actchunks{sub,sess}.chunks_lr = chunks_lr;        
        
    end
end

save('actchunks', 'actchunks');
