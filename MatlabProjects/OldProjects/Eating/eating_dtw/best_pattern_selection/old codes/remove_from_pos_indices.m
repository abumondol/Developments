function pos_indices=remove_from_pos_indices(pos_indices, remove_list)    
    remove_count = size(remove_list, 1);
    ix = [];
    for i=1:remove_count        
        s = remove_list(i,1);
        rix = remove_list(i,2);
        ix = [ix; find(pos_indices(:,1)==s & pos_indices(:,2)==rix)];
    end
    
    fprintf('Remove count from pos indices: %d\n', size(ix, 1));
    pos_indices(ix, :) = [];
end