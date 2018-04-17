[window_length, annot_type, free_length, step_size, x_th, var_th] = get_settings()
sub_count = length(data);

pos_data = [];
neg_data = [];
l = window_length/2-1;
r = window_length/2;

for sub = 1:sub_count
    fprintf('Subject %d\n', sub);
    accel = data(sub).accel;
    annots = data(sub).annots_adjusted;
    annots = get_annot_indices(accel, annots);
    
    accel= accel(:, 2:4);
    accel_count = length(accel);
    if ~isempty(annots)
        annots = annots(annots(:,2)<1000, :);
    end
    
    for ind = window_length/2:step_size:accel_count-window_length/2       
        if isempty(annots) || isempty(find(annots(:,1)> ind-l &  annots(:,1)<ind+r))
            d = accel(ind-l:ind+r, :);
            m = min(d(:, 1));
            v = sum(var(d));            
            if m <= x_th && v>=var_th                
                neg_data = [neg_data; get_features(d), 0, sub];
            end
        end
    end
    
    if isempty(annots)
        continue
    end
    
    annot_bites = annots(annots(:,2)<400, :);
    annot_drink = annots(annots(:,2)>=400 & annots(:,2)<1000, :);
    annot_count = length(annot_bites);
    
    for i = 1:annot_count
        a = annot_bites(i, 1);
        for ind = a:a
            d = accel(ind-window_length/2+1:ind+window_length/2, :);
            m = min(d(:, 1));
            v = sum(var(d));            
            if m <= x_th && v>=var_th                
                pos_data = [pos_data; get_features(d), 1, sub];
            end
            
        end
    end   
       
end

train_data = [pos_data; neg_data];
save('train_data', 'train_data');
