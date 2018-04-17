sub_count = length(segments);
offset = 24;

for sub = 1:sub_count
    fprintf('Subject: %d\n', sub);
    seg = segments(sub).segments ;
    a = segments(sub).annot_indices;
    a = a(a(:, 2) <400, :);
    
    seg_count = size(seg, 1);
    annot_count = size(a, 1);
    
    gt = zeros(seg_count, 1);
    dist = zeros(seg_count, 2);
    annot_covered = zeros(annot_count, 1);
    
    for i = 1:seg_count
        ind = seg(i, 1);
        s = find(a(:,1)>= ind-offset & a(:,1)<=ind+offset);
        gt(i) = length(s);        
        annot_covered(s) = annot_covered(s) + 1;
        
        if length(s) >= 1
            dist(i,1) = a(s(1)) - ind;
            if length(s) >=2
                dist(i,2) = a(s(2)) - ind;
            end
        end
    end    
        
    segments(sub).segments = [seg, gt];
    %segments(sub).distances = dist;
    segments(sub).annot_covered = [a, annot_covered];
    segments(sub).annot_covered_count = sum(annot_covered==1);
    segments(sub).annot_covered_count_2 = sum(annot_covered>=1);
end

save('segments', 'segments');
