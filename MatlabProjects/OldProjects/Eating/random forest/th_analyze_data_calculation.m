sub_count = length(data);

th_pos_data = [];
th_neg_data = [];

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
    
    for ind = 48:16:accel_count-48
        l = ind-47;
        r = ind+48;
        if isempty(annots) || isempty(find(annots(:,1)> l &  annots(:,1)<r))
            m = min(accel(ind-15:ind+16, 1));
            s = var(accel(ind-31:ind+32, :));
            s = sum(s);
            th_neg_data = [th_neg_data; m, s];
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
        for ind = a-8:8:a+8
            m = min(accel(ind-15:ind+16, 1));
            s = var(accel(ind-31:ind+32, :));
            s = sum(s);
            th_pos_data = [th_pos_data; m, s];
        end
    end   
       
end
