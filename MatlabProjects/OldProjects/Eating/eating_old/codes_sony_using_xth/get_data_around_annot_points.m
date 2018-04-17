function res = get_data_around_annot_points(data, annot_indices, before, after)   
    count = length(annot_indices);
    res = [];
    
    for i = 1:count
        t = data(annot_indices(i,1), 1);
        d = data(data(:,1) >= t-before & data(:,1) <= t + after, 2:4);
        res = [res;d];
    end    
end
