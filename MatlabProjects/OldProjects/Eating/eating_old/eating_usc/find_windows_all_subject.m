sample_rate = 64;
load('data_usc');
data = data_usc;
sub_count = length(data);
min_th= -2.25;
min_length = 0.5*sample_rate;
window_length = 6*sample_rate;

segments_usc = [];
for sub = 1:sub_count
    d = data(sub).accel;
    [windows, total] = find_windows(d, min_th, min_length, window_length); 
    window_count = size(windows, 1);
    
    
    annots = data(sub).accel_annot_indices;
    annots = annots(annots(:,2) < 500, :);    
    annot_count = size(annots, 1);
    annot_window_count = zeros(annot_count, 1);
    
    window_annots = zeros(window_count, 4);            
    for i = 1:window_count       
        a = windows(i, 1);
        b = windows(i, 2);
        a = find(annots(:,1) >= a & annots(:,1) <= b);
        len = length(a);
        for j = 1:len
            window_annots(i, j) = a(j);
            annot_window_count(a(j), 1) = annot_window_count(a(j), 1) + 1;                
        end        
    end
    
    segments_usc(sub).total = total;
    segments_usc(sub).windows = windows;    
    segments_usc(sub).window_annots = window_annots;
    segments_usc(sub).window_annots_stat = sum(window_annots ~= 0);    
    
    a = annot_window_count;
    segments_usc(sub).annots = annots;    
    segments_usc(sub).annot_covered = sum(unique(window_annots(:))~=0);
    segments_usc(sub).annot_windows_stat = [sum(a==1), sum(a==2), sum(a==3)];
    
end

save('segments_usc','segments_usc');
