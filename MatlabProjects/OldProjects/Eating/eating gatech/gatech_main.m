load('data','data');
p = get_parameters()
features = [];
close all

for i = 1:5    
    a = data(i).acl_smooth;    
    t = data(i).time_stamps;
    acl_annots = data(i).acl_annots;
    
    chunks = get_chunks(a, t, p);    
    chunks = filter_chunks(chunks, p);
    counts = annot_count_in_segments(chunks, acl_annots);       
    fprintf('Subect %d: %d, %d, %d\n', i, size(chunks,1), data(i).pos_annot_count, sum(counts));
    
    windows = get_windows_by_chunks(a, t, chunks); 
    counts = annot_count_in_segments(windows, acl_annots);
    windows = [windows, counts];   
    fprintf('Subect %d: %d, %d, %d\n\n', i, size(windows,1), data(i).pos_annot_count, sum(counts));
    %plot_data(a, windows);
    
    f = get_features_all_windows(a, t, windows);
    sub_id = i * ones(size(f,1), 1); 
    features = [features; sub_id, f];    
end

csvwrite('features.csv', features);
save('features', 'features');


