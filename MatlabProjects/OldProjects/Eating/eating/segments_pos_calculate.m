[window_length, left, right, slide_length] = get_settings();
%load('data');
sub_count = length(data);
segments_pos = [];
segment_length = 6*16;
left = segment_length/2-1;
right = segment_length/2;

for sub = 1:sub_count
    fprintf('Subject: %d\n', sub);
    accel = data(sub).accel;
    annots = data(sub).annots;
    if isempty(annots)
        break
    end
    
    annots = annots(annots(:,2)<400, :);    
    annot_indices = get_annot_indices(accel, annots);
    annot_count = length(annots);
    
    %w = zeros(annot_count, segment_length*3);    
    edges = zeros(annot_count, 3);
    min_x = zeros(annot_count, 1);
    vars = zeros(annot_count, 1);
    for i = 1:annot_count
        ind = annot_indices(i,1);
        a = accel(ind-left:ind+right, 2:4);        
        edges(i, :) = [ind, ind-left, ind+right];
        min_x(i, 1) = min(a(:,1));        
        vars(i, 1) = mean(var(a));        
        %a = a(:);
        %w(i, :) = a';
    end
    %segments_pos(sub).segments = w;
    
    segments_pos(sub).edges = edges;
    segments_pos(sub).min_x = min_x;
    segments_pos(sub).vars = vars;
end

save('segments_pos', 'segments_pos');