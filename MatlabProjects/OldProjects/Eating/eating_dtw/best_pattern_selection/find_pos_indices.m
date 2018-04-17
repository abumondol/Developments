pos_indices = [];
for i = 1:36
    indices = distances(i).pos_pos(:, 2);
    indices = sort(unique(indices));
    sids = zeros(length(indices), 1) + i;
    pos_indices = [pos_indices; sids, indices];    
end
save('pos_indices', 'pos_indices');