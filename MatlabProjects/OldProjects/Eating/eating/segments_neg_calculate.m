%load('data');
sub_count = length(data);
segments_neg = [];
segment_length = 6*16;
left = segment_length/2-1;
right = segment_length/2;

for sub = 1:sub_count
    fprintf('Subject: %d\n', sub);
    accel = data(sub).accel;
    annots = data(sub).annots;
    
    if isempty(annots)        
        accel_count = length(accel);
        res_count = length(segment_length/2:segment_length/2:accel_count-segment_length/2);
        edges = zeros(res_count, 3);
        min_x = zeros(res_count, 1);
        vars = zeros(res_count, 1);
        i = 1;
        for ind = segment_length/2:segment_length/2:accel_count-segment_length/2
            a = accel(ind-left:ind+right, 2:4);
            edges(i, :) = [ind, ind-left, ind+right];
            min_x(i, 1) = min(a(:,1));        
            vars(i, 1) = mean(var(a)); 
            i = i+1; 
        end
        
        if i~= res_count + 1
            fprintf('res_count and i doesnt match\n');
            break;
        end
        
        segments_neg(sub).edges = edges;
        segments_neg(sub).min_x = min_x;
        segments_neg(sub).vars = vars;        
        continue
    end
    
    edges = [];
    min_x = [];
    vars = [];
    annots = annots(annots(:,2)<1000, :);    
    annot_indices = get_annot_indices(accel, annots);
    annot_count = length(annots);
    accel_count = length(accel);
    
    ind = segment_length/2;
    while ind < accel_count-segment_length/2
        l = ind - segment_length-1;
        r = ind + segment_length;              
        if isempty(find(annot_indices(:,1)>l &  annot_indices(:,1)<r))
            a = accel(ind-left:ind+right, 2:4);
            edges = [edges; ind, ind-left, ind+right];
            min_x = [min_x; min(a(:,1))];        
            vars = [vars; mean(var(a))]; 
            ind = ind + segment_length/2;
        else
            ind = ind + 1;
        end
        
    end
    
    segments_neg(sub).edges = edges;
    segments_neg(sub).min_x = min_x;
    segments_neg(sub).vars = vars;    
end

save('segments_neg', 'segments_neg');