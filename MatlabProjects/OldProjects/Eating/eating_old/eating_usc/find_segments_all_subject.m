load('data_usc');
data = data_usc;
sub_count = length(data);
min_th= -2;
min_length = 0.5;
max_length = 6;
step_length = 0.1;

segments_usc = [];
for sub = 1:sub_count
    d = data(sub).grav;
    d(:, 2:4) = mysmooth(d(:,2:4), 0.8); 
    segments = find_segments(d, min_th, min_length, max_length, step_length); 
    segment_count = size(segments, 1);
    
    
    annots = data(sub).annots_adjusted;
    annots = annots(annots(:,2) < 400, :);    
    annot_indices = get_annot_indices(d, annots);
    
    annot_count = size(annots, 1);
    annot_segment_count = zeros(annot_count, 1);
    
    segment_annots = zeros(segment_count, 4);            
    for i = 1:segment_count       
        a = segments(i, 1);
        b = segments(i, 2);
        a = find(annot_indices(:,1) >= a & annot_indices(:,1) <= b);
        len = length(a);
        for j = 1:len
            segment_annots(i, j) = a(j);
            annot_segment_count(a(j), 1) = annot_segment_count(a(j), 1) + 1;                
        end        
    end    
    
    segments_usc(sub).segments = segments;    
    segments_usc(sub).segment_annots = segment_annots;
    segments_usc(sub).segment_annots_stat = sum(segment_annots ~= 0);    
    
    a = annot_segment_count;
    segments_usc(sub).annots = annots;    
    segments_usc(sub).annot_covered = sum(unique(segment_annots(:))~=0);
    segments_usc(sub).annot_segments_stat = [sum(a==1), sum(a==2), sum(a==3)];
    
end

save('segments_usc','segments_usc');
