sub_count = length(data)
sequence = [];

ico_number = 4;
for s= 1:sub_count
    fprintf('Subject %d\n', s);
    accel = data(s).accel;
    annots = data(s).annots;
    
    if ~isempty(annots)
        annots = annots(annots(:,2)<1000, :);    
        annots = get_annot_indices(accel, annots);
    end
    
    t = accel(:, 1);
    accel = my_normalization(accel(:, 2:4));            
    [cell_assignment, cell_sequence] = cell_assignment_and_sequence(t, accel, ico, ico_number);
    a = annotate_sequence(cell_sequence, annots);
    
    sequence(s).seq = [cell_sequence, a];
end

save('sequence', 'sequence');