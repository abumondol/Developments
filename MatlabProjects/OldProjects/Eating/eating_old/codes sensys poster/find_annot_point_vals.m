load('data');
before = 3;
after = 3;
res = [];
 
for sub = 1:5
    annot_val_sub = [];
    window_data_sub = [];
    for sess = 1:2
        d = data(sub).session(sess).grav;
        annot_indices = data(sub).session(sess).grav_annot_indices;                        
        annot_indices = annot_indices(annot_indices(:,2)>0 & annot_indices(:,2)<200, :); % only eating annot
        
        annot_val_sess = [d(annot_indices(:,1), :), annot_indices(:,2)]; 
        annot_val_sub = [annot_val_sub; annot_val_sess];
        
        %window_data_sess = get_data_around_annot_points(d, annot_indices, before, after);
        %window_data_sub = [window_data_sub; window_data_sess];        
    end
     
    res(sub).annot_point_vals = annot_val_sub;
    %res(sub).window_data = window_data_sub;
    
    fprintf('%d, %d, %d\n', sub, min(annot_val_sub(:,2)), max(annot_val_sub(:,2)));
    
end

save('res','res');