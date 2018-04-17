function annot_indices = get_annot_indices(d, annots)
    annot_count = size(annots, 1);
    annot_indices = zeros(annot_count,2);
    t = d(:,1);
    t2 = [t(2:end); t(end)];
    for j =1:annot_count
        x = find(annots(j,1)>=t & annots(j,1)<t2);
        if isempty(x)
            fprintf('Error in annot indices: %d\n', j);            
        else
            annot_indices(j,1) = x;
        end
        annot_indices(j,2) = annots(j, 2);
    end
    
end