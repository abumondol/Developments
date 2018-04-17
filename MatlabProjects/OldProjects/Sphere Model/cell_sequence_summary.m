function summary = cell_sequence_summary(cell_sequence)
    cell_types = unique(cell_sequence(:,1));
    len = length(cell_types);
    summary = zeros(len+1, 2);
    for i = 1:len
        cell = cell_types(i);
        summary(i, 1) = cell;
        summary(i,2) = sum(cell_sequence(cell_sequence(:,1)==cell, 6));        
    end
    
    if sum(summary(:,2)) ~= sum(cell_sequence(:,6))
        fprintf('\n\nXXXXXXXXXXXXX ERROR in Count in cell sequence summary %d, %d\n\n', sum(summary(i,2)), sum(cell_sequence(:,6)));
    end
    summary(len+1, 1) = 0;
    summary(len+1, 2) = sum(summary(:,2));    
    summary = sortrows(summary, -2);
end