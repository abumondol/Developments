sub_count = 52; %length(data);
%zmin = -7;
%zmax = 7;
%var_min = 1;

segments_for_pat = [];
for s = 1:sub_count    
    X = features(s).X;    
    Y = features(s).Y;
    
    %segs = segments(s).segs_selected;    
    cond = X(:, 1)>=xmin & X(:, 2)<=ymax & X(:, 3)>=zmin & X(:, 3)<=zmax & X(:, 8)>var_min;
    segs = segments(s).segs_selected(cond, :);    
    
    segments_for_pat(s).segments = segs;
    segments_for_pat(s).segspos = segs(segs(:,3)>0, :);
    segments_for_pat(s).segsneg = segs(segs(:,3)==0, :);
    
    segments_for_pat(s).gt_eat_count = segments(s).gt_eat_count;
    segments_for_pat(s).eat_count = sum(segs(:,3)>0);
    segments_for_pat(s).eat_lost = segments_for_pat(s).gt_eat_count - segments_for_pat(s).eat_count;
    segments_for_pat(s).neg_count = sum(segs(:,3)==0);    
end
save('segments_for_pat', 'segments_for_pat');

