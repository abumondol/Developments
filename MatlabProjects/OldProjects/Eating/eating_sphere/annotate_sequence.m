function res = annotate_sequence(sequence, annots)
    seq_count = size(sequence, 1);
    res = zeros(seq_count, 2);
    if isempty(annots)
        return
    end
    
    annot_count = size(annots, 1);
        
    for i = 1:annot_count
        a = annots(i, 1);
        ind = find(a>=sequence(:,2) & a<= sequence(:,3));
        if length(ind) ~= 1
            %fprintf('Error at index: %d, length of indices: %d\n', a, length(ind));            
            %return
            ind = ind(1);
        end
        res(ind, 1) = res(ind, 1) + 1;
        res(ind, 2) = annots(i, 2);
    end
end