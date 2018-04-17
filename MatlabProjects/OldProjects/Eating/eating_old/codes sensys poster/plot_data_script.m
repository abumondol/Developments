load('data','data');
st = 100;
duration = 195;
sub = 2;
sess = 1;

annots = data(sub).session(sess).annotations;
annots = annots(annots(:,1)>=st & annots(:,1)<st+duration, :);

d  = data(sub).session(sess).accel;
len = length(d);
d = d(1:4:len, :);
accel = d(d(:,1) >= st & d(:,1)<st+duration, :);
accel(:,2:4) = smooth(accel(:,2:4), 0.8);

d  = data(sub).session(sess).gyro;
len = length(d);
d = d(1:4:len, :);
gyro = d(d(:,1) >= st & d(:,1)<st+duration, :);
gyro(:,2:4) = gyro(:,2:4)*57.2958; %rad to degree
gyro(:,2:4) = smooth(gyro(:,2:4), 0.8);
gyro = [gyro, get_magnitude(gyro(:,2:4))]; 

plot_data_annot_points_with_time(accel, annots, 50);
plot_data_annot_points_with_time(gyro, annots, 1000);
return


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


